package it.pagopa.interop.common.infrastructure.restassured;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        logRequest(requestSpec);

        Response originalResponse = ctx.next(requestSpec, responseSpec);

        // Salva il body SUBITO DOPO aver ricevuto la response
        // Prima che altri filtri (come OpenApiValidationFilter) lo consumino o lancino eccezioni
        String cachedBody = null;
        try {
            cachedBody = originalResponse.asString();
        } catch (Exception e) {
            logger.warn("Could not cache response body", e);
        }

        // Prova a loggare con il body salvato
        try {
            logResponseWithCachedBody(originalResponse, cachedBody);
        } catch (Exception e) {
            logger.warn("Could not log response", e);
        }

        return originalResponse;
    }

    private void logRequest(FilterableRequestSpecification requestSpec) {
        logger.info("Request Method: {}", requestSpec.getMethod());
        logger.info("Request URI: {}", requestSpec.getURI());

        // Logs header request
        requestSpec.getHeaders().forEach(header ->
                logger.info("Request Header: {} = {}", header.getName(), header.getValue())
        );

        // Logs request body
        if (requestSpec.getBody() != null) {
            logger.info("Request Body: {}", requestSpec.getBody().toString());
        }
    }

    private void logResponseWithCachedBody(Response response, String cachedBody) {
        logger.info("Response Status Code: {}", response.getStatusCode());

        // Estrae il testo dello status (es: "OK" da "HTTP/1.1 200 OK")
        String statusLine = response.getStatusLine();
        String statusCodeStr = String.valueOf(response.getStatusCode());
        String statusText = statusLine.substring(statusLine.indexOf(statusCodeStr) + statusCodeStr.length()).trim();
        logger.info("Response Status Text: {}", statusText);

        // Logs header response
        response.getHeaders().forEach(header ->
                logger.info("Response Header: {} = {}", header.getName(), header.getValue())
        );

        // Logs response body (usando il body cachato PRIMA che altri filtri lo consumino)
        if (cachedBody != null && !cachedBody.isBlank()) {
            logger.info("Response Body: {}", cachedBody);
        }
    }
}
