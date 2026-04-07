package it.frontend.e2e.framework.core.utils;

import java.util.Optional;

public class FallbackUtils {
    public static Object fallbackValue(Class<?> returnType) {
        if (Void.TYPE.equals(returnType)) return null;
        if (Boolean.TYPE.equals(returnType)) return false;
        if (Byte.TYPE.equals(returnType)) return (byte) 0;
        if (Short.TYPE.equals(returnType)) return (short) 0;
        if (Integer.TYPE.equals(returnType)) return 0;
        if (Long.TYPE.equals(returnType)) return 0L;
        if (Float.TYPE.equals(returnType)) return 0f;
        if (Double.TYPE.equals(returnType)) return 0d;
        if (Character.TYPE.equals(returnType)) return '\0';
        if (Optional.class.equals(returnType)) return Optional.empty();
        return null;
    }
}

