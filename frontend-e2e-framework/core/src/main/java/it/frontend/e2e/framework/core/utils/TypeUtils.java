package it.frontend.e2e.framework.core.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Optional;

public class TypeUtils {
    public static boolean isOptionalReturn(Method method) {
        return Optional.class.equals(method.getReturnType());
    }

    public static Type extractOptionalType(Method method) {
        Type generic = method.getGenericReturnType();
        if (!(generic instanceof ParameterizedType pt)) {
            throw new IllegalStateException("Optional senza tipo parametrico: " + method);
        }
        return pt.getActualTypeArguments()[0];
    }

    public static Class<?> resolveClass(Type type) {
        if (type instanceof Class<?> c) return c;
        if (type instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> raw) return raw;
        if (type instanceof WildcardType wt) {
            for (Type upper : wt.getUpperBounds()) {
                Class<?> resolved = resolveClass(upper);
                if (resolved != null && !Object.class.equals(resolved)) return resolved;
            }
            return null;
        }
        if (type instanceof TypeVariable<?> tv) {
            for (Type bound : tv.getBounds()) {
                Class<?> resolved = resolveClass(bound);
                if (resolved != null && !Object.class.equals(resolved)) return resolved;
            }
            return null;
        }
        return null;
    }
}

