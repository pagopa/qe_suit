package it.pagopa.send.bff.informal_notification.infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DeliveryPushMessagesRestClient {

    private final String baseUrl;

    public DeliveryPushMessagesRestClient(
            @Value("${delivery.push.base-url}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
    }

    public String createMessage(Tenant sender, String subject, String longBody, String shortBody, String language) {
        String finalLongBody = (longBody != null && longBody.length() >= 80)
                ? longBody
                : "Gentile cittadino, questa e una comunicazione informativa di prova per la notifica bonaria relativa alla sua utenza.";

        Map<String, Object> primaryMessage = new HashMap<>();
        primaryMessage.put("subject", subject != null ? subject : "Oggetto IT");
        primaryMessage.put("longBody", finalLongBody);
        primaryMessage.put("shortBody", shortBody != null ? shortBody : "Short IT");
        primaryMessage.put("language", language != null ? language : "IT");

        Map<String, Object> payload = new HashMap<>();
        payload.put("primaryMessage", primaryMessage);
        payload.put("additionalMessage", null);

        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-pagopa-pn-cx-id", sender.getOrganizationId())
                .header("x-pagopa-pn-cx-type", "PA")
                .header("x-pagopa-pn-uid", sender.getUid())
                .body(payload)
                .post("/delivery/v1/messages");

        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            log.error("Errore creazione messaggio su delivery push: status={}, body={}", response.getStatusCode(), response.getBody().asString());
            throw new RuntimeException("Creazione messaggio fallita con status: " + response.getStatusCode() + " - " + response.getBody().asString());
        }

        String messageId = response.jsonPath().getString("messageId");
        if (messageId == null || messageId.isBlank()) {
            messageId = response.jsonPath().getString("id");
        }
        log.info("Messaggio creato su delivery push con ID: {}", messageId);
        return messageId;
    }
}

