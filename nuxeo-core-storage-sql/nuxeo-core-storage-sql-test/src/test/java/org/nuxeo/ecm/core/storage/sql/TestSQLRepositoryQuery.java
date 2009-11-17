/*
 * (C) Copyright 2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.storage.sql;

import org.nuxeo.ecm.core.query.test.QueryTestCase;

/**
 * @author Florent Guillaume
 */
public class TestSQLRepositoryQuery extends QueryTestCase {

    @Override
    public void deployRepository() throws Exception {
        DatabaseHelper.DATABASE.setUp();
        deployContrib("org.nuxeo.ecm.core.storage.sql.test",
                DatabaseHelper.DATABASE.getDeploymentContrib());
//        deployBundle("org.nuxeo.ecm.core.event");
    }

    @Override
    public void undeployRepository() throws Exception {
        DatabaseHelper.DATABASE.tearDown();
    }

//    public void testQueryIterable() throws Exception {
//        createDocs();
//
//        IterableQueryResult res = session.queryAndFetch("SELECT * FROM File",
//                "NXQL");
//        List<Map<String, Serializable>> l = new LinkedList<Map<String, Serializable>>();
//        for (Map<String, Serializable> x : res) {
//            l.add(x);
//        }
//        assertEquals(3, l.size());
//    }

}
