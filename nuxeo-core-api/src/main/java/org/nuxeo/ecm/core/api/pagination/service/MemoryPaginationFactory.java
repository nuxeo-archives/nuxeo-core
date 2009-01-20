package org.nuxeo.ecm.core.api.pagination.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.pagination.MemoryPages;
import org.nuxeo.ecm.core.api.pagination.Pages;
import org.nuxeo.ecm.core.api.pagination.PaginationException;

/**
 * Sample factory implementation that build MemoryPages instances out of a list
 * of items found in the context
 * 
 * @author ogrisel
 */
public class MemoryPaginationFactory extends AbstractPaginationFactory {

    public static final String ITEM_LIST_CONTEXT_KEY = "items";

    @SuppressWarnings("unchecked")
    @Override
    protected <E> Pages<E> getRawPages(String paginatorName, SortInfo sortInfo,
            int pageSize, Map<String, Object> context)
            throws PaginationException {

        List<E> items = (List<E>) context.get(ITEM_LIST_CONTEXT_KEY);
        if (items == null) {
            List<String> providedKeys = new ArrayList<String>(context.keySet());
            throw new PaginationException(
                    String.format(
                            "failed to build MemoryPages instance with name '%s' since requested context key '%s' "
                                    + "could not be found among provided context (%s)",
                            name, ITEM_LIST_CONTEXT_KEY,
                            StringUtils.join(providedKeys)));
        }
        return new MemoryPages<E>(paginatorName, items, pageSize, sortInfo);
    }
}
