package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

final class OpenApiOperationAdapter {

    private final ObjectMapper objectMapper;

    OpenApiOperationAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Response execute(Object operation, HttpContractRequest request) {
        bind(operation, request);
        return execute(operation);
    }

    private Response execute(Object operation) {
        try {
            Method method = operation.getClass().getMethod(
                    "execute",
                    Function.class
            );

            Object response = method.invoke(
                    operation,
                    Function.identity()
            );

            if (!(response instanceof Response)) {
                throw new ContractHttpException(
                        "Unsupported operation shape: execute(Function) did not return Response on "
                                + operation.getClass().getSimpleName()
                );
            }

            return (Response) response;

        } catch (NoSuchMethodException exception) {
            throw new ContractHttpException(
                    "Unsupported operation shape: missing execute(Function) on "
                            + operation.getClass().getSimpleName(),
                    exception
            );
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new ContractHttpException(
                    "Failed to execute operation "
                            + operation.getClass().getSimpleName(),
                    exception
            );
        }
    }

    private void bind(Object operation, HttpContractRequest request) {
        applyReqSpec(operation, reqSpec -> {
            if (request.payloadPresent()) {
                if (request.payload() == null) {
                    reqSpec.setBody("null");
                } else {
                    reqSpec.setBody(toJson(request.payload()));
                }
            }
        });

        bindPathParams(operation, request.pathParams());
    }

    private void bindPathParams(
            Object operation,
            JsonNode pathParams
    ) {
        if (pathParams == null || pathParams.isNull()) {
            return;
        }

        if (!pathParams.isObject()) {
            throw new ContractHttpException(
                    "pathParams must be a JSON object"
            );
        }

        Iterator<Map.Entry<String, JsonNode>> fields =
                pathParams.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();

            String parameterName = field.getKey();
            String methodName = parameterName + "Path";
            Method method = resolvePathParamMethod(operation, methodName, field.getValue());

            if (method == null) {
                throw new ContractHttpException(
                        "Cannot bind path parameter '"
                                + parameterName
                                + "': expected method "
                                + methodName
                                + "(...) on "
                                + operation.getClass().getSimpleName()
                );
            }

            try {
                Object value = convertValueForParameter(field.getValue(), method.getParameterTypes()[0]);
                method.invoke(operation, value);
            } catch (InvocationTargetException | IllegalAccessException exception) {
                throw new ContractHttpException(
                        "Failed to bind path parameter '"
                                + parameterName + "'",
                        exception
                );
            }
        }
    }

    private Method resolvePathParamMethod(Object operation, String methodName, JsonNode valueNode) {
        Method candidate = null;
        int bestScore = Integer.MIN_VALUE;

        for (Method method : operation.getClass().getMethods()) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                continue;
            }

            Class<?> parameterType = method.getParameterTypes()[0];
            int score = scoreParameterType(parameterType, valueNode);
            if (score > bestScore) {
                bestScore = score;
                candidate = method;
            }
        }

        return candidate;
    }

    private int scoreParameterType(Class<?> parameterType, JsonNode valueNode) {
        if (parameterType == Object.class) {
            return 1;
        }
        if (valueNode == null || valueNode.isNull()) {
            return parameterType.isPrimitive() ? 0 : 2;
        }

        if (parameterType == UUID.class && valueNode.isTextual() && isValidUuid(valueNode.asText())) {
            return 100;
        }
        if (parameterType == String.class && valueNode.isTextual()) {
            return 90;
        }
        if ((parameterType == Integer.class || parameterType == int.class) && valueNode.isIntegralNumber()) {
            return 80;
        }
        if ((parameterType == Long.class || parameterType == long.class) && valueNode.isIntegralNumber()) {
            return 80;
        }
        if ((parameterType == Double.class || parameterType == double.class
                || parameterType == Float.class || parameterType == float.class)
                && valueNode.isNumber()) {
            return 70;
        }
        if ((parameterType == Boolean.class || parameterType == boolean.class)
                && (valueNode.isBoolean()
                || "true".equalsIgnoreCase(valueNode.asText())
                || "false".equalsIgnoreCase(valueNode.asText()))) {
            return 60;
        }
        if (parameterType == BigDecimal.class && valueNode.isNumber()) {
            return 50;
        }
        if (parameterType.isEnum() && valueNode.isTextual()) {
            return 40;
        }

        return 0;
    }

    private boolean isValidUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private Object convertValueForParameter(JsonNode valueNode, Class<?> parameterType) {
        if (valueNode == null || valueNode.isNull()) {
            return null;
        }

        if (parameterType == Object.class) {
            return asJavaValue(valueNode);
        }
        if (parameterType == String.class) {
            return valueNode.isTextual() ? valueNode.textValue() : valueNode.toString();
        }
        if (parameterType == UUID.class) {
            return UUID.fromString(valueNode.asText());
        }
        if (parameterType == Integer.class || parameterType == int.class) {
            return valueNode.isNumber() ? valueNode.intValue() : Integer.valueOf(valueNode.asText());
        }
        if (parameterType == Long.class || parameterType == long.class) {
            return valueNode.isNumber() ? valueNode.longValue() : Long.valueOf(valueNode.asText());
        }
        if (parameterType == Double.class || parameterType == double.class) {
            return valueNode.isNumber() ? valueNode.doubleValue() : Double.valueOf(valueNode.asText());
        }
        if (parameterType == Float.class || parameterType == float.class) {
            return valueNode.isNumber() ? valueNode.floatValue() : Float.valueOf(valueNode.asText());
        }
        if (parameterType == Boolean.class || parameterType == boolean.class) {
            return valueNode.isBoolean() ? valueNode.booleanValue() : Boolean.valueOf(valueNode.asText());
        }
        if (parameterType == BigDecimal.class) {
            return new BigDecimal(valueNode.asText());
        }
        if (parameterType.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) parameterType, valueNode.asText());
        }

        return asJavaValue(valueNode);
    }

    private void applyReqSpec(
            Object operation,
            Consumer<RequestSpecBuilder> customizer
    ) {
        try {
            Method method = operation
                    .getClass()
                    .getMethod("reqSpec", Consumer.class);

            method.invoke(operation, customizer);

        } catch (NoSuchMethodException exception) {
            throw new ContractHttpException(
                    "Unsupported operation shape: missing "
                            + "reqSpec(Consumer<RequestSpecBuilder>) on "
                            + operation.getClass().getSimpleName(),
                    exception
            );

        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new ContractHttpException(
                    "Failed to customize operation request specification",
                    exception
            );
        }
    }

    private String toJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception exception) {
            throw new ContractHttpException(
                    "Failed to serialize payload JsonNode",
                    exception
            );
        }
    }

    private Object asJavaValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.textValue();
        }
        if (node.isIntegralNumber()) {
            return node.numberValue();
        }
        if (node.isFloatingPointNumber()) {
            return node.numberValue();
        }
        if (node.isBoolean()) {
            return node.booleanValue();
        }
        return node.toString();
    }
}