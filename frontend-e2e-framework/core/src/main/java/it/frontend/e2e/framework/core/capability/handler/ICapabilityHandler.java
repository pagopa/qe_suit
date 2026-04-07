package it.frontend.e2e.framework.core.capability.handler;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;

import java.lang.reflect.Method;

public interface ICapabilityHandler {
    boolean canHandle(Method method);
    <T> T handle(Method method, Object[] args, CapabilityScope scope);
}
