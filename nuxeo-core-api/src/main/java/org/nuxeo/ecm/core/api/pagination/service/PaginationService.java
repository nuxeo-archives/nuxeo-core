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

import java.util.Map;

import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.PaginationException;

/**
 * Service interface to fetched paginated list of items typically computed for
 * as configured query result.
 * 
 * @author ogrisel
 */
public interface PaginationService {

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            Map<String, Object> context) throws PaginationException;

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Object> context)
            throws PaginationException;

    public <E> Pages<E> getEmptyPages(String paginatorName)
            throws PaginationException;

}
