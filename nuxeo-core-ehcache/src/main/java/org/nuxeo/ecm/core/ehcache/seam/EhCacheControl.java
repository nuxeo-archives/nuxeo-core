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

import java.io.Serializable;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.jboss.seam.cache.CacheProvider;
import org.nuxeo.ecm.core.ehcache.constants.CacheRegion;

/**
 * The Interface EhCacheControl.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public interface EhCacheControl extends Serializable {

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================
    /** The NAME. */
    String NAME = "ehCacheControl";

    /** Dashboard cache region, key :<b>dashboard</b> */
    String DASHBOARD = "dashboard";

    /** Layout cache region, key :<b>layout</b> */
    String LAYOUT = "layout";

    /** Webengine cache region, key :<b>webengine</b> */
    String WEBENGINE = "webengine";

    // =========================================================================
    // CACHES REGIONS NAMES
    // =========================================================================
    /**
     * Allow to gets the region name "dashboard" in JSF/EL context.
     * 
     * @return region name.
     */
    String getNameRegionDashboard();

    /**
     * Allow to gets the region name "layout" in JSF/EL context.
     * 
     * @return region name.
     */
    String getNameRegionLayout();

    /**
     * Allow to gets the region name "webengine" in JSF/EL context.
     * 
     * @return region name.
     */
    String getNameRegionWebengine();

    // =========================================================================
    // CACHE CLEAN
    // =========================================================================

    /**
     * Allow to clean Dashboard cache with Seam Observer
     * CacheObservers.CLEAN_DASHBOARD
     * 
     * @see org.nuxeo.ecm.core.ehcache.constants.CacheObservers
     */
    void observerCleanDashboard();

    /**
     * Allow to clean Layout cache with Seam Observer
     * CacheObservers.CLEAN_LAYOUT
     * 
     * @see org.nuxeo.ecm.core.ehcache.constants.CacheObservers
     */
    void observerCleanLayout();

    /**
     * Allow to clean Layout cache with Seam Observer
     * CacheObservers.CLEAN_WEBENGINE
     * 
     * @see org.nuxeo.ecm.core.ehcache.constants.CacheObservers
     */
    void observerCleanWebengine();

    /**
     * Allow to clean an specific cache region
     * 
     * @param regionName the region to clean
     */
    void cleanCache(final String regionName);

    /**
     * Allow to clean an specific cache region
     * 
     * @param regionName the region name
     */
    void cleanCache(final CacheRegion regionName);

    // =========================================================================
    // CACHE PROVIDER
    // =========================================================================

    /**
     * Gets the cache provider.
     * 
     * @return the cache provider
     */
    CacheProvider<CacheManager> getCacheProvider();

    /**
     * Sets the cache provider.
     * 
     * @param cacheProvider the new cache provider
     */
    void setCacheProvider(CacheProvider<CacheManager> cacheProvider);

    // =========================================================================
    // CACHE INFOS
    // =========================================================================

    /**
     * Allow to grab all regions names.
     * 
     * @return the list of cache region name
     */
    String[] regionsNames();

    /**
     * Region memory size.
     * 
     * @param regionName the region name
     * @return the long
     */
    long regionMemorySize(CacheRegion regionName);

    /**
     * Region memory size.
     * 
     * @param regionName the region name
     * @return the long
     */
    long regionMemorySize(String regionName);

    /**
     * Region disk store size.
     * 
     * @param regionName the region name
     * @return the long
     */
    long regionDiskStoreSize(CacheRegion regionName);

    /**
     * Region disk store size.
     * 
     * @param regionName the region name
     * @return the long
     */
    long regionDiskStoreSize(String regionName);

    /**
     * Region item size.
     * 
     * @param regionName the region name
     * @return the int
     */
    int regionItemSize(CacheRegion regionName);

    /**
     * Region item size.
     * 
     * @param regionName the region name
     * @return the int
     */
    int regionItemSize(String regionName);

    /**
     * Region statistics.
     * 
     * @param regionName the region name
     * @return the statistics
     */
    Statistics regionStatistics(String regionName);

    /**
     * Region statistics.
     * 
     * @param regionName the region name
     * @return the statistics
     */
    Statistics regionStatistics(CacheRegion regionName);

}