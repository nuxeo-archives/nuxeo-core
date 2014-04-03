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
package org.nuxeo.ecm.core.ehcache.seam;

import static org.jboss.seam.ScopeType.APPLICATION;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.cache.CacheProvider;
import org.nuxeo.ecm.core.ehcache.constants.CacheObservers;
import org.nuxeo.ecm.core.ehcache.constants.CacheRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EhCacheControlBean.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
@Name(EhCacheControl.NAME)
@Scope(APPLICATION)
public class EhCacheControlBean implements EhCacheControl {

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9096686108381833965L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheControlBean.class);

    /** The cache provider. */
    @In(create = true)
    private CacheProvider<CacheManager> cacheProvider;

    // =========================================================================
    // CACHES REGIONS NAMES
    // =========================================================================
    /**
     * {@inheritDoc}
     */
    @Factory(value = DASHBOARD)
    @Override
    public String getNameRegionDashboard() {
        return DASHBOARD;
    }

    /**
     * {@inheritDoc}
     */
    @Factory(value = LAYOUT)
    @Override
    public String getNameRegionLayout() {
        return LAYOUT;
    }

    /**
     * {@inheritDoc}
     */
    @Factory(value = WEBENGINE)
    @Override
    public String getNameRegionWebengine() {
        return WEBENGINE;
    }

    // =========================================================================
    // CACHE CLEAN
    // =========================================================================
    /**
     * {@inheritDoc}
     */
    @Observer(CacheObservers.CLEAN_DASHBOARD)
    @Override
    public void observerCleanDashboard() {
        LOGGER.debug(CacheObservers.CLEAN_DASHBOARD);
        cleanCache(DASHBOARD);
    }

    /**
     * {@inheritDoc}
     */
    @Observer(CacheObservers.CLEAN_LAYOUT)
    @Override
    public void observerCleanLayout() {
        LOGGER.debug(CacheObservers.CLEAN_LAYOUT);
        cleanCache(LAYOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Observer(CacheObservers.CLEAN_WEBENGINE)
    @Override
    public void observerCleanWebengine() {
        LOGGER.debug(CacheObservers.CLEAN_WEBENGINE);
        cleanCache(WEBENGINE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanCache(String regionName) {

        Cache cache = null;

        if (cacheProvider != null) {
            cache = cacheProvider.getDelegate().getCache(regionName);
        }

        if (cache != null) {
            cache.removeAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanCache(CacheRegion regionName) {
        if (regionName != null) {
            cleanCache(regionName.regionName());
        }
    }

    // =========================================================================
    // CACHE PROVIDER
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheProvider<CacheManager> getCacheProvider() {
        return cacheProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCacheProvider(CacheProvider<CacheManager> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    // =========================================================================
    // CACHE INFOS
    // =========================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] regionsNames() {
        return cacheProvider.getDelegate().getCacheNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long regionMemorySize(final CacheRegion regionName) {
        if (regionName == null) {
            return 0;
        } else {
            return regionMemorySize(regionName.regionName());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long regionMemorySize(final String regionName) {
        final Cache cache = cacheProvider.getDelegate().getCache(regionName);

        if (cache == null) {
            return 0;
        } else {
            return cache.calculateInMemorySize();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long regionDiskStoreSize(final CacheRegion regionName) {
        if (regionName == null) {
            return 0;
        } else {
            return regionDiskStoreSize(regionName.regionName());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long regionDiskStoreSize(final String regionName) {
        final Cache cache = cacheProvider.getDelegate().getCache(regionName);
        if (cache == null) {
            return 0;
        } else {
            return cache.getDiskStoreSize();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int regionItemSize(final CacheRegion regionName) {
        if (regionName == null) {
            return 0;
        } else {
            return regionItemSize(regionName.regionName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int regionItemSize(final String regionName) {
        Cache cache = cacheProvider.getDelegate().getCache(regionName);

        if (cache == null) {
            return 0;
        } else {
            return cache.getSize();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics regionStatistics(final CacheRegion regionName) {
        if (regionName == null) {
            return null;
        } else {
            return regionStatistics(regionName.regionName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statistics regionStatistics(final String regionName) {
        Cache cache = cacheProvider.getDelegate().getCache(regionName);
        Statistics result = null;

        if (cache != null) {
            result = cache.getStatistics();
        }
        return result;
    }

}
