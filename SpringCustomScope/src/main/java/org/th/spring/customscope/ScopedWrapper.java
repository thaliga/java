package org.th.spring.customscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scoped wrapper (registering to/unregistering from given scope).
 */
abstract class ScopedWrapper<V> {

	private static final Logger logger = LoggerFactory.getLogger(ScopedWrapper.class);

	private final CustomScopeKey contextKey;

	protected ScopedWrapper(CustomScopeKey contextKey) {
		this.contextKey = contextKey;
	}

	protected V execute() {
		try {
			return executeWithException();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Checked exception occured: ", e);
			throw new RuntimeException(e);
		}
	}

	protected V executeWithException() throws Exception {
		boolean isNotRegistered = CustomScopeContextHolder.getContextKey() == null;
		if (contextKey != null) {
			try {
				if (isNotRegistered) {
					CustomScopeContextHolder.registerThreadInContext(contextKey);
				}
				return internalExecute();
			} finally {
				if (isNotRegistered) {
					CustomScopeContextHolder.deregisterThreadFromContext();
				}
			}
		} else {
			CustomScopeKey scope = null;
			try {
				if (isNotRegistered) {
					scope = CustomScopeContextHolder.startScope();
				}
				return internalExecute();
			} finally {
				if (isNotRegistered) {
					CustomScopeContextHolder.stopScope(scope);
				}
			}
		}
	}

	protected abstract V internalExecute() throws Exception;
}
