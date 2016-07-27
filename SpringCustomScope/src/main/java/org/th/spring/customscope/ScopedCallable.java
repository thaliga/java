package org.th.spring.customscope;

import java.util.concurrent.Callable;

/**
 * Scoped callable (registering to/unregistering from given scope).
 */
class ScopedCallable<V> extends ScopedWrapper<V> implements Callable<V> {

    private final Callable<V> callable;

    ScopedCallable(Callable<V> callable) {
        this(null, callable);
    }

    ScopedCallable(CustomScopeKey contextKey, Callable<V> callable) {
        super(contextKey);
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        return executeWithException();
    }

    @Override
    protected V internalExecute() throws Exception {
        return callable.call();
    }
}
