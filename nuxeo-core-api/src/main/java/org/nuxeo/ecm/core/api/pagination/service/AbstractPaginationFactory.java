package org.nuxeo.ecm.core.api.pagination.service;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.SimpleSelectablePages;

/**
 * Abstract base class to help build contributions to the PaginationService by
 * factoring away the redundant code.
 * 
 * @author ogrisel
 */
public abstract class AbstractPaginationFactory implements PaginationFactory {

    int defaultPageSize = 30;

    boolean selectable = false;

    protected SortInfo defaultSortInfo;

    protected boolean sortAscending;

    public void setDefaultPageSize(int pageSize) {
        defaultPageSize = pageSize;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setDefaultSortInfo(SortInfo sortInfo) {
        defaultSortInfo = sortInfo;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Serializable> context) {
        Pages<E> rawPages = getUnselectablePages(paginatorName, sortInfo,
                pageSize, context);
        if (selectable) {
            return new SimpleSelectablePages<E>(rawPages);
        }
        return rawPages;
    }

    abstract protected <E> Pages<E> getUnselectablePages(String paginatorName,
            SortInfo sortInfo, int pageSize, Map<String, Serializable> context);

}