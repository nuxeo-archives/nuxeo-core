package org.nuxeo.ecm.core.persistence;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.TransactionException;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.transaction.CMTTransaction;
import org.hibernate.transaction.TransactionFactory.Context;

/**
 * Ensure that any JDBC connection managed by this transaction is not in
 * autocommit mode during the life-span of the transaction.
 *
 * @since 5.7
 */
public class NuxeoTransaction extends CMTTransaction {

	public NuxeoTransaction(JDBCContext jdbcContext, Context transactionContext) {
		super(jdbcContext, transactionContext);
	}

	protected boolean toggleAutoCommit;

	@Override
	public void begin() throws HibernateException {
		super.begin();
		try {
			toggleAutoCommit = jdbcContext.connection().getAutoCommit();
			if (toggleAutoCommit) {
				jdbcContext.connection().setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new TransactionException("Cannot reset autocommit status", e);
		}
	}

	@Override
	public void commit() throws HibernateException {
		try {
			super.commit();
		} finally {
			if (toggleAutoCommit) {
				try {
					jdbcContext.connection().setAutoCommit(true);
				} catch (SQLException e) {
					throw new TransactionException("Cannot set autocommit", e);
				}
			}
		}
	}

	@Override
	public void rollback() throws HibernateException {
		try {
			super.rollback();
		} finally {
			if (toggleAutoCommit) {
				try {
					jdbcContext.connection().setAutoCommit(true);
				} catch (SQLException e) {
					throw new TransactionException("Cannot set autocommit", e);
				}
			}
		}
	}
}
