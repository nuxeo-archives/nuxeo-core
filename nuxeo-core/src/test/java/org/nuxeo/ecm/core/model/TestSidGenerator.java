/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bogdan Stefanescu
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.model;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.nuxeo.ecm.core.utils.SIDGenerator;

public class TestSidGenerator {

    @Test
    public void testTwoDistincts() {
        long id1= SIDGenerator.next();
        long id2 = SIDGenerator.next();
        assertNotSame(id2, id1);
    }

    @Test
    public void testOneThousandDistincts() {
        Set<Long> ids = new HashSet<Long>();
        Long[] index = new Long[1000];
        for (int i = 0; i < 1000; i++) {
            index[i]  = SIDGenerator.next();
            if (!ids.add(index[i])) {
                fail("ID already generated: " + Long.toBinaryString(index[i]) + " for " + i);
            }
        }
    }


}
