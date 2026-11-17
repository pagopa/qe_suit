package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface PurposeTemplateGateway extends Plugin<Channel> {

    /**
     * Verifica il rifiuto dell'ultimo tentativo di associare un e-service o template e-service in ricezione.
     * Il tentativo deve corrispondere all'ultima chiamata effettuata in piattaforma.
     */
    void shouldFailLinkToReceiveEService();

    PurposeTemplate getPurposeTemplate(UUID purposeTemplateId);

    PurposeTemplate createPurposeTemplate(
            Tenant targetTenant, RiskAnalysisForm riskAnalysisForm, RiskAnalysisFormConfig config);

    PurposeTemplate createPurposeTemplate(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm);

    PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate);

    PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate);

    PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef);

    // Proposta di refactor: https://pagopa.atlassian.net/browse/QA-19158
    /**
     * Tenta di associare un e-service a un template di finalità senza polling.
     *
     * @param purposeTemplate il template di finalità a cui associare l'e-service
     * @param eServiceRef il riferimento all'e-service da associare
     * @return il {@link PurposeTemplate} aggiornato nel contesto se la chiamata ha esito positivo;
     *         {@code null} se la chiamata fallisce (esito non di successo)
     */
    PurposeTemplate tryLinkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef);

    // Proposta di refactor: https://pagopa.atlassian.net/browse/QA-19158
    /**
     * Tenta di associare un template di e-service a un template di finalità senza polling.
     *
     * @param purposeTemplate il template di finalità a cui associare il template di e-service
     * @param eServiceTemplateRef il riferimento al template di e-service da associare
     * @return il {@link PurposeTemplate} aggiornato nel contesto se la chiamata ha esito positivo;
     *         {@code null} se la chiamata fallisce (esito non di successo)
     */
    PurposeTemplate tryLinkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef eServiceTemplateRef);

    PurposeTemplate linkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef templateRef);

}
