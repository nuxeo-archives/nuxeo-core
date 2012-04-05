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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.jboss.seam.cache.CacheProvider;
import org.nuxeo.ecm.core.ehcache.constants.CacheItem;
import org.nuxeo.ecm.core.ehcache.constants.CacheRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractEhCacheCommons.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public abstract class AbstractEhCacheCommons {

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEhCacheCommons.class);

    private final String synchroSetObject;

    private final String synchroDeleteObject;

    private final String synchroDelete;

    /** The region. */
    protected CacheRegion region;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================
    /**
     * Instantiates a new abstract eh cache commons.
     */
    protected AbstractEhCacheCommons() {
        region = initializeEhCacheRegion();
        final String className = getClass().getName();

        synchroSetObject = String.format("%s-setObject", className);
        synchroDeleteObject = String.format("%s-deleteObject", className);
        synchroDelete = String.format("%s-delete", className);
    }

    // =========================================================================
    // ABSTRACT
    // =========================================================================
    /**
     * Initialize eh cache region.
     * 
     * @return the cache region
     */
    protected abstract CacheRegion initializeEhCacheRegion();


    // =========================================================================
    // IMPLEMENTS
    // =========================================================================

    // -------------------------------------------------------------------------
    // getObject
    // -------------------------------------------------------------------------
    /**
     * Allow to grab object from cache.Only use this method with dynamics key
     * value !
     * 
     * @param <T> the generic type
     * @param key object key
     * @param clazz desire class
     * @return the desire object
     */
    public <T> T getObject(String key, Class<T> clazz) {
        return getObject(key, clazz, false);
    }

    /**
     * Allow to grab object from cache with enumeration
     * 
     * @param <T> the generic type
     * @param item object key
     * @param clazz desire class
     * @return the desire object
     */
    public <T> T getObject(CacheItem item, Class<T> clazz) {
        return getObject(item.itemKey(), clazz, false);
    }

    /**
     * Allow to grab object from cache.Only use this method with dynamics key
     * value !
     * 
     * @param <T> the generic type
     * @param key object key
     * @param clazz desire class
     * @param preview in preview mode, cache is disable
     * @return the desire object
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz, boolean preview) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> getObject({},{})", key, clazz);
        }
        T result = null;

        if (!preview) {
            final CacheProvider<CacheManager> cacheProvider = EhCacheServices.getInstance().getCacheProvider();

            Object objectCached = cacheProvider.get(region.regionName(), key);

            if (objectCached != null) {
                result = (T) objectCached;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<<< getObject return : {} - {}", key, result);
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // setObject
    // -------------------------------------------------------------------------
    /**
     * Allow to put an object to cache. Only use this method with dynamics key
     * value !
     * 
     * @param item cache object key
     * @param object the object
     */
    public void setObject(CacheItem item, Object object) {
        setObject(item.itemKey(), object, false);
    }

    /**
     * Allow to put an object to cache. Only use this method with dynamics key
     * value !
     * 
     * @param key cache object key
     * @param object the object
     */
    public void setObject(String key, Object object) {
        setObject(key, object, false);
    }

    /**
     * Allow to put an object to cache. Only use this method with dynamics key
     * value !
     * 
     * @param key cache object key
     * @param object the object
     * @param preview if it's preview mode, cache is disable
     */
    public void setObject(String key, Object object, boolean preview) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(">>> setObject({},{})", key, object);
        }
        if (!preview) {
            synchronized (synchroSetObject) {
                final CacheProvider<CacheManager> cacheProvider = EhCacheServices.getInstance().getCacheProvider();
                cacheProvider.put(region.regionName(), key, object);
            }

        }
    }

    /**
     * Allow to put an object to cache
     * 
     * @param item cache object key
     * @param object the object
     * @param preview if it's preview mode, cache is disable
     */
    public void setObject(CacheItem item, Object object, boolean preview) {
        setObject(item.itemKey(), object, preview);
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    /**
     * Allow to delete an object from cache. Only use this method with dynamics
     * key value !
     * 
     * @param key cache object key
     */
    public void delete(String key) {
        delete(key, false);
    }

    /**
     * Allow to delete an object from cache
     * 
     * @param item cache object key
     */
    public void delete(CacheItem item) {
        delete(item.itemKey(), false);
    }

    /**
     * Allow to delete an object from cache. Only use this method with dynamics
     * key value !
     * 
     * @param key cache object key
     * @param preview if it's preview mode, cache is disable
     */
    public void delete(String key, boolean preview) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("xxx delete({})", key);
        }
        if (!preview) {
            synchronized (synchroDeleteObject) {
                final CacheProvider<CacheManager> cacheProvider = EhCacheServices.getInstance().getCacheProvider();
                cacheProvider.remove(region.regionName(), key);
            }
        }
    }

    /**
     * Allow to delete an object from cache.
     * 
     * @param item cache object key
     * @param preview if it's preview mode, cache is disable
     */
    public void delete(CacheItem item, boolean preview) {
        delete(item.itemKey(), preview);
    }

    /**
     * Allow to clean cache region.
     */
    public void deleteAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XXXX delete all webengine cache()");
        }

        synchronized (synchroDelete) {
            final CacheProvider<CacheManager> cacheProvider = EhCacheServices.getInstance().getCacheProvider();
            final Cache cache = cacheProvider.getDelegate().getCache(
                    region.regionName());
            cache.removeAll();
        }
    }
}
