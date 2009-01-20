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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.runtime.model.RuntimeContext;

@XObject(value = "paginationFactory")
public class PaginationFactoryDescriptor {

    @XNode("@name")
    protected String name;

    @XNode("@factoryClass")
    protected String factoryClass;

    @XNode("@pageSize")
    protected int pageSize = 30;

    @XNode("@selectable")
    protected boolean selectable = false;
    
    @XNode("@sortable")
    protected boolean sortable = false;

    @XNodeMap(value = "sortCriterion", key = "@name", type = LinkedHashMap.class, componentType = Boolean.class)
    protected LinkedHashMap<String, Boolean> sortCriteria;

    @XNodeMap(value = "parameter", key = "@name", type = LinkedHashMap.class, componentType = String.class)
    protected Map<String, String> parameters;

    protected PaginationFactory instance;

    public String getName() {
        return name;
    }

    /**
     * Used for containment tests in unregisterContribution method
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PaginationFactoryDescriptor) {
            PaginationFactoryDescriptor other = (PaginationFactoryDescriptor) o;
            if (name == null) {
                return other.getName() == null;
            } else {
                return name.equals(other.getName());
            }
        }
        return false;
    }

    public void buildInstance(RuntimeContext runtimeContext)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (factoryClass == null) {
            throw new InstantiationException("factory class name required");
        }
        instance = (PaginationFactory) runtimeContext.loadClass(factoryClass).newInstance();
        instance.setName(name);
        instance.setSelectable(selectable);
        instance.setSortable(sortable);
        instance.setDefaultPageSize(pageSize);
        if (sortCriteria != null && !sortCriteria.isEmpty()) {
            // TODO: make SortInfo able to handle a list of (sort criterion,
            // sort order) pairs
            Entry<String, Boolean> firstCriterion = sortCriteria.entrySet().iterator().next();
            SortInfo sortInfo = new SortInfo(firstCriterion.getKey(),
                    firstCriterion.getValue().booleanValue());
            instance.setDefaultSortInfo(sortInfo);
        }
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            instance.setParameter(parameter.getKey(), parameter.getValue());
        }
    }

    public PaginationFactory getFactoryInstance() {
        return instance;
    }

}
