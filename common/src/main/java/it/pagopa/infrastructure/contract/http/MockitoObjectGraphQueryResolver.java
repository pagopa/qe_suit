package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.objectgraph.ObjectGraphQuery;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

final class MockitoObjectGraphQueryResolver implements ObjectGraphQueryResolver {

    @Override
    public <T> ObjectGraphQuery resolve(Class<T> rootType, TargetExpression<T> expression) {
        Objects.requireNonNull(rootType, "rootType must not be null");
        Objects.requireNonNull(expression, "expression must not be null");

        RecordingContext context = new RecordingContext();
        T root = context.createRootMock(rootType);
        try {
            expression.select(root);
        } catch (ContractHttpException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContractHttpException("Unsupported or non-deterministic target expression", exception);
        }
        return context.toQuery();
    }

    private static final class RecordingContext {
        private final List<Function<ObjectGraphQuery, ObjectGraphQuery>> steps = new java.util.ArrayList<>();
        private final Map<Object, Class<?>> listElementTypes = new IdentityHashMap<>();
        private final Map<Object, Type> mapValueTypes = new IdentityHashMap<>();
        private Object expectedReceiver;
        private boolean terminalReached;

        <T> T createRootMock(Class<T> rootType) {
            return createMock(rootType, null);
        }

        @SuppressWarnings("unchecked")
        private <T> T createMock(Class<T> type, Class<?> listElementType) {
            Answer<Object> answer = this::onInvocation;
            T mock = Mockito.mock(type, Mockito.withSettings().defaultAnswer(answer));
            if (listElementType != null) {
                listElementTypes.put(mock, listElementType);
            }
            if (expectedReceiver == null) {
                expectedReceiver = mock;
            }
            return mock;
        }

        private Object onInvocation(InvocationOnMock invocation) {
            Object receiver = invocation.getMock();
            Method method = invocation.getMethod();

            if (method.getDeclaringClass() == Object.class) {
                try {
                    return Mockito.RETURNS_DEFAULTS.answer(invocation);
                } catch (Throwable throwable) {
                    throw new ContractHttpException("Failed to evaluate Object method on tracking mock", throwable);
                }
            }
            if (terminalReached || receiver != expectedReceiver) {
                throw new ContractHttpException("Target expression must contain exactly one navigation path");
            }
            if (isListGet(method)) {
                return handleListGet(receiver, invocation);
            }
            if (isMapGet(method)) {
                return handleMapGet(receiver, invocation);
            }
            if (!isPropertyGetter(method)) {
                throw new ContractHttpException("Unsupported target navigation method: " + method);
            }
            return handleProperty(method);
        }

        private Object handleProperty(Method method) {
            steps.add(query -> query.property(method));
            Class<?> returnType = method.getReturnType();
            if (returnType == Void.TYPE || isTerminal(returnType)) {
                terminalReached = true;
                expectedReceiver = null;
                return Defaults.defaultValue(returnType);
            }
            if (List.class.isAssignableFrom(returnType)) {
                Class<?> elementType = resolveTypeArgument(method.getGenericReturnType(), 0, Object.class);
                Object listMock = createMock(returnType, elementType);
                expectedReceiver = listMock;
                return listMock;
            }
            if (Map.class.isAssignableFrom(returnType)) {
                Object mapMock = createMock(returnType, null);
                mapValueTypes.put(mapMock, resolveTypeArgument(method.getGenericReturnType(), 1, Object.class));
                expectedReceiver = mapMock;
                return mapMock;
            }
            Object nested = createMock(returnType, null);
            expectedReceiver = nested;
            return nested;
        }

        private Object handleListGet(Object receiver, InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            if (args.length != 1 || !(args[0] instanceof Integer index) || index < 0) {
                throw new ContractHttpException("List index navigation requires a non-negative integer");
            }
            steps.add(query -> query.index(index));
            Class<?> elementType = listElementTypes.get(receiver);
            if (elementType == null || elementType == Object.class) {
                terminalReached = true;
                expectedReceiver = null;
                return null;
            }
            if (isTerminal(elementType)) {
                terminalReached = true;
                expectedReceiver = null;
                return Defaults.defaultValue(elementType);
            }
            Object nested = createMock(elementType, null);
            expectedReceiver = nested;
            return nested;
        }

        private Object handleMapGet(Object receiver, InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            if (args.length != 1 || !(args[0] instanceof String key)) {
                throw new ContractHttpException("Map key navigation requires a string key");
            }
            steps.add(query -> query.key(key));
            Type valueType = mapValueTypes.get(receiver);
            Class<?> valueClass = rawClass(valueType);
            if (valueClass == null || valueClass == Object.class) {
                terminalReached = true;
                expectedReceiver = null;
                return null;
            }
            if (isTerminal(valueClass)) {
                terminalReached = true;
                expectedReceiver = null;
                return Defaults.defaultValue(valueClass);
            }
            if (List.class.isAssignableFrom(valueClass)) {
                Class<?> elementType = resolveTypeArgument(valueType, 0);
                Object listMock = createMock(valueClass, elementType);
                expectedReceiver = listMock;
                return listMock;
            }
            if (Map.class.isAssignableFrom(valueClass)) {
                Object mapMock = createMock(valueClass, null);
                mapValueTypes.put(mapMock, resolveTypeArgument(valueType, 1, Object.class));
                expectedReceiver = mapMock;
                return mapMock;
            }
            Object nested = createMock(valueClass, null);
            expectedReceiver = nested;
            return nested;
        }

        ObjectGraphQuery toQuery() {
            ObjectGraphQuery query = ObjectGraphQuery.root();
            for (Function<ObjectGraphQuery, ObjectGraphQuery> step : steps) {
                query = step.apply(query);
            }
            return query;
        }

        private boolean isTerminal(Class<?> type) {
            return type.isPrimitive()
                    || type.isEnum()
                    || type == Boolean.class
                    || type == Character.class
                    || Number.class.isAssignableFrom(type)
                    || CharSequence.class.isAssignableFrom(type)
                    || type == java.util.UUID.class
                    || java.time.temporal.Temporal.class.isAssignableFrom(type)
                    || java.util.Date.class.isAssignableFrom(type)
                    || type == java.net.URI.class
                    || type == java.net.URL.class;
        }

        private boolean isPropertyGetter(Method method) {
            return method.getParameterCount() == 0
                    && !method.getName().equals("getClass")
                    && (method.getName().startsWith("get") || method.getName().startsWith("is"));
        }

        private boolean isListGet(Method method) {
            return method.getName().equals("get")
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == int.class
                    && List.class.isAssignableFrom(method.getDeclaringClass());
        }

        private boolean isMapGet(Method method) {
            return method.getName().equals("get")
                    && method.getParameterCount() == 1
                    && Map.class.isAssignableFrom(method.getDeclaringClass());
        }

        private Class<?> resolveTypeArgument(Type type, int index) {
            return resolveTypeArgument(type, index, null);
        }

        private Class<?> resolveTypeArgument(Type type, int index, Class<?> fallback) {
            if (type instanceof ParameterizedType parameterizedType) {
                Type[] arguments = parameterizedType.getActualTypeArguments();
                if (index < arguments.length) return rawClass(arguments[index]);
            }
            return fallback;
        }

        private Class<?> rawClass(Type type) {
            if (type instanceof Class<?> klass) return klass;
            if (type instanceof ParameterizedType parameterizedType
                    && parameterizedType.getRawType() instanceof Class<?> klass) {
                return klass;
            }
            return null;
        }
    }

    private static final class Defaults {
        private Defaults() {
        }

        static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) {
                return null;
            }
            if (type == boolean.class) return false;
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0F;
            if (type == double.class) return 0D;
            if (type == char.class) return '\0';
            return null;
        }
    }
}
