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
package org.nuxeo.ecm.core.api.provider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.SortInfo;

/**
 * Adapter to transform a normal ResultProvider implementation into a
 * SelectableResultProvider that stores its selection internally as a list
 * field.
 * 
 * @author ogrisel
 * 
 * @param <E> the type of results elements
 */
public class SimpleSelectableResultsProvider<E> implements
        SelectableResultsProvider<E> {

    private static final long serialVersionUID = 1L;

    protected final ResultsProvider<E> provider;

    protected final List<SelectionListener<E>> listeners = new ArrayList<SelectionListener<E>>();

    protected final Set<E> selected = new LinkedHashSet<E>();
    
    public SimpleSelectableResultsProvider(ResultsProvider<E> provider) {
        this.provider = provider;
    }

    public SimpleSelectableResultsProvider(String name, List<E> items,
            int pageSize, SortInfo sortInfo) {
        this(new MemoryListResultsProvider<E>(name, items, pageSize, sortInfo));
    }

    public List<SelectableResultItem<E>> getSelectableCurrentPage()
            throws ResultsProviderException {
        List<SelectableResultItem<E>> wrappedPage = new ArrayList<SelectableResultItem<E>>();
        for (E item : getCurrentPage()) {
            wrappedPage.add(new SimpleSelectableResultItem<E>(this, item));
        }
        return wrappedPage;
    }

    public List<E> getSelectedResultItems() throws ResultsProviderException {
        return new ArrayList<E>(selected);
    }

    /*
     * Listeners registration API
     */

    public void addSelectionListener(SelectionListener<E> listener) {
        listeners.add(listener);
    }

    public void removeSelectModelListener(SelectionListener<E> listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public SelectionListener<E>[] getSelectModelListeners() {
        return listeners.toArray(new SelectionListener[listeners.size()]);
    }

    public boolean isSelected(E item) {
        return selected.contains(item);
    }

    public void select(E item) throws ResultsProviderException {
        if (!getCurrentPage().contains(item)) {
            throw new ResultsProviderException(String.format(
                    "item %s is not part of current page (with index %d)",
                    item, getCurrentPageIndex()));
        }
        selected.add(item);
        for (SelectionListener<E> listener : listeners) {
            listener.handleSelect(this, item);
        }
    }

    public void unselect(E item) throws ResultsProviderException {
        if (!getCurrentPage().contains(item)) {
            throw new ResultsProviderException(String.format(
                    "item %s is not part of current page (with index %d)",
                    item, getCurrentPageIndex()));
        }
        selected.remove(item);
        for (SelectionListener<E> listener : listeners) {
            listener.handleUnselect(this, item);
        }
    }

    public boolean isAllSelected() {
        for (E item: getCurrentPage()) {
            if (!selected.contains(item)) {
                return false;
            }
        }
        return true;
    }

    public void selectAll() throws ResultsProviderException {
        for (E item : getCurrentPage()) {
            if (!selected.contains(item)) {
                select(item);
            }
        }
    }

    public void unselectAll() throws ResultsProviderException {
        for (E item : getCurrentPage()) {
            if (selected.contains(item)) {
                unselect(item);
            }
        }
    }

    /*
     * Wrapped ResultsProvider API
     */

    public List<E> getCurrentPage() {
        return provider.getCurrentPage();
    }

    public int getCurrentPageIndex() {
        return provider.getCurrentPageIndex();
    }

    public int getCurrentPageOffset() {
        return provider.getCurrentPageOffset();
    }

    public int getCurrentPageSize() {
        return provider.getCurrentPageSize();
    }

    public String getCurrentPageStatus() {
        return provider.getCurrentPageStatus();
    }

    public String getName() {
        return provider.getName();
    }

    public List<E> getNextPage() throws ResultsProviderException {
        return provider.getNextPage();
    }

    public int getNumberOfPages() {
        return provider.getNumberOfPages();
    }

    public List<E> getPage(int page) throws ResultsProviderException {
        return provider.getPage(page);
    }

    public int getPageSize() {
        return provider.getPageSize();
    }

    public long getResultsCount() {
        return provider.getResultsCount();
    }

    public SortInfo getSortInfo() {
        return provider.getSortInfo();
    }

    public boolean isNextPageAvailable() {
        return provider.isNextPageAvailable();
    }

    public boolean isPreviousPageAvailable() {
        return provider.isPreviousPageAvailable();
    }

    public boolean isSortable() {
        return provider.isSortable();
    }

    public void last() throws ResultsProviderException {
        provider.last();
    }

    public void next() throws ResultsProviderException {
        provider.next();
    }

    public void previous() throws ResultsProviderException {
        provider.previous();
    }

    public void refresh() throws ResultsProviderException {
        provider.refresh();
    }

    public void rewind() throws ResultsProviderException {
        provider.rewind();
    }

    public void setName(String name) {
        provider.setName(name);
    }

}
