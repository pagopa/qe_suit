package it.pagopa.interop.domain.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
@Getter
public class RiskAnalysis extends AbstractModel {

    private final String title;

    @Delegate
    private final RiskAnalysisFormSeed form;

    @Override
    public String getUniqueIdentifier() {
        return title;
    }
}
