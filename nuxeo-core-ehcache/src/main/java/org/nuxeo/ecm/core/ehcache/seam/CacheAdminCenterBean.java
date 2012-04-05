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

import static org.jboss.seam.ScopeType.PAGE;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * The Class CacheAdminCenterBean.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
@Name(CacheAdminCenter.NAME)
@Scope(PAGE)
public class CacheAdminCenterBean implements CacheAdminCenter{

    // =========================================================================
    // ATTRIBUTS
    // =========================================================================
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2344695765601556947L;

    /** The cache provider. */
    @In(create = true)
    private EhCacheControl ehCacheControl;
    
    // =========================================================================
    // CLEAN CACHE
    // =========================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanAll() {
        final String[] caches = ehCacheControl.regionsNames();
        
        for(String region:caches){
            ehCacheControl.cleanCache(region);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanCache(String regionName) {
        ehCacheControl.cleanCache(regionName);
    }
    
    
    // =========================================================================
    // DATAS
    // =========================================================================
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] regionsNames() {
        return ehCacheControl.regionsNames();
    }
}
