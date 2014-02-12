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
 */

package org.nuxeo.ecm.core.storage.sql;

import java.util.Arrays;

import org.nuxeo.common.xmap.XMap;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.ecm.core.storage.sql.RepositoryDescriptor.FieldDescriptor;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLRepositoryFactory;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

/**
 * @author Florent Guillaume
 */
public abstract class SQLBackendTestCase extends NXRuntimeTestCase {

    public Repository repository;

    public Repository repository2;

    /** Set to false for client unit tests */
    public boolean initDatabase() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.core");
        deployBundle("org.nuxeo.ecm.core.schema");
        deployBundle("org.nuxeo.ecm.core.event");
        deployBundle("org.nuxeo.ecm.core.storage.sql");
        if (initDatabase()) {
            DatabaseHelper.DATABASE.setUp();
        }
        repository = newRepository(-1, false);
    }

    protected int repoCount = 0;
    
    protected Repository newRepository(long clusteringDelay,
            boolean fulltextDisabled) throws Exception {
        RepositoryDescriptor sqlDescriptor = newDescriptor(clusteringDelay,
                fulltextDisabled);
        XMap xmap = new XMap();
        xmap.register(org.nuxeo.ecm.core.storage.sql.RepositoryDescriptor.class);
        String config = xmap.toXML(sqlDescriptor);
        String name = sqlDescriptor.name;
        org.nuxeo.ecm.core.repository.RepositoryDescriptor coreDescriptor = new org.nuxeo.ecm.core.repository.RepositoryDescriptor();
		coreDescriptor.setName(name);
		coreDescriptor.setFactoryClass(SQLRepositoryFactory.class);
		coreDescriptor.setConfigurationContent(config);
		Framework.getLocalService(RepositoryService.class).getRepositoryManager().registerRepository(coreDescriptor);
        Framework.getLocalService(RepositoryManager.class).addRepository(new org.nuxeo.ecm.core.api.repository.Repository(name));
		return RepositoryResolver.getRepository(name);
    }
    
    
    protected void closeRepository(Repository repository) {
    	Framework.getLocalService(RepositoryService.class).getRepositoryManager().releaseRepository(repository.getName());
    	Framework.getLocalService(RepositoryManager.class).removeRepository(repository.getName());
    }
    
    protected RepositoryDescriptor newDescriptor(long clusteringDelay,
            boolean fulltextDisabled) {
        RepositoryDescriptor descriptor = DatabaseHelper.DATABASE.getRepositoryDescriptor();
        descriptor.name = DatabaseHelper.DATABASE.repositoryName;
        descriptor.clusteringEnabled = clusteringDelay != -1;
        descriptor.clusteringDelay = clusteringDelay;
        FieldDescriptor schemaField1 = new FieldDescriptor();
        schemaField1.field = "tst:bignote";
        schemaField1.type = Model.FIELD_TYPE_LARGETEXT;
        FieldDescriptor schemaField2 = new FieldDescriptor();
        schemaField2.field = "tst:bignotes";
        schemaField2.type = Model.FIELD_TYPE_LARGETEXT;
        descriptor.schemaFields = Arrays.asList(schemaField1, schemaField2);
        descriptor.binaryStorePath = "testbinaries";
        descriptor.fulltextDisabled = fulltextDisabled;
        return descriptor;
    }

    @Override
    public void tearDown() throws Exception {
        closeRepository();
        if (initDatabase()) {
            DatabaseHelper.DATABASE.tearDown();
        }
        super.tearDown();
    }

    protected void closeRepository() throws Exception {
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();
        if (repository != null) {
            closeRepository(repository);
            repository = null;
        }
        if (repository2 != null) {
            closeRepository(repository2);
            repository2 = null;
        }
    }

    public boolean isSoftDeleteEnabled() {
        return ((RepositoryImpl) repository).getRepositoryDescriptor().softDeleteEnabled;
    }

    public boolean isProxiesEnabled() {
        return ((RepositoryImpl) repository).getRepositoryDescriptor().proxiesEnabled;
    }

}
