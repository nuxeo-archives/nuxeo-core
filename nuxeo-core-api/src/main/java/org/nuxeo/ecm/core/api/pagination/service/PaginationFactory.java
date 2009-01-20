package org.nuxeo.ecm.core.api.pagination.service;

import java.util.Map;

import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.PaginationException;

/**
 * Interface to be implemented by components contributed as extensions to the
 * PaginationService.
 * 
 * @author ogrisel
 */
public interface PaginationFactory {

    /**
     * Method actually called to build the paginated list of results.
     * 
     * @throws PaginationException if the provided arguments do not allow the
     *             instantiation of the requested page set
     * 
     */
    public <E> Pages<E> getPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Object> context)
            throws PaginationException;

    //
    // API used by the PaginationService to configure the factory behavior from
    // the extension point configuration parameters
    //

    public void setName(String name);

    /**
     * Method used to pass a map of implementation dependent parameters to
     * customize the factory behavior.
     */
    public void setParameter(String name, String value);

    public void setSelectable(boolean selectable);

    public void setDefaultPageSize(int pageSize);

    public void setDefaultSortInfo(SortInfo sortInfo);

    public void setSortable(boolean sortable);

}
