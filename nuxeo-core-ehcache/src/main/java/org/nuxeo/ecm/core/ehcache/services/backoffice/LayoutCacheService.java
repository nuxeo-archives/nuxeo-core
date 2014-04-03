/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrick Guillerm - initial API and implementation
 *
 * $Id$
 */
package org.nuxeo.ecm.core.ehcache.services.backoffice;

import org.nuxeo.ecm.core.ehcache.constants.CacheRegion;
import org.nuxeo.ecm.core.ehcache.constants.EhCacheRegion;
import org.nuxeo.ecm.core.ehcache.services.AbstractEhCacheCommons;

/**
 * The Class LayoutCacheService.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public class LayoutCacheService extends AbstractEhCacheCommons {
    // =========================================================================
    // SINGLETON
    // =========================================================================
    /** The Constant INSTANCE. */
    private static final LayoutCacheService INSTANCE = new LayoutCacheService();

    /**
     * Gets the single instance of DashboardCacheService.
     * 
     * @return single instance of DashboardCacheService
     */
    public static LayoutCacheService getInstance() {
        synchronized (LayoutCacheService.class) {
            return INSTANCE;
        }
    }

    // =========================================================================
    // IMPLEMENTS
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected CacheRegion initializeEhCacheRegion() {
        return EhCacheRegion.layout;
    }
}
