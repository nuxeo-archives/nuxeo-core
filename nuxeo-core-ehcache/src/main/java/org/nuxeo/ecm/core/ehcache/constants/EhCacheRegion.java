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
package org.nuxeo.ecm.core.ehcache.constants;

/**
 * The Enum CacheRegion.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public enum EhCacheRegion implements CacheRegion{

    
    /** Dashboard cache region, key :<b>dashboard</b> */
    dashboard,
    

    /** Layout cache region, key :<b>layout</b> */
    layout,
    
    
    /** Webengine cache region, key :<b>webengine</b> */
    webengine;

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String regionName() {
        return this.name();
    }
    
}
