package it.frontend.e2e.framework.core.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TypeUtils {

    public static boolean isOptionalReturn(Method method) {
        return Optional.class.equals(method.getReturnType());
    }

    public static Type extractOptionalType(Method method) {
        Type generic = method.getGenericReturnType();

        if (!(generic instanceof ParameterizedType pt)) {
            throw new IllegalStateException(
                    "Optional senza tipo parametrico: " + method
            );
        }

        return pt.getActualTypeArguments()[0];
    }

    public static Class<?> resolveClass(Type type) {
        if (type instanceof Class<?> c) {
            return c;
        }

        if (type instanceof ParameterizedType pt
                && pt.getRawType() instanceof Class<?> raw) {
            return raw;
        }

        if (type instanceof WildcardType wt) {
            for (Type upper : wt.getUpperBounds()) {
                Class<?> resolved = resolveClass(upper);

                if (resolved != null && !Object.class.equals(resolved)) {
                    return resolved;
                }
            }

            return null;
        }

        if (type instanceof TypeVariable<?> tv) {
            for (Type bound : tv.getBounds()) {
                Class<?> resolved = resolveClass(bound);

                if (resolved != null && !Object.class.equals(resolved)) {
                    return resolved;
                }
            }

            return null;
        }

        return null;
    }

    public static boolean isListReturn(Method method) {
        return List.class.isAssignableFrom(method.getReturnType());
    }

    public static Class<?> getListGenericType(
            Method method,
            Type boundType
    ) {
        Type genericReturnType = method.getGenericReturnType();

        if (!(genericReturnType instanceof ParameterizedType parameterizedType)) {
            throw new IllegalStateException(
                    "List return type must be parameterized: " + method
            );
        }

        Type typeArg = parameterizedType.getActualTypeArguments()[0];

        /*
         * Caso semplice:
         *
         * List<String>
         * List<MyComponent>
         */
        Class<?> resolved = resolveClass(typeArg);

        if (resolved != null) {
            return resolved;
        }

        /*
         * Caso generico:
         *
         * List<T>
         */
        if (typeArg instanceof TypeVariable<?> typeVariable) {
            resolved = resolveTypeVariable(
                    typeVariable,
                    boundType
            );

            if (resolved != null) {
                return resolved;
            }
        }

        throw new IllegalStateException(
                "Unsupported List generic type: " + typeArg
                        + " | boundType: " + boundType
                        + " | method: " + method
        );
    }

    private static Class<?> resolveTypeVariable(
            TypeVariable<?> variable,
            Type boundType
    ) {
        if (boundType == null) {
            return null;
        }

        return resolveTypeVariable(
                variable,
                boundType,
                new HashMap<>()
        );
    }

    private static Class<?> resolveTypeVariable(
            TypeVariable<?> targetVariable,
            Type currentType,
            Map<TypeVariable<?>, Type> bindings
    ) {
        Class<?> rawClass;
        Map<TypeVariable<?>, Type> currentBindings =
                new HashMap<>(bindings);

        if (currentType instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();

            if (!(rawType instanceof Class<?> clazz)) {
                return null;
            }

            rawClass = clazz;

            TypeVariable<?>[] parameters =
                    rawClass.getTypeParameters();

            Type[] arguments =
                    parameterizedType.getActualTypeArguments();

            for (int i = 0; i < parameters.length; i++) {
                Type argument = substituteTypeVariables(
                        arguments[i],
                        currentBindings
                );

                currentBindings.put(
                        parameters[i],
                        argument
                );
            }

            Type resolvedTarget =
                    currentBindings.get(targetVariable);

            if (resolvedTarget != null) {
                Class<?> resolved =
                        resolveClass(resolvedTarget);

                if (resolved != null) {
                    return resolved;
                }
            }

        } else if (currentType instanceof Class<?> clazz) {
            rawClass = clazz;

        } else {
            return null;
        }

        /*
         * Cerca nelle interfacce estese/implementate.
         */
        for (Type genericInterface : rawClass.getGenericInterfaces()) {
            Type resolvedInterface =
                    substituteTypeVariables(
                            genericInterface,
                            currentBindings
                    );

            Class<?> resolved = resolveTypeVariable(
                    targetVariable,
                    resolvedInterface,
                    currentBindings
            );

            if (resolved != null) {
                return resolved;
            }
        }

        /*
         * Cerca anche nella superclass.
         */
        Type genericSuperclass =
                rawClass.getGenericSuperclass();

        if (genericSuperclass != null) {
            Type resolvedSuperclass =
                    substituteTypeVariables(
                            genericSuperclass,
                            currentBindings
                    );

            Class<?> resolved = resolveTypeVariable(
                    targetVariable,
                    resolvedSuperclass,
                    currentBindings
            );

            if (resolved != null) {
                return resolved;
            }
        }

        return null;
    }

    private static Type substituteTypeVariables(
            Type type,
            Map<TypeVariable<?>, Type> bindings
    ) {
        if (type instanceof TypeVariable<?> variable) {
            Type resolved =
                    bindings.getOrDefault(variable, variable);

            /*
             * Potrebbero esserci più livelli:
             *
             * T -> E
             * E -> String
             */
            if (resolved instanceof TypeVariable<?>
                    && !resolved.equals(variable)) {
                return substituteTypeVariables(
                        resolved,
                        bindings
                );
            }

            return resolved;
        }

        if (type instanceof ParameterizedType parameterizedType) {
            Type[] arguments =
                    parameterizedType.getActualTypeArguments();

            Type[] resolvedArguments =
                    new Type[arguments.length];

            for (int i = 0; i < arguments.length; i++) {
                resolvedArguments[i] =
                        substituteTypeVariables(
                                arguments[i],
                                bindings
                        );
            }

            return new SimpleParameterizedType(
                    parameterizedType.getRawType(),
                    parameterizedType.getOwnerType(),
                    resolvedArguments
            );
        }

        return type;
    }

    private record SimpleParameterizedType(
            Type rawType,
            Type ownerType,
            Type[] actualTypeArguments
    ) implements ParameterizedType {

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }
}