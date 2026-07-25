package it.pagopa.interop.common.infrastructure.http;

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

        // 1. Log della Request
        logRequest(requestSpec);

        // 2. Esecuzione della chiamata HTTP
        Response response = ctx.next(requestSpec, responseSpec);

        // 3. Log della Response
        logResponse(response);

        return response;
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

    private void logResponse(Response response) {
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

        // Logs response body (Sicuro: REST Assured non consuma lo stream qui)
        String responseBody = response.asString();
        if (responseBody != null && !responseBody.isBlank()) {
            logger.info("Response Body: {}", responseBody);
        }
    }
}