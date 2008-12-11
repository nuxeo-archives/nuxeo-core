package org.nuxeo.ecm.core.api.provider;

import org.nuxeo.ecm.core.api.provider.SelectableResultsProvider.SelectableResultItem;

/**
 * Basic implementation of the SelectableResultItem that relies upon the
 * SelectableResultsProvider to hold the selection state of each item.
 * 
 * @author ogrisel
 * 
 * @param <E>
 */
public class SimpleSelectableResultItem<E> implements SelectableResultItem<E> {

    protected final E item;
    
    protected final SelectableResultsProvider<E> provider;

    public SimpleSelectableResultItem(SelectableResultsProvider<E> provider, E item) {
        this.item = item;
        this.provider = provider;
    }
    
    public E getData() {
        return item;
    }

    public SelectableResultsProvider<E> getProvider() {
        return provider;
    }

    public boolean isSelected() {
        return provider.isSelected(item);
    }

    public void select() {
        provider.select(item);
    }

    public void unselect() {
        provider.unselect(item);
    }

}
