package org.nuxeo.ecm.core.api.pagination;

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
    }

    public void testMemoryPaginationFactory() throws Exception {
        PaginationService service = Framework.getService(PaginationService.class);
        assertNotNull(service);
    }

}
