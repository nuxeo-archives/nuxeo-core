/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */
package org.nuxeo.ecm.core.api.pagination.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.EmptyPages;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.PaginationException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Reference implementation for the PaginationService interface
 * 
 * This service host PaginationFactory instance to build paginated list of
 * elements typically as a result of a persistent service query such as the Core
 * repository, the worklfow engine, the directories, ...
 * 
 * @author ogrisel
 */
public class PaginationServiceImpl extends DefaultComponent implements
        PaginationService {

    private static final Log log = LogFactory.getLog(PaginationServiceImpl.class);

    protected List<PaginationFactoryDescriptor> descriptors;

    protected Map<String, PaginationFactory> factories;

    //
    // OSGi component API
    //

    @Override
    public void activate(ComponentContext context) {
        descriptors = new ArrayList<PaginationFactoryDescriptor>();
        factories = null;
    }

    @Override
    public void deactivate(ComponentContext context) {
        descriptors = null;
        factories = null;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {

        PaginationFactoryDescriptor descriptor = (PaginationFactoryDescriptor) contribution;

        // use the classloading context of the contributor to instantiate the
        // factory instance of the descriptor
        descriptor.buildInstance(contributor.getContext());
        descriptors.add(descriptor);
        log.debug("registered PaginatorFactoryDescriptor: "
                + descriptor.getName());

        // invalidate the cached merged map
        factories = null;
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {

        PaginationFactoryDescriptor descriptor = (PaginationFactoryDescriptor) contribution;
        int lastIndex = descriptors.lastIndexOf(descriptor);
        if (lastIndex >= 0) {
            descriptors.remove(lastIndex);
            log.debug("unregistered PaginatorFactoryDescriptor: "
                    + descriptor.getName());
            // invalidate the cached merged map
            factories = null;
        } else {
            log.warn("failed to unregister PaginatorFactoryDescriptor: no such descriptor "
                    + descriptor.getName());
        }
    }

    protected Map<String, PaginationFactory> getFactories() {
        if (factories == null) {
            // lazy computation of the merged map of factory instances from the
            // list of descriptors]
            factories = new HashMap<String, PaginationFactory>();
            for (PaginationFactoryDescriptor descriptor : descriptors) {
                factories.put(descriptor.getName(),
                        descriptor.getFactoryInstance());
            }
        }
        return factories;
    }

    //
    // PaginationService API
    //

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            Map<String, Object> context) throws PaginationException {
        return getPages(paginatorName, sortInfo, -1, context);
    }

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Object> context)
            throws PaginationException {
        PaginationFactory factory = getFactories().get(paginatorName);
        if (factory == null) {
            throw new PaginationException(
                    "could not find registered factory for Pages with name "
                            + paginatorName);
        }
        return factory.getPages(paginatorName, sortInfo, pageSize, context);
    }

    public <E> Pages<E> getEmptyPages(String paginatorName)
            throws PaginationException {
        if (!getFactories().containsKey(paginatorName)) {
            throw new PaginationException(
                    "could not find registered factory for Pages with name "
                            + paginatorName);
        }
        return new EmptyPages<E>(paginatorName);
    }

}
