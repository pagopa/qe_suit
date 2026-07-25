package it.pagopa.interop.common.infrastructure.http.contract.engine;

import java.util.Map;

public record RequestParts(Map<String, Object> inputs, Map<String, Object> body) {
}