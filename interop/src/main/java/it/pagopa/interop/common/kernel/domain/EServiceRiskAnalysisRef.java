package it.pagopa.interop.common.kernel.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record EServiceRiskAnalysisRef(@Nonnull UUID id) {
    public static EServiceRiskAnalysisRef of(UUID id) {
        return new EServiceRiskAnalysisRef(id);
    }
}

