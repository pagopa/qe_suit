package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import java.util.UUID;

/** Setup helpers (journey steps) used by {@link BffPurposeContractTest}. */
class BffPurposeContractSetup {

    static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;
    static final Tenant CONSUMER = Tenant.COMUNE_DI_POZZALLO;

    private final InteropJourney interopJourney;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;

    BffPurposeContractSetup(InteropJourney interopJourney,
        EServiceDescriptorUseCase eServiceDescriptorUseCase,
        EntityStore entityStore) {
        this.interopJourney = interopJourney;
        this.eServiceDescriptorUseCase = eServiceDescriptorUseCase;
        this.entityStore = entityStore;
    }

    record ReceiveEServiceSetup(EService eService, UUID riskAnalysisId) {
    }

    EService createPublishedEService(boolean receiveMode) {
        EServiceCreationCommand eServiceCommand = eServiceDescriptorUseCase.getDefaultEServiceCreationCommand(false);
        UpdateEServiceDescriptorCommand updateCommand =
            eServiceDescriptorUseCase.getUpdateEServiceDescriptorCommand(10, 20);

        if (receiveMode) {
            eServiceCommand.mode(EServiceMode.RECEIVE);
            // La risk analysis dell'e-service RECEIVE va aggiunta prima della pubblicazione
            interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .addRiskAnalysis()
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);
        } else {
            interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);
        }

        return entityStore.getLastOrThrow(EService.class);
    }

    EService createPublishedEServiceWithActiveAgreement() {
        EService eService = createPublishedEService(false);
        activateConsumerAgreement(eService);
        return eService;
    }

    ReceiveEServiceSetup createPublishedReceiveEServiceWithActiveAgreement() {
        EService eService = createPublishedEService(true);
        UUID riskAnalysisId = entityStore.getLastOrThrow(EServiceRiskAnalysis.class).getId();
        activateConsumerAgreement(eService);
        return new ReceiveEServiceSetup(eService, riskAnalysisId);
    }

    void activateConsumerAgreement(EService eService) {
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkAgreement(eService, AgreementState.ACTIVE);
    }

    UUID createDraftPurposeId() {
        EService eService = createPublishedEServiceWithActiveAgreement();
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkPurpose(eService, PurposeVersionState.DRAFT);
        return entityStore.getLastOrThrow(Purpose.class).getId();
    }

    UUID createDraftReversePurposeId() {
        ReceiveEServiceSetup setup = createPublishedReceiveEServiceWithActiveAgreement();
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkReversePurpose(setup.eService(), setup.riskAnalysisId(), PurposeVersionState.DRAFT);
        return entityStore.getLastOrThrow(Purpose.class).getId();
    }

    /**
     * Prepara: e-service PUBLISHED del produttore, purpose template PUBLISHED collegato all'e-service
     * e agreement ACTIVE del consumer. Restituisce il template creato.
     */
    PurposeTemplate prepareTemplateSetup() {
        EService eService = createPublishedEService(false);

        interopJourney
            .withProducer(PRODUCER, UserRole.ADMIN)
            .createPurposeTemplate(CONSUMER, PurposeTemplateState.PUBLISHED)
            .linkEServiceToPurposeTemplate(eService);

        activateConsumerAgreement(eService);
        return entityStore.getLastOrThrow(PurposeTemplate.class);
    }

    void createDraftPurposeFromTemplate() {
        PurposeTemplate purposeTemplate = prepareTemplateSetup();
        EService eService = entityStore.getLastOrThrow(EService.class);
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .createPurposeFromTemplate(purposeTemplate, eService, PurposeVersionState.DRAFT);
    }
}

