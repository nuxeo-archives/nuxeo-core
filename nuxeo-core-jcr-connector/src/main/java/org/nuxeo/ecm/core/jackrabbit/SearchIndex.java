/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.core.jackrabbit;

import java.util.Set;

import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.query.lucene.FieldNames;
import org.apache.jackrabbit.core.query.lucene.IndexFormatVersion;
import org.apache.jackrabbit.core.query.lucene.NamePathResolverImpl;
import org.apache.jackrabbit.core.query.lucene.NamespaceMappings;
import org.apache.jackrabbit.core.state.ItemStateException;
import org.apache.jackrabbit.core.state.ItemStateManager;
import org.apache.jackrabbit.core.state.NoSuchItemStateException;
import org.apache.jackrabbit.core.state.NodeState;
import org.apache.jackrabbit.core.state.PropertyState;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.nuxeo.ecm.core.repository.jcr.NodeConstants;
import org.nuxeo.ecm.core.repository.jcr.TypeAdapter;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.api.Framework;
import org.w3c.dom.traversal.DocumentTraversal;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SearchIndex extends
org.apache.jackrabbit.core.query.lucene.SearchIndex {


    private static SchemaManager mgr = Framework.getLocalService(SchemaManager.class);




    @Override
    protected Document createDocument(NodeState node,
            NamespaceMappings nsMappings, IndexFormatVersion indexFormatVersion)
    throws RepositoryException {


        Document doc = null;
        if (node.getNodeTypeName().equals(NodeConstants.ECM_NT_DOCUMENT_PROXY.qname)) {
            doc = createProxyDocument(node, nsMappings, indexFormatVersion);
        } else {
            doc = super.createDocument(node, nsMappings, indexFormatVersion);
        }
        NamePathResolver resolver = NamePathResolverImpl.create(nsMappings);
        if ( node.getNodeTypeName().getNamespaceURI().equals(NodeConstants.NS_ECM_DOCS_URI)) {
            addFacets(resolver, indexFormatVersion, node, doc);
            addParent(resolver, indexFormatVersion, node, doc);
        }
        return doc;
    }

    Set<String> getFacets(NodeState  node) {
        Name name = node.getNodeTypeName();
        String typeName = name.getLocalName();
        DocumentType dt =  mgr.getDocumentType(typeName);
        return dt == null ? null : dt.getFacets();
    }

    private void addFacets(NamePathResolver resolver, IndexFormatVersion indexFormatVersion, NodeState node, Document doc) {
        try {
            Name name = NodeConstants.ECM_MIXIN_TYPE.qname;
            String propName = resolver.getJCRName(name);
            if (indexFormatVersion.getVersion()
                    >= IndexFormatVersion.V2.getVersion()) {
                String fieldName = name.getLocalName();
                try {
                    fieldName = resolver.getJCRName(name);
                } catch (NamespaceException e) {
                    // will never happen
                }
                doc.add(new Field(FieldNames.PROPERTIES_SET, fieldName, Field.Store.NO, Field.Index.NO_NORMS));
            }
            Set<String> facets = getFacets(node);
            if (facets != null ) {
                for (String facet : facets) {
                    Field field = new Field(FieldNames.PROPERTIES,
                            FieldNames.createNamedValue(propName, facet),
                            Field.Store.NO, Field.Index.NO_NORMS,
                            Field.TermVector.NO);
                    doc.add(field);
                }
                doc.add(new Field(FieldNames.MVP, propName, Field.Store.NO, Field.Index.UN_TOKENIZED, Field.TermVector.NO));
            }
        } catch (NamespaceException e) {
            // will never happen, prefixes are created dynamically
        }
    }
    
    private void addParent(NamePathResolver resolver, IndexFormatVersion indexFormatVersion, NodeState node, Document doc) {
        Name name = NodeConstants.ECM_PARENT_ID.qname;
        try {
            String propName = resolver.getJCRName(name);
            ItemStateManager stateMgr = getContext().getItemStateManager();
            
            NodeId parentId = node.getParentId();
            if ( parentId == null ) {
                return ;
            }
            NodeState parent = (NodeState) stateMgr.getItemState(parentId);
            
            parentId = parent.getParentId();
            if ( parentId == null ) {
                return ;
            }
            parent = (NodeState) stateMgr.getItemState(parentId);
            
            Field field = new Field(FieldNames.PROPERTIES,
                    FieldNames.createNamedValue(propName, parent.getId().toString()),
                    Field.Store.NO, Field.Index.NO_NORMS,
                    Field.TermVector.NO);
            doc.add(field);
        } catch (NamespaceException e) {
            // will never happen, prefixes are created dynamically
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    



    protected Document createProxyDocument(NodeState node,
            NamespaceMappings nsMappings,
            IndexFormatVersion indexFormatVersion) throws RepositoryException {
        PropertyId id = new PropertyId(node.getNodeId(), NodeConstants.ECM_REF_FROZEN_NODE.qname);
        ItemStateManager stateMgr = getContext().getItemStateManager();
        NodeState versionNode = null;
        try {
            PropertyState ps = (PropertyState) stateMgr.getItemState(id);
            versionNode = (NodeState) stateMgr.getItemState(new NodeId(ps.getValues()[0].getUUID()));
        } catch (ItemStateException e) {
            throw new RepositoryException("No item state: "+id, e);
        }
        // index the version node
        Document doc = super.createDocument(versionNode, nsMappings, indexFormatVersion);

        // replace UUID and parent
        doc.removeField(FieldNames.UUID);
        doc.removeField(FieldNames.PARENT);
        doc.add(new Field(FieldNames.UUID, node.getNodeId().getUUID().toString(),
                Field.Store.YES, Field.Index.NO_NORMS, Field.TermVector.NO));
        doc.add(new Field(FieldNames.PARENT, node.getParentId().toString(),
                Field.Store.YES, Field.Index.NO_NORMS, Field.TermVector.NO));

        // TODO replace label too?
        try {
            doc.removeField(FieldNames.LABEL);
            NameResolver resolver = NamePathResolverImpl.create(nsMappings);
            NodeState parent = (NodeState) stateMgr.getItemState(node.getParentId());
            NodeState.ChildNodeEntry child = parent.getChildNodeEntry(node.getNodeId());
            if (child == null) {
                // this can only happen when jackrabbit
                // is running in a cluster.
                throw new RepositoryException("Missing child node entry " +
                        "for node with id: " + node.getNodeId());
            }
            String name = resolver.getJCRName(child.getName());
            doc.add(new Field(FieldNames.LABEL, name,
                    Field.Store.NO, Field.Index.NO_NORMS, Field.TermVector.NO));
        } catch (ItemStateException e) {
            e.printStackTrace();
        }

        // supported only in lucene 2.3.0
        // field.setValue(value);
        return doc;
    }

}
