package org.nuxeo.ecm.core.ehcache.services;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;

import org.jboss.seam.cache.CacheProvider;

/**
 * The Class EhCacheMock for JUnit testing.
 */
public final class EhCacheMock extends CacheProvider<CacheManager> {

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================
    /** The instance. */
    private static EhCacheMock instance = new EhCacheMock();

    /** The CACH e_ mock. */
    private static Map<String, Map<String, Object>> cacheMock = new HashMap<String, Map<String, Object>>();

    // =========================================================================
    // SINGLETON CONTRUCTOR
    // =========================================================================

    /**
     * Instantiates a new webengine eh cache.
     */
    private EhCacheMock() {
        super();
    }

    /**
     * Gets the factory.
     * 
     * @return the factory
     */
    public static EhCacheMock getInstance() {
        synchronized (EhCacheMock.class) {
            return instance;
        }
    }

    // =========================================================================
    // IMPLEMENTS
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cacheMock = new HashMap<String, Map<String, Object>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String region, String key) {
        return cacheMock.get(region).get(key);
    }

    /**
     * Gets the delegate.
     * 
     * @return the delegate {@inheritDoc}
     */
    @Override
    public CacheManager getDelegate() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String region, String key, Object object) {
        if (!cacheMock.containsKey(region)) {
            cacheMock.put(region, new HashMap<String, Object>());
        }
        cacheMock.get(region).put(key, object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String region, String key) {
        if (cacheMock.containsKey(region)
                && cacheMock.get(region).containsKey(key)) {
            cacheMock.get(region).remove(key);
        }
    }
}
