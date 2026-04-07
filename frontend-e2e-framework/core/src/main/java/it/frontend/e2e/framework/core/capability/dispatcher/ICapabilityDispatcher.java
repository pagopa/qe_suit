package it.frontend.e2e.framework.core.capability.dispatcher;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;

import java.lang.reflect.Method;

public interface ICapabilityDispatcher {
    <T> T dispatch(Method method, Object[] args, CapabilityScope scope);
}
