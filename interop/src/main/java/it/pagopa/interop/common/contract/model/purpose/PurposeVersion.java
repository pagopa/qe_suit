package it.pagopa.interop.common.contract.model.purpose;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.*;
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
