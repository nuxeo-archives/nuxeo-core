/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephane Lacoin
 */
package org.nuxeo.ecm.core.test;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.repository.RepositoryFactory;
import org.nuxeo.ecm.core.storage.sql.DatabaseHelper;
import org.nuxeo.ecm.core.storage.sql.ra.PoolingRepositoryFactory;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.test.annotations.TransactionalConfig;
import org.nuxeo.runtime.jtajca.JtaActivator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.SimpleFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

@Deploy({ "org.nuxeo.runtime.jtajca" })
@RepositoryConfig(cleanup=Granularity.METHOD, repositoryFactoryClass=PoolingRepositoryFactory.class)
public class TransactionalFeature extends SimpleFeature {

    protected TransactionalConfig txConfig;

    protected String autoactivationValue;

    protected String singledsValue;

    protected boolean txStarted;

    protected RepositoryConfig repoConfig;

    protected Class<? extends RepositoryFactory> defaultFactory;

    @Override
    public void initialize(FeaturesRunner runner) throws Exception {
        txConfig = runner.getConfig(TransactionalConfig.class);
        repoConfig = runner.getConfig(RepositoryConfig.class);
    }

    @Override
    public void start(FeaturesRunner runner) throws Exception {
        autoactivationValue = System.getProperty(JtaActivator.AUTO_ACTIVATION);
        System.setProperty(JtaActivator.AUTO_ACTIVATION, "true");
        singledsValue = System.getProperty(DatabaseHelper.SINGLEDS_PROPERTY);
        System.setProperty(DatabaseHelper.SINGLEDS_PROPERTY, "true");
    }

    @Override
    public void stop(FeaturesRunner runner) throws Exception {
        Properties props = System.getProperties();
        if (autoactivationValue != null) {
            props.put(JtaActivator.AUTO_ACTIVATION, autoactivationValue);
        } else {
            props.remove(JtaActivator.AUTO_ACTIVATION);
        }
        if (singledsValue != null) {
            props.put(DatabaseHelper.SINGLEDS_PROPERTY, singledsValue);
        } else {
            props.remove(DatabaseHelper.SINGLEDS_PROPERTY);
        }
    }

    @Override
    public void beforeRun(FeaturesRunner runner) throws Exception {
        if (repoConfig.cleanup().equals(Granularity.CLASS)) {
            begin();
        }
    }

    @Override
    public void afterRun(FeaturesRunner runner) throws Exception {
        if (repoConfig.cleanup().equals(Granularity.CLASS)) {
            commitOrRollback();
        }
    }

    @Override
    public void beforeSetup(FeaturesRunner runner) throws Exception {
        if (repoConfig.cleanup().equals(Granularity.METHOD)) {
            begin();
        }
    }

    @Override
    public void afterTeardown(FeaturesRunner runner) throws Exception {
        if (repoConfig.cleanup().equals(Granularity.METHOD)) {
            commitOrRollback();
        }
    }

    public void begin() {
        if (txStarted) {
            return;
        }
        if (txConfig.autoStart() == false) {
            return;
        }
        txStarted = TransactionHelper.startTransaction();
    }

    public void commitOrRollback() {
        if (!txStarted) {
            return;
        }
        if (TransactionHelper.isTransactionActive()) {
            if (!txStarted || txConfig.rollback()) {
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        if (TransactionHelper.isTransactionActiveOrMarkedRollback()) {
            try {
                TransactionHelper.commitOrRollbackTransaction();
            } finally {
                if (!txStarted) {
                    Logger.getLogger(TransactionalFeature.class).warn(
                            "Committing a transaction for your, please do it yourself");
                }
                txStarted = false;
            }
        }
    }
}
