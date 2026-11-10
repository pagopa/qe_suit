package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose getPurpose(PurposeRef purposeRef);

    Purpose createPurpose(PurposeCreateCommand createCommand);

    Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    // Proposta di refactor: https://pagopa.atlassian.net/browse/QA-19158
    /**
     * Tenta la creazione di una finalità senza polling.
     *
     * @param createCommand il comando contenente i dati per la creazione della finalità
     * @return la {@link Purpose} creata e aggiornata nel contesto se la chiamata ha esito positivo;
     *         {@code null} se la chiamata fallisce (esito non di successo)
     */
    Purpose tryCreatePurpose(PurposeCreateCommand createCommand);

    // Proposta di refactor: https://pagopa.atlassian.net/browse/QA-19158
    /**
     * Tenta la creazione di una finalità a partire da un template di finalità senza polling.
     *
     * @param purposeTemplateRef il riferimento al template di finalità
     * @param eServiceRef il riferimento all'e-service da associare
     * @return la {@link Purpose} creata e aggiornata nel contesto se la chiamata ha esito positivo;
     *         {@code null} se la chiamata fallisce (esito non di successo)
     */
    Purpose tryCreatePurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef);
}
