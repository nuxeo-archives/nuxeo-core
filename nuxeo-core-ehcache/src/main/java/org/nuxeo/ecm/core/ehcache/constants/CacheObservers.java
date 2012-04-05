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
 * The Interface CacheObservers.
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 */
public final class CacheObservers {

    /**
     * Instantiates a new cache observers.
     */
    private CacheObservers() {
        super();
    }

    /** clean dashboard cache event. */
    public final static String CLEAN_DASHBOARD = "clean_dashboard";

    /** clean layout cache event. */
    public final static String CLEAN_LAYOUT = "clean_layout";

    /** clean webengine cache event. */
    public final static String CLEAN_WEBENGINE = "clean_webengine";

}
