package it.pagopa.interop.new_arch.common.purpose.domain;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PurposeVersion implements TestModel {
    UUID id;
    PurposeVersionState purposeVersionState;
    Integer dailyCalls;
}
