package it.pagopa.interop.new_arch.common.shared.domain;

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
