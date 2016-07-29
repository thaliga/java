package org.th.spring.customscope;

import java.util.concurrent.Callable;

public class CustomScopeExecutor {

	public static void execute(Runnable runnable) {
		new ScopedRunnable(runnable).execute();
	}

	public static <V> V execute(Callable<V> callable) {
		return new ScopedCallable<>(callable).execute();
	}

	public static CustomScopeKey createContext() {
		return CustomScopeContextHolder.createContext();
	}
}
