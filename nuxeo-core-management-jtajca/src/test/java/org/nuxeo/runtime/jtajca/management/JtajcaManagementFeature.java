package org.nuxeo.runtime.jtajca.management;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.junit.runners.model.FrameworkMethod;
import org.nuxeo.ecm.core.management.jtajca.CoreSessionMonitor;
import org.nuxeo.ecm.core.management.jtajca.DatabaseConnectionMonitor;
import org.nuxeo.ecm.core.management.jtajca.Defaults;
import org.nuxeo.ecm.core.management.jtajca.StorageConnectionMonitor;
import org.nuxeo.ecm.core.management.jtajca.TransactionMonitor;
import org.nuxeo.ecm.core.storage.sql.DatabaseHelper;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.TransactionalFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.SimpleFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import com.google.inject.Binder;

@Features({ TransactionalFeature.class, CoreFeature.class })
@Deploy({ "org.nuxeo.runtime.metrics", "org.nuxeo.ecm.core.management.jtajca" })
public class JtajcaManagementFeature extends SimpleFeature {

    protected DatabaseConnectionMonitor databaseMonitor;

    protected StorageConnectionMonitor storageMonitor;

    protected TransactionMonitor txMonitor;

    protected CoreSessionMonitor sessionMonitor;

    protected ObjectName nameOf(Class<?> itf, String name) {
        try {
            return new ObjectName(Defaults.instance.name(itf, name));
        } catch (MalformedObjectNameException cause) {
            throw new AssertionError("Cannot name monitor", cause);
        }
    }

    protected <T> T bind(Binder binder, MBeanServer mbs, Class<T> type,
            String name) {
        T instance = type.cast(JMX.newMXBeanProxy(mbs, nameOf(type, name), type));
        binder.bind(type).toInstance(instance);
        return instance;
    }

    @Override
    public void configure(FeaturesRunner runner, Binder binder) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        databaseMonitor = bind(binder, mbs, DatabaseConnectionMonitor.class,
                "NuxeoTestDS");
        storageMonitor = bind(binder, mbs, StorageConnectionMonitor.class,
                DatabaseHelper.DATABASE.repositoryName);
        txMonitor = bind(binder, mbs, TransactionMonitor.class, "default");
        sessionMonitor = bind(binder, mbs, CoreSessionMonitor.class, "default");
        try {
            binder.bind(TransactionManager.class).toInstance(
                    TransactionHelper.lookupTransactionManager());
        } catch (NamingException cause) {
            throw new AssertionError("Cannot access to tx manager", cause);
        }

    }

    @Override
    public void beforeMethodRun(FeaturesRunner runner, FrameworkMethod method,
            Object test) throws Exception {
        txMonitor.toggle();
    }

    @Override
    public void afterMethodRun(FeaturesRunner runner, FrameworkMethod method,
            Object test) throws Exception {
        txMonitor.toggle();
    }
}
