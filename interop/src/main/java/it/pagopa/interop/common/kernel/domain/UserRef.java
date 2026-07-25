package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class UserRef {
    User user;
    Tenant tenant;
}
