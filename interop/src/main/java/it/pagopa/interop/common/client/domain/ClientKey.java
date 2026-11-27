package it.pagopa.interop.common.client.domain;

import it.pagopa.kernel.security.Key;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Jacksonized
public record ClientKey(String name, Key key) {
}
