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

/**
 * The Interface CacheAdminCenter.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public interface CacheAdminCenter extends Serializable {

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================
    /** The NAME. */
    String NAME = "cacheAdminCenter";
    
    
    // =========================================================================
    // METHODS
    // =========================================================================

    /**
     * Clean all cache regions
     */
    void cleanAll();
    
    /**
     * Clean an specific cache region.
     *
     * @param regionName the cache region name
     */
    void cleanCache(final String regionName);
    
    
    // =========================================================================
    // DATAS
    // =========================================================================
    
    /**
     * Allow to grab all regions names.
     *
     * @return the list of cache region name
     */
    String[] regionsNames();
    
}
