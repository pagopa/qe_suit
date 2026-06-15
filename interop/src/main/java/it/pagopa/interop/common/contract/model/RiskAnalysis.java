package it.pagopa.interop.common.contract.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class RiskAnalysis implements TestModel {

    private final String title;
    private final UUID id = UUID.randomUUID();

    @Delegate
    private final RiskAnalysisFormSeed form;

    @Override
    public UUID getId() {
        return id;
    }
}
