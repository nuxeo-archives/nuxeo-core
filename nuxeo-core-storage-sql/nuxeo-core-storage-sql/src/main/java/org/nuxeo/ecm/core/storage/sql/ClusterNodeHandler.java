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

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.jdbc.JDBCConnection;

/**
 * Encapsulates cluster node operations.
 * <p>
 * There is one cluster node handler per cluster node (repository).
 */
public class ClusterNodeHandler {

    private static final Log log = LogFactory.getLog(ClusterNodeHandler.class);

    protected final IsolatedMapperRunner runner;

    private final long clusteringDelay;

    // modified only under clusterMapper synchronization
    private volatile long clusterNodeLastInvalidationTimeMillis;

    /** Propagator of invalidations to the cluster node's mappers. */
    private final InvalidationsPropagator propagator;

    /** Cluster node id, needed at the Java level for some databases. */
    private String nodeId;


    public ClusterNodeHandler(RepositoryImpl repository, Mapper mapper) throws StorageException {
        runner = new IsolatedMapperRunner("nuxeo-cluster-"+repository.getName(), mapper);
        clusteringDelay = repository.getRepositoryDescriptor().clusteringDelay;
        propagator = new InvalidationsPropagator("cluster-" + this);
    }

    public void createNode() throws StorageException {
        nodeId = runner.open(new Callable<String>() {

            @Override
            public String call() throws Exception {
                runner.mapper.open();
                return runner.mapper.createClusterNode();
            }

        }).submit();
        processClusterInvalidationsNext();
        propagator.propagateInvalidations(Invalidations.ALL, null);
    }

    public JDBCConnection getConnection() {
        return (JDBCConnection) runner.mapper;
    }

    public void startup() throws StorageException {
         createNode();
         processClusterInvalidationsNext();
    }

    public void shutdown() throws StorageException {
        runner.close(new Callable<Void>() {

            @Override
            public Void call() throws StorageException {
                try {
                    runner.mapper.removeClusterNode();
                } finally {
                    runner.mapper.close();
                }
                return null;
            }

        }).submit();
    }

    public void connectionWasReset() throws StorageException {
        createNode();
    }

    // TODO should be called by RepositoryManagement
    public void processClusterInvalidationsNext() {
        clusterNodeLastInvalidationTimeMillis = System.currentTimeMillis()
                - clusteringDelay - 1;
    }

    /**
     * Adds an invalidation queue to this cluster node.
     */
    public void addQueue(InvalidationsQueue queue) {
        propagator.addQueue(queue);
    }

    /**
     * Removes an invalidation queue from this cluster node.
     */
    public void removeQueue(InvalidationsQueue queue) {
        propagator.removeQueue(queue);
    }

    /**
     * Propagates invalidations to all the queues of this cluster node.
     */
    public void propagateInvalidations(Invalidations invalidations,
            InvalidationsQueue skipQueue) {
        if (invalidations.isEmpty()) {
            return;
        }
        propagator.propagateInvalidations(invalidations, null);
    }

    /**
     * Receives cluster invalidations from other cluster nodes.
     */
    public Invalidations receiveClusterInvalidations()
            throws StorageException {
        final long time = System.currentTimeMillis();
        if (clusterNodeLastInvalidationTimeMillis + clusteringDelay > time) {
            return Invalidations.EMPTY;
        }
        clusterNodeLastInvalidationTimeMillis = time;
        return runner.submit(new Callable<InvalidationsRows>() {

            @Override
            public InvalidationsRows call() throws Exception {
                return runner.mapper.getClusterInvalidations(nodeId);
            }

        });
    }


    /**
     * Sends cluster invalidations to other cluster nodes.
     */
    public void sendClusterInvalidations(final Invalidations invalidations)
            throws StorageException {
        if (invalidations.isEmpty()) {
            return;
        }
        runner.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                runner.mapper.insertClusterInvalidations(invalidations, nodeId);
                return null;
            }

        });
    }


}
