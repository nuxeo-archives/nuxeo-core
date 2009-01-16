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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.pagination;

import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.SortInfo;

public class EmptyPages<E> implements Pages<E> {

    private static final long serialVersionUID = 1L;
    
    protected String name;
    
    public EmptyPages(String name) {
        this.name = name;
    }

    public static <T> Pages<T> getInstance() {
        return new EmptyPages<T>(null);
    }
    
    public List<E> getCurrentPage() {
        return Collections.emptyList();
    }

    public int getCurrentPageIndex() {
        return 0;
    }

    public int getCurrentPageOffset() {
        return 0;
    }

    public int getCurrentPageSize() {
        return 0;
    }

    public String getCurrentPageStatus() {
        return "";
    }

    public List<E> getNextPage() {
        return null;
    }

    public int getNumberOfPages() {
        return 0;
    }

    public List<E> getPage(int page) {
        return null;
    }

    public long getResultsCount() {
        return 0;
    }

    public boolean isNextPageAvailable() {
        return false;
    }

    public boolean isPreviousPageAvailable() {
        return false;
    }

    public void lastPage() {
    }

    public void nextPage() {
    }

    public void previousPage() {
    }

    public void refresh() throws PaginationException {
    }

    public void firstPage() {
    }

    public int getPageSize() {
        return 0;
    }
    
    public SortInfo getSortInfo() {
        return null;
    }
    
    public boolean isSortable() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
