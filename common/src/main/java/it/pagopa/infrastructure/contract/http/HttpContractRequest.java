package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;

record HttpContractRequest(
        JsonNode payload,
        boolean payloadPresent,
        JsonNode pathParams,
        JsonNode queryParams
) {
    HttpContractRequest(JsonNode payload, boolean payloadPresent, JsonNode pathParams) {
        this(payload, payloadPresent, pathParams, null);
    }
}
