package it.pagopa.interop.new_arch.common.purpose.domain;

import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PurposeVersion implements Identifiable {
    UUID id;
    PurposeVersionState purposeVersionState;
    Integer dailyCalls;
}
