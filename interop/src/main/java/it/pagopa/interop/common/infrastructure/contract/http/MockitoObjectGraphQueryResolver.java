package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphQuery;
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
        private final List<Method> propertyMethods = new java.util.ArrayList<>();
        private final List<Integer> indices = new java.util.ArrayList<>();
        private final List<Boolean> isPropertyStep = new java.util.ArrayList<>();
        private final Map<Object, Class<?>> listElementTypes = new IdentityHashMap<>();
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
            if (isMapGet(method)) {
                throw new ContractHttpException("Map key navigation is not supported by ObjectGraphQuery");
            }
            if (isListGet(method)) {
                return handleListGet(receiver, invocation);
            }
            if (!isPropertyGetter(method)) {
                throw new ContractHttpException("Unsupported target navigation method: " + method);
            }
            return handleProperty(method);
        }

        private Object handleProperty(Method method) {
            propertyMethods.add(method);
            isPropertyStep.add(Boolean.TRUE);
            Class<?> returnType = method.getReturnType();
            if (returnType == Void.TYPE || returnType.isPrimitive() || returnType == String.class || Number.class.isAssignableFrom(returnType) || returnType.isEnum()) {
                terminalReached = true;
                expectedReceiver = null;
                return Defaults.defaultValue(returnType);
            }
            if (List.class.isAssignableFrom(returnType)) {
                Class<?> elementType = resolveListElementType(method.getGenericReturnType());
                Object listMock = createMock(returnType, elementType);
                expectedReceiver = listMock;
                return listMock;
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
            indices.add(index);
            isPropertyStep.add(Boolean.FALSE);
            Class<?> elementType = listElementTypes.get(receiver);
            if (elementType == null || elementType == Object.class) {
                terminalReached = true;
                expectedReceiver = null;
                return null;
            }
            if (elementType.isPrimitive() || elementType == String.class || Number.class.isAssignableFrom(elementType) || elementType.isEnum()) {
                terminalReached = true;
                expectedReceiver = null;
                return Defaults.defaultValue(elementType);
            }
            Object nested = createMock(elementType, null);
            expectedReceiver = nested;
            return nested;
        }

        ObjectGraphQuery toQuery() {
            ObjectGraphQuery query = ObjectGraphQuery.root();
            int propertyIndex = 0;
            int listIndex = 0;
            for (Boolean propertyStep : isPropertyStep) {
                if (propertyStep) {
                    query = query.property(propertyMethods.get(propertyIndex++));
                } else {
                    query = query.index(indices.get(listIndex++));
                }
            }
            return query;
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

        private Class<?> resolveListElementType(Type type) {
            if (type instanceof ParameterizedType parameterizedType) {
                Type arg = parameterizedType.getActualTypeArguments()[0];
                if (arg instanceof Class<?> klass) {
                    return klass;
                }
            }
            return Object.class;
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
