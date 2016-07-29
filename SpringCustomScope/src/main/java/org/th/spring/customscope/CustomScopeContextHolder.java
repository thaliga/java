package org.th.spring.customscope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enables creating new {@link CustomScope} and registering/deregistering
 * threads for this scope.
 *
 * @see CustomScope
 * @see ScopedRunnable
 * @see ScopedCallable
 */
class CustomScopeContextHolder {

	private static final Logger logger = LoggerFactory.getLogger(CustomScopeContextHolder.class);

	private static final ThreadLocal<CustomScopeKey> contextThreadLocal = new ThreadLocal<>();
	private static final Map<CustomScopeKey, CustomScopeContextAttributes> contextToAttributes = new ConcurrentHashMap<>();

	/**
	 * Creates a brand new context with the given name. If there is an existing
	 * context then an {@link IllegalStateException} is thrown.
	 */
	static CustomScopeKey createContext() {
		CustomScopeKey contextKey = new CustomScopeKey();
		logger.debug("New context is created with scopeKey: {}", contextKey);

		CustomScopeContextAttributes threadScopeAttributes = contextToAttributes.get(contextKey);
		if (threadScopeAttributes == null) {
			threadScopeAttributes = new CustomScopeContextAttributes();
			contextToAttributes.put(contextKey, threadScopeAttributes);
		}
		return contextKey;
	}

	/**
	 * Registers the current thread to the context and so it can access beans
	 * shared in the scope.
	 *
	 * @param contextKey
	 *            the context key
	 */
	static void registerThreadInContext(CustomScopeKey contextKey) {
		Validate.notNull(contextKey);
		logger.debug("Thread registration to context: " + contextKey);

		CustomScopeKey threadContextKey = getContextKey();
		if (threadContextKey != null) {
			throw new IllegalStateException("Thread tried to be registered in context " + contextKey
					+ " but was already registered in " + threadContextKey);
		}

		synchronized (contextKey) {
			CustomScopeContextAttributes threadScopeAttributes = contextToAttributes.get(contextKey);
			if (threadScopeAttributes == null) {
				throw new IllegalStateException("Context (" + contextKey + ") does not exist!");
			}
			contextThreadLocal.set(contextKey);
		}
		logger.debug("Thread is registered to context: " + contextKey);
	}

	/**
	 * Gets <code>{@link CustomScopeContextAttributes}</code>.
	 */
	static CustomScopeContextAttributes getContextAttributes() {
		CustomScopeKey contextKey = getContextKey();
		if (contextKey == null) {
			throw new IllegalStateException("No context scoped attributes found for this thread.");
		}
		return contextToAttributes.get(contextKey);
	}

	/**
	 * Deletes the context and removes the contained objects from the scope.
	 *
	 * @param contextKey
	 */
	static void deleteContext(CustomScopeKey contextKey) {
		if (contextKey == null) {
			return;
		}

		synchronized (contextKey) {
			CustomScopeContextAttributes threadContextAttributes = contextToAttributes.get(contextKey);
			if (threadContextAttributes != null) {
				try {
					boolean wasRegistered = true;
					if (getContextKey() == null) {
						wasRegistered = false;
						CustomScopeContextHolder.registerThreadInContext(contextKey);
					}
					threadContextAttributes.contextCompleted();
					if (!wasRegistered) {
						CustomScopeContextHolder.deregisterThreadFromContext();
					}
				} finally {
					contextToAttributes.remove(contextKey);
					threadContextAttributes.clear();
				}
			}
		}
		logger.debug("Context " + contextKey + " was deleted. ");

		deregisterThreadFromContext();
	}

	/**
	 * Deregisters the thread from the context. Should be used when the thread
	 * is returned to the thread pool or the controller thread finished the
	 * logical work.
	 */
	static void deregisterThreadFromContext() {
		CustomScopeKey contextKey = getContextKey();
		contextThreadLocal.set(null);
		logger.debug("Thread is deregistered from context {}", contextKey);
	}

	static String getContextId() {
		CustomScopeKey contextKey = getContextKey();
		return contextKey == null ? null : contextKey.toString();
	}

	static CustomScopeKey getContextKey() {
		return contextThreadLocal.get();
	}

	static CustomScopeKey startScope() {
		CustomScopeKey contextKey = createContext();
		registerThreadInContext(contextKey);
		return contextKey;
	}

	static void stopScope(CustomScopeKey contextKey) {
		deleteContext(contextKey);
	}
}
