package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.purpose.infrastructure.BffPurposeRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static it.pagopa.interop.suite.contract.BffPurposeContractSetup.CONSUMER;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffPurposeContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffPurposeRequestFactory requestFactory;
    private final EntityStore entityStore;
    private final BffPurposeContractSetup setup;

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
        this.entityStore = entityStore;
        this.setup = new BffPurposeContractSetup(interopJourney, eServiceDescriptorUseCase, entityStore);
    }

    @TestFactory
    Stream<DynamicTest> createPurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().createPurpose())
            .payload(() -> requestFactory.creationRequest(setup.createPublishedEServiceWithActiveAgreement()))
            .tests();
    }

    @TestFactory
    Stream<DynamicTest> updatePurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().updatePurpose())
            .pathParams(() -> Map.of("purposeId", setup.createDraftPurposeId()))
            .payload(asConsumer(requestFactory::updateRequest))
            .tests();
    }

    @TestFactory
    Stream<DynamicTest> createPurposeForReceiveEservice() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().createPurposeForReceiveEservice())
            .payload(() -> {
                var receive = setup.createPublishedReceiveEServiceWithActiveAgreement();
                return requestFactory.reverseCreationRequest(receive.eService(), receive.riskAnalysisId());
            })
            .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateReversePurpose() {
        return httpContractValidator
            .as(CONSUMER, UserRole.ADMIN)
            .apiCall(() -> apiClient.purposes().updateReversePurpose())
            .pathParams(() -> Map.of("purposeId", setup.createDraftReversePurposeId()))
            .payload(asConsumer(requestFactory::reverseUpdateRequest))
            .tests();
    }

    /** Imposta l'identità del consumer prima di costruire il payload (i supplier sono valutati prima dell'autenticazione). */
    private <T> Supplier<T> asConsumer(Supplier<T> supplier) {
        return () -> {
            interopJourney.withConsumer(CONSUMER, UserRole.ADMIN);
            return supplier.get();
        };
    }
}
