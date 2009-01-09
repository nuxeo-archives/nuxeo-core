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

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.SortInfo;

/**
 * Interface that provide means to access a result set by pages, allowing easy
 * navigation between them.
 *
 * This is typically used to build paginated list of DocumentModel in search
 * results views or workspace content listing views for instance.
 *
 * The type of the elements in a page is generic hence it can also be used to
 * build paginated list of non document elements such as lists of active
 * workflow tasks for instance.
 *
 * @author <a href="mailto:dm@nuxeo.com">Dragos Mihalache</a>
 * @author <a href="mailto:gr@nuxeo.com">Georges Racinet</a>
 * @author <a href="mailto:og@nuxeo.com">Olivier Grisel</a>
 *
 */
public interface Pages<E> extends Serializable {

    /**
     * Constant to express that the total number of result elements is unknown.
     */
    int UNKNOWN_SIZE = -1;

    /**
     * Returns the current page of results.
     * <p>
     * This method is designed to be called from web views. It therefore ensures
     * cheapness of repeated calls, rather than data consistency.
     * There is a refresh() method for that.
     * <p>
     *
     * @return the current page
     */
    List<E> getCurrentPage();

    /**
     * Sets the current page of results to the required one and return it.
     *
     * @param page the page index, starting from 0
     * @return
     * @throws PaginationException is requested page is not available
     */
    List<E> getPage(int page) throws PaginationException;

    /**
     * Force refresh of the current page
     * @throws PaginationException
     */
    void refresh() throws PaginationException;

    /**
     * @return a boolean expressing if there are further pages.
     */
    boolean isNextPageAvailable();

    /**
     * @return a boolean expressing if there is a previous page.
     */
    boolean isPreviousPageAvailable();

    /**
     * @return the number of elements in current page.
     */
    int getCurrentPageSize();

    /**
     *
     * @return the number of requested page size.

     */
    int getPageSize();

    /**
     * @return the offset (starting from 0) of the first element in the
     * current page or <code>UNKNOWN_SIZE</code>
     */
    int getCurrentPageOffset();

    /**
     * Get the next page of elements.
     * <p>Has the side effect of setting the current page, too,
     * hence <code>provider.getNextPage()</code> is equivalent to
     * <code> provider.next(); page = provider.getCurrentPage() </code>
     * in terms of returned value and state of the provider, although
     * implementation details might imply a performance difference.
     *
     * @return the next page
     * @throws PaginationException is requested page is not available
     */
    List<E> getNextPage() throws PaginationException;

    /**
     * @return number of result elements if available or <code>UNKNOWN_SIZE</code> if it is unknown
     */
    long getResultsCount();

    /**
     * Get current page index as a 0 (zero) based int.
     *
     * @return current page index
     */
    int getCurrentPageIndex();

    /**
     * Return the total number of pages
     * @return an integer
     */
    int getNumberOfPages();

    /**
     * @return a simple formatted string for current pagination statuts.
     */
    String getCurrentPageStatus();

    /* Easy navigation API */

    /**
     * Go to the first page
     * @throws PaginationException is requested page is not available
     */
    void firstPage() throws PaginationException;

    /**
     * Go to the previous page
     * @throws PaginationException is requested page is not available
     */
    void previousPage() throws PaginationException;

    /**
     * Go to the next page
     * @throws PaginationException is requested page is not available
     */
    void nextPage() throws PaginationException;

    /**
     * Go to the last page
     * @throws PaginationException is requested page is not available
     */
    void lastPage() throws PaginationException;

    /**
     * @return the sorting info for this provider
     */
    SortInfo getSortInfo();

    /**
     * @return if this provider is sortable
     */
    boolean isSortable();

    /**
     * @return the provider identifier
     */
    String getName();

    /**
     * @param name the provider identifier
     */
    void setName(String name);

}
