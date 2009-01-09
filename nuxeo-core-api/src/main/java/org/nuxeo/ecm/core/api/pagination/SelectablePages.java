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

/**
 * @author ogrisel
 * 
 * @param <E> the type of results element (e.g. DocumentModel, DashboardItem,
 *            ...)
 */
public interface SelectablePages<E> extends Pages<E> {

    /**
     * Wrapper interface to wrap a paginated element into a selectable component
     * 
     * @param <E> the type of the wrapped result element (e.g. DocumentModel,
     *            DashboardItem, ...)
     */
    public static interface SelectablePageElement<E> {

        /**
         * @return the SelectablePages instance that holds the
         *         selection
         */
        SelectablePages<E> getPages();

        boolean isSelected();

        void select() throws PaginationException;

        void unselect() throws PaginationException;

        /**
         * @return the wrapped result element
         */
        E getData();

    }

    /**
     * @return the list of items of the current page wrapped into
     *         SelectablePageItem objects that provide selection API
     * @throws PaginationException
     */
    List<SelectablePageElement<E>> getSelectableCurrentPage()
            throws PaginationException;

    /**
     * @return the list of items that are marked as selected on any page
     * @throws PaginationException
     */
    List<E> getSelectedElements() throws PaginationException;

    /**
     * @return true if item is selected
     */
    boolean isSelected(E item);

    /**
     * Mark item as selected
     * @throws PaginationException if item is not part of current page
     */
    void select(E item) throws PaginationException;

    /**
     * Mark item as not selected
     * @throws PaginationException if item is not part of current page
     */
    void unselect(E item) throws PaginationException;
    
    /**
     * Select any unselected element in current page
     */
    void selectCurrentPage() throws PaginationException;

    /**
     * Unselect any selected element in the current page
     */
    void unselectCurrentPage() throws PaginationException;
    
    /**
     * @return true if all items of the current page are marked as selected
     */
    boolean isCurrentPageSelected();

    /*
     * SelectionListener registration and API
     */

    /**
     * Interface to be implemented by components that want to react to item
     * selection.
     * 
     * @author ogrisel
     * 
     * @param <E> the type of selected items
     */
    public static interface SelectionListener<E> {

        void handleSelect(SelectablePages<E> pages, E item);

        void handleUnselect(SelectablePages<E> pages, E item);

    }

    void addSelectionListener(SelectionListener<E> listener);

    void removeSelectModelListener(SelectionListener<E> listener);

    SelectionListener<E>[] getSelectModelListeners();

}
