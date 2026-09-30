package it.pagopa.infrastructure.http.restassured.flow;

import io.restassured.filter.Filter;

import java.util.function.Consumer;

public final class FlowValidationFilterFactory {

    private FlowValidationFilterFactory() {
    }

    public static Filter create(Consumer<String> errorConsumer) {
        return (requestSpec, responseSpec, ctx) -> {
            var response = ctx.next(requestSpec, responseSpec);

            int statusCode = response.getStatusCode();

            if (isAccepted(statusCode)) {
                return response;
            }

            String payload = requestSpec.getBody() != null
                    ? requestSpec.getBody().toString()
                    : "<empty>";

            String error = """
                    HTTP %d
                    Method: %s
                    Endpoint: %s
                    Payload: %s
                    Response: %s
                    """.formatted(
                    statusCode,
                    requestSpec.getMethod(),
                    requestSpec.getURI(),
                    payload,
                    response.getBody().asString()
            );

            errorConsumer.accept(error);

            return response;
        };
    }

    private static boolean isAccepted(int statusCode) {
        return (statusCode >= 200 && statusCode < 300)
                || (statusCode >= 400 && statusCode < 500);
    }
}
