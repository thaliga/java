package org.th.spring.customscope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the beans for an {@link CustomScope}.
 */
class CustomScopeContextAttributes {

	private static final Logger logger = LoggerFactory.getLogger(CustomScopeContextAttributes.class);

	private final Map<String, Object> beans = new HashMap<>();

	/** Map from attribute name String to destruction callback Runnable */
	private final Map<String, Runnable> contextDestructionCallbacks = new LinkedHashMap<>(8);

	private Object mutex = new Object();

	/**
	 * Gets bean <code>Map</code>.
	 */
	Map<String, Object> getBeanMap() {
		return beans;
	}

	/**
	 * Expose the best available mutex for the underlying context: that is, an
	 * object to synchronize on for the underlying context.
	 *
	 * @return the session mutex to use (never {@code null})
	 */
	Object getMutexObject() {
		return mutex;
	}

	/**
	 * Clears beans and processes all bean destruction callbacks.
	 */
	void clear() {
		beans.clear();
	}

	/**
	 * Signal that the scope context has been completed.
	 * <p>
	 * Executes all scope context destruction callbacks
	 */
	void contextCompleted() {
		executeContextDestructionCallbacks();
	}

	/**
	 * Execute all callbacks that have been registered for execution after
	 * request completion.
	 */
	private void executeContextDestructionCallbacks() {
		synchronized (this.contextDestructionCallbacks) {
			final Collection<Runnable> callbacks = new ArrayList<>(this.contextDestructionCallbacks.values());
			callbacks.forEach(Runnable::run);
			this.contextDestructionCallbacks.clear();
		}
	}

	/**
	 * Register the given callback as to be executed after request completion.
	 * 
	 * @param name
	 *            the name of the attribute to register the callback for
	 * @param callback
	 *            the callback to be executed for destruction
	 */
	void registerContextDestructionCallback(String name, Runnable callback) {
		Validate.notNull(name, "Name must not be null");
		Validate.notNull(callback, "Callback must not be null");

		synchronized (this.contextDestructionCallbacks) {
			this.contextDestructionCallbacks.put(name, callback);
		}
	}
}