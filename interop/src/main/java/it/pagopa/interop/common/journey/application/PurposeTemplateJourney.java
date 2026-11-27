package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import jakarta.annotation.Nullable;

public interface PurposeTemplateJourney<SELF extends PurposeTemplateJourney<SELF>> extends JourneyModule {

    /**
     * Crea un purpose template (creatore = identità corrente) per il tenant target e lo porta nello stato richiesto.
     */
    SELF createPurposeTemplate(Tenant targetTenant, PurposeTemplateState state);

    /**
     * Crea un purpose template (creatore = identità corrente) per il tenant target, registra l'eventuale alias
     * e lo porta nello stato richiesto.
     */
    SELF createPurposeTemplate(Tenant targetTenant, PurposeTemplateState state, @Nullable String alias);

    /**
     * Collega l'e-service all'ultimo purpose template creato.
     */
    SELF linkEServiceToPurposeTemplate(EService eService);

    SELF addAnnotationToPurposeTemplateRiskAnalysisAnswer();
}

