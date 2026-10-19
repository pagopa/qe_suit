package it.pagopa.interop.common.kernel.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Rappresenta un'analisi del rischio associata a un EService o a un EServiceTemplate (in modalità RECEIVE).
 * <p>
 * TODO: A differenza di {@code EServiceTemplate} (che espone {@code List<EServiceRiskAnalysis> riskAnalyses}),
 * {@code EService} non modella ancora direttamente le proprie {@code EServiceRiskAnalysis} e questa classe
 * non mantiene un riferimento all'e-service padre. Valutare l'aggiunta della relazione diretta in {@code EService}
 * per evitare di perdere il legame tra {@code EServiceRiskAnalysis} ed {@code EService} nel contesto dei test.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class EServiceRiskAnalysis implements Identifiable {
    UUID id;
    String name;
    RiskAnalysisForm riskAnalysisForm;

    public EServiceRiskAnalysisRef getRef() {
        return EServiceRiskAnalysisRef.of(this.id);
    }
}
