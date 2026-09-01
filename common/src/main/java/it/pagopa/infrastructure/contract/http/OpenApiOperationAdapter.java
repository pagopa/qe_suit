package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

final class OpenApiOperationAdapter {
    private final ObjectMapper objectMapper;

    OpenApiOperationAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Response execute(Oper operation, HttpContractRequest request) {
        bind(operation, request);
        return operation.execute(Function.identity());
    }

    private void bind(Oper operation, HttpContractRequest request) {
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

    private void bindPathParams(Oper operation, JsonNode pathParams) {
        if (pathParams == null || pathParams.isNull()) {
            return;
        }
        if (!pathParams.isObject()) {
            throw new ContractHttpException("pathParams must be a JSON object");
        }
        Iterator<Map.Entry<String, JsonNode>> fields = pathParams.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String methodName = field.getKey() + "Path";
            try {
                Method method = operation.getClass().getMethod(methodName, Object.class);
                Object value = asJavaValue(field.getValue());
                method.invoke(operation, value);
            } catch (NoSuchMethodException exception) {
                throw new ContractHttpException(
                        "Cannot bind path parameter '" + field.getKey() + "': expected method "
                                + methodName + "(Object) on " + operation.getClass().getSimpleName(),
                        exception
                );
            } catch (InvocationTargetException | IllegalAccessException exception) {
                throw new ContractHttpException("Failed to bind path parameter '" + field.getKey() + "'", exception);
            }
        }
    }

    private void applyReqSpec(Oper operation, Consumer<RequestSpecBuilder> customizer) {
        try {
            Method method = operation.getClass().getMethod("reqSpec", Consumer.class);
            method.invoke(operation, customizer);
        } catch (NoSuchMethodException exception) {
            throw new ContractHttpException("Unsupported operation shape: missing reqSpec(Consumer<RequestSpecBuilder>)", exception);
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new ContractHttpException("Failed to customize operation request specification", exception);
        }
    }

    private String toJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception exception) {
            throw new ContractHttpException("Failed to serialize payload JsonNode", exception);
        }
    }

    private Object asJavaValue(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isTextual()) return node.textValue();
        if (node.isIntegralNumber()) return node.numberValue();
        if (node.isFloatingPointNumber()) return node.numberValue();
        if (node.isBoolean()) return node.booleanValue();
        return node.toString();
    }
}
