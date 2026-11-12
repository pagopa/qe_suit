package it.pagopa.send.selfcare.infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.send.common.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * API di test di Selfcare per le deleghe: crea una delega da un ente PA a un Partner Tecnologico. L'host è il Selfcare
 * di uat per tutti gli ambienti SEND, il prodotto ({@code product-id}) distingue l'ambiente.
 * La delega arriva poi a SEND con un evento, in modo asincrono.
 */
@Component
public class SelfcareDelegationsRestClient {

    private static final int CREATED = 201;
    private static final int ALREADY_ACTIVE = 409;

    private final String baseUrl;
    private final String productId;
    private final String subscriptionKey;

    public SelfcareDelegationsRestClient(
            @Value("${selfcare.delegations-test.base-url:}") String baseUrl,
            @Value("${selfcare.delegations-test.product-id:}") String productId,
            @Value("${selfcare.delegations-test.subscription-key:}") String subscriptionKey
    ) {
        this.baseUrl = baseUrl;
        this.productId = productId;
        this.subscriptionKey = subscriptionKey;
    }

    /** Crea, o riattiva se cancellata, la delega dell'ente {@code delegator} al PT {@code partner}; 409 se è già attiva. */
    public void createPtDelegation(User delegator, User partner) {
        createPtDelegation(delegator.getOrganizationId(), delegator.getOrganization(),
                partner.getOrganizationId(), partner.getOrganization());
    }

    /** Come {@link #createPtDelegation(User, User)}, con id e nome degli enti su Selfcare. */
    public void createPtDelegation(String delegatorId, String delegatorName, String partnerId, String partnerName) {
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "from", delegatorId,
                        "to", partnerId,
                        "institutionFromName", delegatorName,
                        "institutionToName", partnerName,
                        "productId", productId,
                        "type", "PT"))
                .post("/delegations");
        if (response.statusCode() != CREATED && response.statusCode() != ALREADY_ACTIVE) {
            throw new IllegalStateException("Creazione della delega PT su Selfcare fallita: HTTP " + response.statusCode()
                    + " " + response.asString());
        }
    }
}
