package it.pagopa.infrastructure.openapi.seed;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class DeterministicSeedFactory {

    private static final UUID SEED_UUID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final Instant SEED_INSTANT = Instant.parse("2020-01-01T00:00:00Z");
    private static final URI SEED_URI = URI.create("https://example.org/seed");
    // Keep one nested instance of a recursive model so graph traversal sees the recursive branch.
    private static final int MAX_TYPE_OCCURRENCES_PER_BRANCH = 2;

    public <T> T create(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return type.cast(createValue(type, new LinkedHashMap<>()));
    }

    public Object create(Type type) {
        Objects.requireNonNull(type, "type must not be null");
        return createValue(type, new LinkedHashMap<>());
    }

    private Object createValue(Type type, Map<Class<?>, Integer> branchDepth) {
        Class<?> rawType = rawClass(type);
        if (rawType == String.class || rawType == CharSequence.class) return "seed";
        if (rawType == UUID.class) return SEED_UUID;
        if (rawType == Integer.class || rawType == int.class) return 1;
        if (rawType == Long.class || rawType == long.class) return 1L;
        if (rawType == Short.class || rawType == short.class) return (short) 1;
        if (rawType == Byte.class || rawType == byte.class) return (byte) 1;
        if (rawType == BigInteger.class) return BigInteger.ONE;
        if (rawType == BigDecimal.class) return BigDecimal.ONE;
        if (rawType == Float.class || rawType == float.class) return 1.0f;
        if (rawType == Double.class || rawType == double.class) return 1.0d;
        if (rawType == Boolean.class || rawType == boolean.class) return true;
        if (rawType == Character.class || rawType == char.class) return 'a';
        Object jdkValue = jdkValueType(rawType);
        if (jdkValue != null) return jdkValue;
        if (rawType.isEnum()) return firstEnumValue(rawType);
        if (rawType == Optional.class) {
            Type elementType = typeArgument(type, 0);
            return Optional.ofNullable(createValue(elementType, branchDepth));
        }
        if (rawType.isArray() || type instanceof GenericArrayType) {
            Type elementType = rawType.isArray() ? rawType.getComponentType() : ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = rawClass(elementType);
            Object array = Array.newInstance(componentClass, 1);
            Array.set(array, 0, createValue(elementType, branchDepth));
            return array;
        }
        if (Collection.class.isAssignableFrom(rawType)) return createCollection(rawType, type, branchDepth);
        if (Map.class.isAssignableFrom(rawType)) return createMap(type, branchDepth);
        if (rawType == Object.class) return "seed";
        if (rawType.isInterface() || java.lang.reflect.Modifier.isAbstract(rawType.getModifiers())) {
            throw new IllegalStateException("Cannot create deterministic seed for abstract type " + rawType.getName());
        }
        return createBean(rawType, branchDepth);
    }

    /**
     * Value-like JDK types are not JavaBeans: they get a deterministic representative instance.
     */
    private Object jdkValueType(Class<?> rawType) {
        if (rawType == Instant.class) return SEED_INSTANT;
        if (rawType == OffsetDateTime.class) return SEED_INSTANT.atOffset(ZoneOffset.UTC);
        if (rawType == ZonedDateTime.class) return SEED_INSTANT.atZone(ZoneOffset.UTC);
        if (rawType == LocalDateTime.class) return LocalDateTime.ofInstant(SEED_INSTANT, ZoneOffset.UTC);
        if (rawType == LocalDate.class) return LocalDate.ofInstant(SEED_INSTANT, ZoneOffset.UTC);
        if (rawType == LocalTime.class) return LocalTime.ofInstant(SEED_INSTANT, ZoneOffset.UTC);
        if (rawType == OffsetTime.class) return OffsetTime.ofInstant(SEED_INSTANT, ZoneOffset.UTC);
        if (rawType == Duration.class) return Duration.ofSeconds(1);
        if (rawType == Period.class) return Period.ofDays(1);
        if (rawType == java.util.Date.class) return java.util.Date.from(SEED_INSTANT);
        if (rawType == URI.class) return SEED_URI;
        if (rawType == URL.class) {
            try {
                return SEED_URI.toURL();
            } catch (MalformedURLException exception) {
                throw new IllegalStateException("Cannot create deterministic seed for " + rawType.getName(), exception);
            }
        }
        if (rawType == File.class) return new File("seed");
        return null;
    }

    private Object createCollection(Class<?> collectionType, Type declaredType, Map<Class<?>, Integer> branchDepth) {        Collection<Object> values;
        if (Set.class.isAssignableFrom(collectionType)) {
            values = new LinkedHashSet<>();
        } else if (collectionType.isInterface() || collectionType == Collection.class) {
            values = new ArrayList<>();
        } else {
            values = instantiateCollection(collectionType);
        }
        values.add(createValue(typeArgument(declaredType, 0), branchDepth));
        return values;
    }

    private Collection<Object> instantiateCollection(Class<?> collectionType) {
        try {
            Object instance = collectionType.getDeclaredConstructor().newInstance();
            if (instance instanceof Collection<?> collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> typed = (Collection<Object>) collection;
                return typed;
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot create collection seed for " + collectionType.getName(), exception);
        }
        throw new IllegalStateException("Unsupported collection type " + collectionType.getName());
    }

    private Map<Object, Object> createMap(Type declaredType, Map<Class<?>, Integer> branchDepth) {
        Map<Object, Object> values = new LinkedHashMap<>();
        Object key = createValue(typeArgument(declaredType, 0), branchDepth);
        Object value = createValue(typeArgument(declaredType, 1), branchDepth);
        values.put(key, value);
        return values;
    }

    private Object createBean(Class<?> type, Map<Class<?>, Integer> branchDepth) {
        int occurrences = branchDepth.getOrDefault(type, 0);
        if (occurrences >= MAX_TYPE_OCCURRENCES_PER_BRANCH) return null;

        Object bean = instantiateBean(type);
        branchDepth.put(type, occurrences + 1);
        try {
            List<PropertyDescriptor> properties = List.of(Introspector.getBeanInfo(type, Object.class).getPropertyDescriptors())
                    .stream()
                    .filter(property -> property.getWriteMethod() != null)
                    .sorted(Comparator.comparing(PropertyDescriptor::getName))
                    .toList();
            for (PropertyDescriptor property : properties) {
                var setter = property.getWriteMethod();
                if (setter.getParameterCount() != 1) continue;
                Object value = createValue(setter.getGenericParameterTypes()[0], branchDepth);
                try {
                    setter.invoke(bean, value);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new IllegalStateException(
                            "Cannot populate seed property " + type.getName() + "." + property.getName(),
                            exception
                    );
                }
            }
            return bean;
        } catch (java.beans.IntrospectionException exception) {
            throw new IllegalStateException("Cannot inspect seed model " + type.getName(), exception);
        } finally {
            if (occurrences == 0) branchDepth.remove(type);
            else branchDepth.put(type, occurrences);
        }
    }

    private Object instantiateBean(Class<?> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Seed model requires an accessible no-args constructor: " + type.getName(), exception);
        }
    }

    private Object firstEnumValue(Class<?> type) {
        Object[] values = type.getEnumConstants();
        if (values == null || values.length == 0) {
            throw new IllegalStateException("Cannot create seed for empty enum " + type.getName());
        }
        return values[0];
    }

    private Type typeArgument(Type type, int index) {
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] arguments = parameterizedType.getActualTypeArguments();
            if (index < arguments.length) return arguments[index];
        }
        return Object.class;
    }

    private Class<?> rawClass(Type type) {
        if (type instanceof Class<?> clazz) return clazz;
        if (type instanceof ParameterizedType parameterizedType) return rawClass(parameterizedType.getRawType());
        if (type instanceof GenericArrayType arrayType) return Array.newInstance(rawClass(arrayType.getGenericComponentType()), 0).getClass();
        if (type instanceof WildcardType wildcardType && wildcardType.getUpperBounds().length > 0) {
            return rawClass(wildcardType.getUpperBounds()[0]);
        }
        if (type instanceof TypeVariable<?> variable && variable.getBounds().length > 0) {
            return rawClass(variable.getBounds()[0]);
        }
        return Object.class;
    }
}
