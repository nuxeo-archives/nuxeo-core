package org.nuxeo.ecm.core.api.pagination.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.PaginationException;
import org.nuxeo.ecm.core.api.pagination.SimpleSelectablePages;

/**
 * Abstract base class to help build contributions to the PaginationService by
 * factoring away the redundant code.
 * 
 * @author ogrisel
 */
public abstract class AbstractPaginationFactory implements PaginationFactory {

    private static final Log log = LogFactory.getLog(AbstractPaginationFactory.class);

    String name;

    int defaultPageSize = 30;

    boolean selectable = false;

    boolean sortable = false;

    protected SortInfo defaultSortInfo;

    protected boolean sortAscending;

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultPageSize(int pageSize) {
        defaultPageSize = pageSize;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public void setDefaultSortInfo(SortInfo sortInfo) {
        defaultSortInfo = sortInfo;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public void setParameter(String name, String value) {
        // no additional parameters by default, override me if needed
    }

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Object> context)
            throws PaginationException {

        if (!sortable && sortInfo != null) {
            log.warn(String.format(
                    "ignore requested sortinfo %s/%s since factory '%s' is not sortable",
                    sortInfo.getSortColumn(), sortInfo.getSortAscending(), name));
            sortInfo = null;
        }

        Pages<E> rawPages = getRawPages(paginatorName, sortInfo,
                pageSize > 0 ? pageSize : defaultPageSize, context);
        if (selectable) {
            return new SimpleSelectablePages<E>(rawPages);
        }
        return rawPages;
    }

    abstract protected <E> Pages<E> getRawPages(String paginatorName,
            SortInfo sortInfo, int pageSize, Map<String, Object> context)
            throws PaginationException;

}