package org.nuxeo.ecm.core.storage.sql.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.nuxeo.ecm.core.storage.sql.Mapper;

public class JDBCMapperConnector implements InvocationHandler {

    protected final Mapper mapper;

    protected JDBCMapperConnector(Mapper mapper) {
        this.mapper = mapper;
    }

    protected Object doInvoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(mapper, args);
        } catch (InvocationTargetException cause) {
            throw cause.getTargetException();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        if (mapper.isConnected()) {
            return doInvoke(method, args);
        }
        mapper.connect();
        try {
            return doInvoke(method, args);
        } finally {
            if (mapper.isConnected()) {
                mapper.disconnect();
            }
        }
    }

    public static Mapper newConnector(Mapper mapper) {
        return (Mapper)Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), new Class<?>[] {
                        Mapper.class },
                new JDBCMapperConnector(mapper));
    }
}