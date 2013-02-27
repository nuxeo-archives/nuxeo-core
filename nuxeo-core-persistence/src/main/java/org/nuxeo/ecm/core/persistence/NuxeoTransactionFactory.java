package org.nuxeo.ecm.core.persistence;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;

public class NuxeoTransactionFactory extends CMTTransactionFactory {

    private static final long serialVersionUID = 1L;

    @Override
    public ConnectionReleaseMode getDefaultReleaseMode() {
        return ConnectionReleaseMode.AFTER_TRANSACTION;
    }

}
