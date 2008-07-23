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

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.query.lucene.FieldNames;
import org.apache.jackrabbit.core.query.lucene.IndexFormatVersion;
import org.apache.jackrabbit.core.query.lucene.NamePathResolverImpl;
import org.apache.jackrabbit.core.query.lucene.NamespaceMappings;
import org.apache.jackrabbit.core.state.ItemStateException;
import org.apache.jackrabbit.core.state.ItemStateManager;
import org.apache.jackrabbit.core.state.NodeState;
import org.apache.jackrabbit.core.state.PropertyState;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.nuxeo.ecm.core.repository.jcr.NodeConstants;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SearchIndex extends
        org.apache.jackrabbit.core.query.lucene.SearchIndex {

    @Override
    protected Document createDocument(NodeState node,
            NamespaceMappings nsMappings, IndexFormatVersion indexFormatVersion)
            throws RepositoryException {

        if (node.getNodeTypeName().equals(NodeConstants.ECM_NT_DOCUMENT_PROXY.qname)) {
            return createProxyDocument(node, nsMappings, indexFormatVersion);
        } else {
            return super.createDocument(node, nsMappings, indexFormatVersion);
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
