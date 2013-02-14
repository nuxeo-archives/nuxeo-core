/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Configurable;
import org.nuxeo.runtime.api.ConnectionHelper;

/**
 * ConnectionProvider for Hibernate that looks up the connection in a
 * thread-local location, in order to share all connections to the database and
 * to avoid the need for XA datasources.
 *
 * @since 5.7
 */
public class NuxeoConnectionProvider implements ConnectionProvider, Configurable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Delegate to do a standard Hibernate ConnectionProvider when no Nuxeo
     * connection is available.
     */
    protected DatasourceConnectionProviderImpl dscp;

    /**
     * The application-server-specific JNDI name for the datasource.
     */
    protected String dataSourceName;

    @Override
    public void configure(Map props) throws HibernateException {
        dscp = new DatasourceConnectionProviderImpl();
        dscp.configure(props);
        dataSourceName = (String) props.get(AvailableSettings.DATASOURCE);
    }

    @Override
    public Connection getConnection() throws SQLException {
        // try single-datasource non-XA mode
        Connection connection = ConnectionHelper.getConnection(dataSourceName);
        if (connection == null) {
            // standard datasource usage
            connection = dscp.getConnection();
        }
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException();
    }

}
