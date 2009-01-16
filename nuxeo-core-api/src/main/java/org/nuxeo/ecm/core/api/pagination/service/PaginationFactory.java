package org.nuxeo.ecm.core.api.pagination.service;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.Pages;

/**
 * Interface to be implemented by components contributed as extensions to the
 * PaginationService.
 * 
 * @author ogrisel
 */
public interface PaginationFactory {

    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Serializable> context);

    public void setParameter(String name, String value);

    public void setSelectable(boolean selectable);

    public void setDefaultPageSize(int pageSize);

    public void setDefaultSortInfo(SortInfo sortInfo);

}
