/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 *     Benoit Delbosc
 */

package org.nuxeo.ecm.core;

/**
 * Tests about security in a context where negative ACLs are allowed.
 */
public class TestSQLRepositorySecurityNegativeAcl extends
        TestSQLRepositorySecurity {

    @Override
    protected boolean allowNegativeAcl() {
        return true;
    }

}
