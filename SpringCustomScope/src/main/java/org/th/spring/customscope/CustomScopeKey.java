package org.th.spring.customscope;

import java.util.concurrent.Callable;

/**
 * Represents a key for a logical unit scope. Can be used to register/deregister
 * a thread to a {@link CustomScope}. This should be only created by
 * {@link contextHolder#createContext()}
 * 
 * @see contextHolder#createContext()
 * @see contextHolder#registerThreadInContext(ScopeKey)
 * @see contextHolder#deleteContext(ScopeKey)
 */
public class CustomScopeKey {

	CustomScopeKey() {
		// decreased visibility, should be created by contextHolder
	}

	public void execute(Runnable runnable) {
		new ScopedRunnable(this, runnable).execute();
	}

	public <V> V execute(Callable<V> callable) {
		return new ScopedCallable<>(this, callable).execute();
	}
}
