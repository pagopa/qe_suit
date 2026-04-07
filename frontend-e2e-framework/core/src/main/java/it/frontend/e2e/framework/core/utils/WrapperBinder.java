package it.frontend.e2e.framework.core.utils;

import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

public class WrapperBinder {
    public static Optional<?> bindOptional(DefaultBinderInvocationHandler handler, Method method, Object[] args) {
        Type optionalArgType = TypeUtils.extractOptionalType(method);
        Class<?> innerType = TypeUtils.resolveClass(optionalArgType);
        if (innerType == null || !handler.isBindableType(innerType)) {
            try {
                Object value = handler.resolveCapabilityMethod(method, args, handler.getCtx());
                if (value instanceof Optional<?> optionalValue) {
                    return optionalValue;
                }
                return Optional.ofNullable(value);
            } catch (RuntimeException ex) {
                handler.getLogger().logDebug("Best-effort Optional capability resolution failed for method: " + method.getName() + " -> empty");
                return Optional.empty();
            }
        }
        try {
            Object bound = handler.bindRecursive(method, innerType, true);
            return Optional.ofNullable(bound);
        } catch (RuntimeException ex) {
            handler.getLogger().logDebug("Best-effort Optional bind failed for method: " + method.getName() + " -> empty");
            return Optional.empty();
        }
    }
}

