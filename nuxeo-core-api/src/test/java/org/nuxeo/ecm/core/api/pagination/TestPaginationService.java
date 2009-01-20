package org.nuxeo.ecm.core.api.pagination;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.pagination.service.MemoryPaginationFactory;
import org.nuxeo.ecm.core.api.pagination.service.PaginationService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class TestPaginationService extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // I don't get why deployBundle does not work
        // deployBundle("org.nuxeo.ecm.core.api");
        deployContrib("org.nuxeo.ecm.core.api",
                "OSGI-INF/pagination-service-framework.xml");
        deployContrib("org.nuxeo.ecm.core.api.test",
                "test-pagination-service-contrib.xml");
    }

    public void testMissingPaginationFactory() throws Exception {
        PaginationService service = Framework.getService(PaginationService.class);
        assertNotNull(service);

        // test lookup of non registered factories
        try {
            service.getEmptyPages("missing paginator");
            service.getPages("missing paginator", null, null);
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
            // expected exception
        }
    }

    public void testUnselectableMemoryPaginationFactory() throws Exception {
        PaginationService service = Framework.getService(PaginationService.class);
        assertNotNull(service);

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(MemoryPaginationFactory.ITEM_LIST_CONTEXT_KEY,
                Arrays.asList(0, 1, 2, 3, 4));
        Pages<Integer> pages = service.getPages(
                "unselectable_memory_pagesize_3", null, context);

        assertNotNull(pages);
        assertFalse(pages instanceof SelectablePages);
        assertEquals(Arrays.asList(0, 1, 2), pages.getCurrentPage());
    }

    public void testSelectableMemoryPaginationFactory() throws Exception {
        PaginationService service = Framework.getService(PaginationService.class);
        assertNotNull(service);

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(MemoryPaginationFactory.ITEM_LIST_CONTEXT_KEY,
                Arrays.asList(0, 1, 2, 3, 4));
        Pages<Integer> pages = service.getPages("selectable_memory_pagesize_2",
                null, context);

        assertNotNull(pages);
        assertEquals(Arrays.asList(0, 1), pages.getCurrentPage());
        assertTrue(pages instanceof SelectablePages);

        SelectablePages<Integer> spages = (SelectablePages<Integer>) pages;
        assertEquals(Arrays.asList(0, 1), spages.getCurrentPage());

        spages.select(Integer.valueOf(1));
        assertTrue(spages.isSelected(Integer.valueOf(1)));
    }

}
