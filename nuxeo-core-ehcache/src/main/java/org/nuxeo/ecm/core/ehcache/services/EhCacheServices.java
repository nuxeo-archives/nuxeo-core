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
package org.nuxeo.ecm.core.ehcache.services;

import net.sf.ehcache.CacheManager;

import org.jboss.seam.Component;
import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.contexts.Lifecycle;
import org.nuxeo.ecm.core.ehcache.seam.EhCacheControl;

/**
 * The Class EhCacheServices.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public class EhCacheServices {

    // =========================================================================
    // ATTRIBUT
    // =========================================================================

    private static EhCacheServices instance;

    private CacheProvider<CacheManager> cacheProvider;

    // =========================================================================
    // SINGELTON
    // =========================================================================

    /**
     * Instantiates a new EhCacheServices.
     */
    private EhCacheServices() {
        Lifecycle.beginCall();
        final EhCacheControl ehCacheControl = (EhCacheControl) Component.getInstance(EhCacheControl.NAME);
        cacheProvider = ehCacheControl.getCacheProvider();
        Lifecycle.endCall();
    }

    /**
     * Instantiates a new EhCacheServices in unit tests.
     * 
     * @param unitTest to enable CacheMock.
     */
    protected EhCacheServices(final boolean unitTest) {
        if (unitTest) {
            cacheProvider = EhCacheMock.getInstance();
        } else {
            cacheProvider = null;
        }

    }

    /**
     * Gets the single instance of EhCacheServices.
     * 
     * @return single instance of EhCacheServices
     */
    public static EhCacheServices getInstance() {
        synchronized (EhCacheServices.class) {
            instance = new EhCacheServices();
            return instance;
        }
    }

    /**
     * Gets the single instance of EhCacheServices.
     * 
     * @return single instance of EhCacheServices
     */
    protected EhCacheServices getInstanceForJunit() {
        instance = new EhCacheServices(true);
        return instance;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================
    /**
     * Gets the cache provider.
     * 
     * @return the cache provider
     */
    public CacheProvider<CacheManager> getCacheProvider() {
        return cacheProvider;
    }
}
