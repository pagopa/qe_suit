package it.pagopa.send.bff.informal_notification.infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InformalDeliveryRestClient {

    private final String baseUrl;

    public InformalDeliveryRestClient(
            @Value("${delivery.api.base-url:${delivery.api.base-url:https://api.dev.notifichedigitali.it}}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
    }

    public Response createInformalRequest(String apiKey, Map<String, Object> payload) {
        log.info("Invio richiesta creazione comunicazione informale a {}/informal/delivery/v1/requests", baseUrl);
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-api-key", apiKey)
                .body(payload)
                .post("/informal/delivery/v1/requests");

        log.info("Risposta creazione informal request: status={}, body={}", response.getStatusCode(), response.getBody().asString());
        return response;
    }

    public Response preloadAttachments(String apiKey, List<Map<String, Object>> requests) {
        log.info("Invio richiesta preload allegati informal a {}/delivery/v1/attachments/preload", baseUrl);
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-api-key", apiKey)
                .body(requests)
                .post("/informal/delivery/v1/attachments/preload");

        log.info("Risposta preload allegati informal: status={}, body={}", response.getStatusCode(), response.getBody().asString());
        return response;
    }
}
