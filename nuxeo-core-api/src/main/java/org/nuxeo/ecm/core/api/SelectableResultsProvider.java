package org.nuxeo.ecm.core.api;


public interface SelectableResultsProvider<E> extends ResultsProvider<E> {

    public static interface SelectableResultPage<E> {

    }

    public static interface SelectableResultItem<E> {

        boolean isSelected();

        void select();

        E getData();

    }

    public static interface SelectionListener<E> {

    }

    SelectableResultPage<E> getSelectableCurrentPage() throws ResultsProviderException;

    void addSelectionListener(SelectionListener<E> listener);

    void removeSelectModelListener(SelectionListener<E> listener);

    SelectionListener<E>[] getSelectModelListeners();

}
