package org.th.spring.customscope;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * Custom {@link Scope} which represents shared beans(state) between multiple threads working on the same logical block. As this
 * logical block is not tied to any particular event (like incoming http request from a browser), <i>it should be started and closed programmatically</i>.
 * </br>
 * 
 * 
 * <ul><li>To start a new scope use: {@linkplain contextHolder#createContext()}. This should be called by the 'coordinator' thread.
 * <li>To register the current thread to the scope: {@linkplain contextHolder#registerThreadInContext(ScopeKey)} Multiple threads can be registered
 * to the same context, but one thread can be registered only to the one context. The 'coordinator' and 'children' threads should call it when they want to share some data.
 * <li>To deregister a thread from the actual context: {@linkplain contextHolder#deregisterThreadFromContext()}
 * <li>To delete a context: {@link contextHolder#deleteContext(ScopeKey)}. This should be called by the 'coordinator' thread.
 * </ul></li>
 */
public class CustomScope implements Scope {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomScope.class);

    @Override
    public Object get(String name, ObjectFactory<?> factory) {
        CustomScopeContextAttributes scopeAttributes = getScopeAttributes();
        if (scopeAttributes == null) {
            return null;
        }

        synchronized (scopeAttributes.getMutexObject()) {
            Map<String, Object> beans = getScopeAttributes().getBeanMap();
            Object result = null;
            if (!beans.containsKey(name)) {
                result = factory.getObject();
                beans.put(name, result);
            } else {
                result = beans.get(name);
            }
            return result;
        }
    }

    @Override
    public Object remove(String name) {
        CustomScopeContextAttributes scopeAttributes = getScopeAttributes();

        synchronized (scopeAttributes.getMutexObject()) {
            Map<String, Object> beans = scopeAttributes.getBeanMap();
            Object result = null;
            if (beans.containsKey(name)) {
                result = beans.get(name);
                beans.remove(name);
            }
            return result;
        }
    }

    private CustomScopeContextAttributes getScopeAttributes() {
        return CustomScopeContextHolder.getContextAttributes();
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        logger.debug("Registering destruction callback for ["+name+"]");
        getScopeAttributes().registerContextDestructionCallback(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return CustomScopeContextHolder.getContextId();
    }
}