package it.pagopa.interop.new_arch.common.infrastructure.http.contract.engine;

import java.util.Map;

public record RequestParts(Map<String, Object> inputs, Map<String, Object> body) {
}