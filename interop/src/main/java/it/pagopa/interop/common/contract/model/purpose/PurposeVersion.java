package it.pagopa.interop.common.contract.model.purpose;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurposeVersion implements TestModel {
    private UUID id;
    private PurposeVersionState purposeVersionState;
    private Integer dailyCalls;

}
