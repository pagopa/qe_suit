package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.purpose.infrastructure.BffPurposeRequestFactory;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffPurposeContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;
    private static final Tenant CONSUMER = Tenant.COMUNE_DI_POZZALLO;

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffPurposeRequestFactory requestFactory;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;

    public BffPurposeContractTest(ApiClient apiClient,
        @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
        InteropJourney interopJourney,
        BffPurposeRequestFactory requestFactory,
        EServiceDescriptorUseCase eServiceDescriptorUseCase,
        EntityStore entityStore) {
            this.apiClient = apiClient;
            this.httpContractValidator = httpContractValidator;
            this.interopJourney = interopJourney;
            this.requestFactory = requestFactory;
            this.eServiceDescriptorUseCase = eServiceDescriptorUseCase;
            this.entityStore = entityStore;
    }

    @TestFactory
    Stream<DynamicTest> createPurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().createPurpose())
            .payload(() -> requestFactory.creationRequest(createPublishedEServiceWithActiveAgreement()))
            .tests();
    }
    @TestFactory
    Stream<DynamicTest> updatePurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().updatePurpose())
            .pathParams(() -> Map.of("purposeId", createDraftPurposeId()))
            .payload(asConsumer(requestFactory::updateRequest))
            .tests();
    }
    @TestFactory
    Stream<DynamicTest> createPurposeForReceiveEservice() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().createPurposeForReceiveEservice())
            .payload(() -> {
                ReceiveEServiceSetup setup = createPublishedReceiveEServiceWithActiveAgreement();
                return requestFactory.reverseCreationRequest(setup.eService(), setup.riskAnalysisId());
            })
            .tests();
    }
    @TestFactory
    Stream<DynamicTest> updateReversePurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().updateReversePurpose())
            .pathParams(() -> Map.of("purposeId", createDraftReversePurposeId()))
            .payload(asConsumer(requestFactory::reverseUpdateRequest))
            .tests();
    }
    // NB: il validator materializza prima il payload e poi i path params:
    // il payload prepara il setup, i path params ne leggono gli id dall'EntityStore.
    @TestFactory
    Stream<DynamicTest> createPurposeFromTemplate() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().createPurposeFromTemplate())
            .payload(() -> {
                PurposeTemplate purposeTemplate = prepareTemplateSetup();
                return requestFactory.creationFromTemplateRequest(entityStore.getLastOrThrow(EService.class));
            })
            .pathParams(() -> Map.of("purposeTemplateId", entityStore.getLastOrThrow(PurposeTemplate.class).getId()))
            .tests();
    }
    @TestFactory
    Stream<DynamicTest> patchUpdatePurposeFromTemplate() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().patchUpdatePurposeFromTemplate())
            .payload(() -> {
                createDraftPurposeFromTemplate();
                return requestFactory.patchFromTemplateRequest();
            })
            .pathParams(() -> Map.of(
                "purposeTemplateId", entityStore.getLastOrThrow(PurposeTemplate.class).getId(),
                "purposeId", entityStore.getLastOrThrow(Purpose.class).getId()
            ))
            .tests();
    }
    /** Imposta l'identità del consumer prima di costruire il payload (i supplier sono valutati prima dell'autenticazione). */
    private <T> Supplier<T> asConsumer(Supplier<T> supplier) {
        return () -> {
            interopJourney.withConsumer(CONSUMER, UserRole.ADMIN);
            return supplier.get();
        };
    }    private EService createPublishedEService(boolean receiveMode) {
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

    private EService createPublishedEServiceWithActiveAgreement() {
        EService eService = createPublishedEService(false);
        activateConsumerAgreement(eService);
        return eService;
    }

    private ReceiveEServiceSetup createPublishedReceiveEServiceWithActiveAgreement() {
        EService eService = createPublishedEService(true);
        UUID riskAnalysisId = entityStore.getLastOrThrow(EServiceRiskAnalysis.class).getId();
        activateConsumerAgreement(eService);
        return new ReceiveEServiceSetup(eService, riskAnalysisId);
    }

    private void activateConsumerAgreement(EService eService) {
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkAgreement(eService, AgreementState.ACTIVE);
    }

    private UUID createDraftPurposeId() {
        EService eService = createPublishedEServiceWithActiveAgreement();
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkPurpose(eService, PurposeVersionState.DRAFT);
        return entityStore.getLastOrThrow(Purpose.class).getId();
    }

    private UUID createDraftReversePurposeId() {
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
    private PurposeTemplate prepareTemplateSetup() {
        EService eService = createPublishedEService(false);

        interopJourney
            .withProducer(PRODUCER, UserRole.ADMIN)
            .createPurposeTemplate(CONSUMER, PurposeTemplateState.PUBLISHED)
            .linkEServiceToPurposeTemplate(eService);

        activateConsumerAgreement(eService);
        return entityStore.getLastOrThrow(PurposeTemplate.class);
    }

    private void createDraftPurposeFromTemplate() {
        PurposeTemplate purposeTemplate = prepareTemplateSetup();
        EService eService = entityStore.getLastOrThrow(EService.class);
        interopJourney
            .withConsumer(CONSUMER, UserRole.ADMIN)
            .linkPurposeFromTemplate(purposeTemplate, eService, PurposeVersionState.DRAFT);
    }    private record ReceiveEServiceSetup(EService eService, UUID riskAnalysisId) {
    }
}

