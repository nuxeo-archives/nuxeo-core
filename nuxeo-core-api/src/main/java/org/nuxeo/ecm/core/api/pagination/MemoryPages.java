/*
 * (C) Copyright 2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id $
 */
package org.nuxeo.ecm.core.api.pagination;

import java.util.List;

import org.nuxeo.ecm.core.api.SortInfo;

/**
 * Simple implementation of the Pages<E> interface that hold all the
 * pages in memory.
 * 
 * @author ogrisel
 * 
 * @param <E> the type of the element items
 */
public class MemoryPages<E> implements Pages<E> {

    private static final long serialVersionUID = 1L;
    
    protected List<E> items;
    
    protected final int pageSize;
    
    protected final SortInfo sortInfo;

    protected final int nbPages;
    
    protected final int lastPageSize;

    protected int currentPageIndex = 0;

    protected String name;
    
    public MemoryPages(String name, List<E> items, int pageSize, SortInfo sortInfo) {
        this.name = name;
        this.items = items;
        this.pageSize = pageSize;
        this.sortInfo = sortInfo;
        int div = items.size() / pageSize;
        int mod = items.size() % pageSize;
        if (mod != 0) {
            lastPageSize = mod;
            nbPages = div + 1;
        } else {
            lastPageSize = pageSize;
            nbPages = div;
        }
    }

    public List<E> getCurrentPage() {
        int offset = getCurrentPageOffset();
        if (currentPageIndex == nbPages - 1) {
            // special handling of last page
            return items.subList(offset, offset + lastPageSize);
        } else {
            // other pages all have the standard page size
            return items.subList(offset, offset + pageSize);
        }
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getCurrentPageOffset() {
        return currentPageIndex * pageSize;
    }

    public int getCurrentPageSize() {
        return getCurrentPage().size();
    }

    public String getCurrentPageStatus() {
        return String.format("%d/%d", currentPageIndex + 1, getNumberOfPages());
    }

    public List<E> getNextPage() throws PaginationException {
        nextPage();
        return getCurrentPage();
    }

    public int getNumberOfPages() {
        return nbPages;
    }

    public List<E> getPage(int page) throws PaginationException {
        if (page >= nbPages) {
            throw new PaginationException(String.format(
                    "cannot get page #%d of a results provider with %d pages",
                    page, nbPages));
        } else if (page < 0) {
            throw new PaginationException(String.format(
                    "cannot get page with negative index %d", page));
        }
        currentPageIndex = page;
        return getCurrentPage();
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getResultsCount() {
        return items.size();
    }

    public SortInfo getSortInfo() {
        return sortInfo;
    }

    public boolean isNextPageAvailable() {
        return currentPageIndex < nbPages - 1;
    }

    public boolean isPreviousPageAvailable() {
        return currentPageIndex > 0;
    }

    public boolean isSortable() {
        return sortInfo != null;
    }

    public void lastPage() throws PaginationException {
        currentPageIndex = nbPages - 1;
    }

    public void nextPage() throws PaginationException {
        if (currentPageIndex == nbPages - 1) {
            throw new PaginationException("already at last page");
        }
        currentPageIndex++;
    }

    public void previousPage() throws PaginationException {
        if (currentPageIndex == 0) {
            throw new PaginationException("already at first page");
        }
        currentPageIndex--;
    }

    public void refresh() throws PaginationException {
        // nothing to do
    }

    public void firstPage() throws PaginationException {
        currentPageIndex = 0;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
       this.name = name;
    }

}
