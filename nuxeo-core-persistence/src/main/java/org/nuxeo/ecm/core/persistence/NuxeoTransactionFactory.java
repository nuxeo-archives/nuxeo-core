package org.nuxeo.ecm.core.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.ejb.transaction.JoinableCMTTransactionFactory;
import org.hibernate.jdbc.JDBCContext;

/**
 * Transaction factory to return {@link NuxeoTransaction} in instances that
 * remove the autocommit flag on managed JDBC connections.
 *
 * @since 5.7
 */
public class NuxeoTransactionFactory extends JoinableCMTTransactionFactory {

    @Override
    public Transaction createTransaction(JDBCContext jdbcContext,
            Context transactionContext) throws HibernateException {
        return new NuxeoTransaction(jdbcContext, transactionContext);
    }
}
