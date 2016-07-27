package org.th.spring.customscope;

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
		// decrease visibility, should be created by contextHolder
	}
}
