/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     matic
 */
package org.nuxeo.ecm.core.test;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author matic
 *
 */
public class CoreScope implements Scope {

    protected final ThreadLocal<Cache> threadHolder =
            new ThreadLocal<Cache>() {
    };

    public interface CleanupHook<T> {
        void handleCleanup(T instance);
    }

    protected static class Cache {

        protected Map<Key<?>, ScopedProvider<?>> cached =
                new HashMap<>();

        @SuppressWarnings("unchecked")
        <T> T getOrCreate(Key<T> key, ScopedProvider<T> provider) {
            if (!cached.containsKey(key)) {
                provider.instance = provider.unscoped.get();
                cached.put(key, provider);
            }
            return (T)cached.get(key).instance;
        }

        void cleanup() {
            AssertionError errors = new AssertionError("Cannot cleanup core scope");
            for (ScopedProvider<?> each:cached.values()) {
                try {
                    cleanup(each);
                } catch (Exception cause) {
                    errors.addSuppressed(cause);
                }
            }
            if (errors.getSuppressed().length > 0) {
                throw errors;
            }
        }

        @SuppressWarnings("unchecked")
        protected <T> void cleanup(ScopedProvider<T> scoped) {
            if (scoped.unscoped instanceof CleanupHook) {
                ((CleanupHook<T>)scoped.unscoped).handleCleanup(scoped.instance);
            }
        }
    }

    protected class ScopedProvider<T> implements Provider<T> {

        protected final Key<T> key;
        protected final Provider<T> unscoped;
        protected T instance;

        ScopedProvider(Key<T> key, Provider<T> unscoped) {
            this.key = key;
            this.unscoped = unscoped;
        }

        @Override
        public T get() {
            return threadHolder.get().getOrCreate(key, this);
        }

    }
    public final static CoreScope INSTANCE = new CoreScope();

    protected CoreScope() {
        threadHolder.get();
    }

    public void enter() {
        threadHolder.set(new Cache());
    }

    public void exit() {
        try {
            threadHolder.get().cleanup();
        } finally {
            threadHolder.remove();
        }
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new ScopedProvider<T>(key, unscoped);
    }

}
