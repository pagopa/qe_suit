package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.delegation.infrastructure.BffDelegationRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.DelegationState;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffDelegationContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;
    private static final Tenant DELEGATE_PRODUCER = Tenant.COMUNE_DI_COMUN_NUOVO;

    private static final Tenant CONSUMER_DELEGATOR = Tenant.COMUNE_DI_COMUN_NUOVO;
    private static final Tenant DELEGATE_CONSUMER = Tenant.COMUNE_DI_POZZALLO;

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffDelegationRequestFactory requestFactory;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;

    public BffDelegationContractTest(ApiClient apiClient,
                                     @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
                                     InteropJourney interopJourney,
                                     BffDelegationRequestFactory requestFactory,
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
    Stream<DynamicTest> rejectProducerDelegation() {
        return httpContractValidator
                .as(DELEGATE_PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.producerDelegations().rejectProducerDelegation())
                .pathParams(() -> Map.of("delegationId", createPendingProducerDelegationId()))
                .payload(requestFactory::rejectionRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> rejectConsumerDelegation() {
        return httpContractValidator
                .as(DELEGATE_CONSUMER, UserRole.ADMIN)
                .apiCall(() -> apiClient.consumerDelegations().rejectConsumerDelegation())
                .pathParams(() -> Map.of("delegationId", createPendingConsumerDelegationId()))
                .payload(requestFactory::rejectionRequest)
                .tests();
    }

    private UUID createPendingProducerDelegationId() {
        UUID eServiceId = interopJourney
            .withProducer(PRODUCER, UserRole.ADMIN)
            .createEService(EServiceDescriptorState.PUBLISHED)
            .get(EService.class)
            .getId();
        ProducerDelegationCreationCommand delegationCommand = ProducerDelegationCreationCommand.builder()
            .delegator(Tenant.fromOrganizationId(PRODUCER.getOrganizationId()))
            .delegate(Tenant.fromOrganizationId(DELEGATE_PRODUCER.getOrganizationId()))
            .targetState(DelegationState.WAITING_FOR_APPROVAL)
            .eServiceRef(EServiceRef.of(eServiceId))
            .build();
        return interopJourney
            .createProducerDelegation(delegationCommand)
            .get(Delegation.class)
            .getId();
    }

    private UUID createPendingConsumerDelegationId() {
        EService eService = createPublishedEService();
        interopJourney
                .withConsumer(CONSUMER_DELEGATOR, UserRole.ADMIN)
                .createConsumerDelegation(TenantRef.of(DELEGATE_CONSUMER.getOrganizationId()), eService.getRef());
        return entityStore.getLastOrThrow(Delegation.class).getId();
    }

    private EService createPublishedEService() {
        EServiceCreationCommand eServiceCommand = eServiceDescriptorUseCase.getDefaultEServiceCreationCommand(false);
        UpdateEServiceDescriptorCommand updateCommand =
                eServiceDescriptorUseCase.getUpdateEServiceDescriptorCommand(10, 20);

        interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        return entityStore.getLastOrThrow(EService.class);
    }
}

