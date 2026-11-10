package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceInstanceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceInstanceLabelUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.UpdateEServiceTemplateInstanceDescriptorQuotasSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.interop.m2m.v3.infrastructure.config.M2Mv3ApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, M2Mv3ApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class M2Mv3EServiceTemplateContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final EServiceTemplateInstanceUseCase instanceUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final EServiceRequestFactory requestFactory;

    public M2Mv3EServiceTemplateContractTest(
            ApiClient apiClient,
            @Qualifier("m2mV3InteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            EServiceTemplateInstanceUseCase instanceUseCase,
            EServiceDescriptorUseCase descriptorUseCase,
            EServiceRequestFactory requestFactory,
            CurrentChannel<Channel> currentChannel
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.instanceUseCase = instanceUseCase;
        this.descriptorUseCase = descriptorUseCase;
        this.requestFactory = requestFactory;
        currentChannel.setCurrentChannel(Channel.M2M_V3);
    }

    /**
     * Crea un e-service da template con il primo descrittore in stato DRAFT.
     * Precondizioni BFF: template con una versione PUBLISHED; nessuna istanza preesistente necessaria.
     */
    @TestFactory
    Stream<DynamicTest> createEServiceInstanceFromTemplate() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().createEServiceInstanceFromTemplate())
                .pathParams(() -> Map.of("templateId", createPublishedEServiceTemplateId()))
                .payload(() -> new InstanceEServiceSeed()
                        .isSignalHubEnabled(false)
                        .isConsumerDelegable(false)
                        .isClientAccessDelegable(false)
                        .instanceLabel("istanza"))
                // All fields are optional, but the body is required and null is not allowed.
                .scenario(FuzzScenario.REPLACED_WITH_NULL, response -> response.then().statusCode(400))
                .scenario(FuzzScenario.REMOVED, response -> response.then().statusCode(200))
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(400),
                        List.of(seed -> seed)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        response -> response.then().statusCode(200),
                        List.of(InstanceEServiceSeed::getInstanceLabel)
                )
                .tests();
    }

    /**
     * Crea un nuovo descrittore DRAFT per un e-service derivato da template.
     * Precondizioni BFF: template con versione PUBLISHED e relativa istanza con il primo descrittore PUBLISHED,
     * senza descrittori DRAFT o WAITING_FOR_APPROVAL. Non serve una nuova versione del template.
     */
    @TestFactory
    Stream<DynamicTest> createTemplateInstanceDescriptor() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().createTemplateInstanceDescriptor())
                .pathParams(() -> Map.of("eserviceId", createPublishedEServiceInstanceId()))
                .payload(() -> new EServiceInstanceDescriptorSeed()
                        .audience(List.of("QA"))
                        .dailyCallsPerConsumer(1)
                        .dailyCallsTotal(10)
                        .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC))
                .scenario(FuzzScenario.REPLACED_WITH_NULL, response -> response.then().statusCode(400))
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(200),
                        List.of(
                                seed -> seed.getAudience().get(0),
                                EServiceInstanceDescriptorSeed::getAgreementApprovalPolicy
                        )
                )
                .targets(
                        List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzScenario.REPLACED_WITH_BLANK_STRING),
                        response -> response.then().statusCode(200),
                        List.of(seed -> seed.getAudience().get(0))
                )
                .tests();
    }

    /**
     * Modifica l'etichetta di un e-service derivato da template dopo la pubblicazione.
     * Precondizioni BFF: template con versione PUBLISHED e relativa istanza con il primo descrittore PUBLISHED,
     * dopo la configurazione dei dati del descrittore e dell'interfaccia REST.
     */
    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateInstanceLabel() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateEServiceTemplateInstanceLabel())
                .pathParams(() -> Map.of("eserviceId", createPublishedEServiceInstanceId()))
                .payload(() -> new EServiceInstanceLabelUpdateSeed().instanceLabel("nuova-label"))
                .scenario(FuzzScenario.REPLACED_WITH_NULL, response -> response.then().statusCode(400))
                .scenario(FuzzScenario.REMOVED, response -> response.then().statusCode(200))
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(400),
                        List.of(seed -> seed)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        response -> response.then().statusCode(200),
                        List.of(EServiceInstanceLabelUpdateSeed::getInstanceLabel)
                )
                .tests();
    }

    /**
     * Modifica Signal Hub, flag di delegabilita ed etichetta di un e-service derivato da template in bozza.
     * Precondizioni BFF: template con versione PUBLISHED e relativa istanza con il primo descrittore DRAFT,
     * non ancora pubblicato.
     */
    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateInstance() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateEServiceTemplateInstance())
                .pathParams(() -> Map.of("eserviceId", createDraftEServiceInstanceId()))
                .payload(() -> new UpdateEServiceTemplateInstanceSeed()
                        .isSignalHubEnabled(false)
                        .isConsumerDelegable(false)
                        .isClientAccessDelegable(false)
                        .instanceLabel("aggiornata"))
                .scenario(FuzzScenario.REPLACED_WITH_NULL, response -> response.then().statusCode(400))
                .scenario(FuzzScenario.REMOVED, response -> response.then().statusCode(200))
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(400),
                        List.of(seed -> seed)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        response -> response.then().statusCode(200),
                        List.of(UpdateEServiceTemplateInstanceSeed::getInstanceLabel)
                )
                .tests();
    }

    /**
     * POST /eserviceTemplates/eservices/{eserviceId}/descriptors/{descriptorId}:
     * modifica audience, soglie giornaliere e politica di approvazione di un descrittore DRAFT.
     * Precondizioni BFF: template con versione PUBLISHED e relativa istanza con il primo descrittore DRAFT.
     */
    @TestFactory
    Stream<DynamicTest> updateDraftDescriptorTemplateInstance() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateDraftDescriptorTemplateInstance())
                .pathParams(this::createDraftEServiceInstanceDescriptorPathParams)
                .payload(() -> new UpdateEServiceDescriptorTemplateInstanceSeed()
                        .audience(List.of("QA"))
                        .dailyCallsPerConsumer(1)
                        .dailyCallsTotal(10)
                        .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC))
                .targets(
                        List.of(FuzzScenario.REMOVED, FuzzScenario.REPLACED_WITH_BLANK_STRING),
                        response -> response.then().statusCode(200),
                        List.of(seed -> seed.getAudience().get(0))
                )
                .tests();
    }

    /**
     * POST /eserviceTemplates/eservices/{eserviceId}/descriptors/{descriptorId}/quotas:
     * modifica le soglie giornaliere complessive e per fruitore di un descrittore PUBLISHED.
     * Precondizioni BFF: template con versione PUBLISHED e relativa istanza con il primo descrittore PUBLISHED.
     */
    @TestFactory
    Stream<DynamicTest> updateTemplateInstanceDescriptorQuotas() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateTemplateInstanceDescriptorQuotas())
                .pathParams(this::createPublishedEServiceInstanceDescriptorPathParams)
                .payload(() -> new UpdateEServiceTemplateInstanceDescriptorQuotasSeed()
                        .dailyCallsPerConsumer(2)
                        .dailyCallsTotal(20))
                .tests();
    }

    private UUID createPublishedEServiceTemplateId() {
        interopJourney.switchChannel(Channel.BFF);
        try {
            return createPublishedEServiceTemplate().getId();
        } finally {
            interopJourney.resetChannel(Channel.M2M_V3);
        }
    }

    private UUID createDraftEServiceInstanceId() {
        interopJourney.switchChannel(Channel.BFF);
        try {
            return createDraftEServiceInstance().getId();
        } finally {
            interopJourney.resetChannel(Channel.M2M_V3);
        }
    }

    private Map<String, UUID> createDraftEServiceInstanceDescriptorPathParams() {
        interopJourney.switchChannel(Channel.BFF);
        try {
            EService instance = createDraftEServiceInstance();
            return Map.of(
                    "eserviceId", instance.getId(),
                    "descriptorId", instance.getLastDraftDescriptor().getId()
            );
        } finally {
            interopJourney.resetChannel(Channel.M2M_V3);
        }
    }

    private UUID createPublishedEServiceInstanceId() {
        return createPublishedEServiceInstanceDescriptorPathParams().get("eserviceId");
    }

    private Map<String, UUID> createPublishedEServiceInstanceDescriptorPathParams() {
        interopJourney.switchChannel(Channel.BFF);
        try {
            EService instance = createDraftEServiceInstance();
            EServiceDescriptor descriptor = instance.getLastDraftDescriptor();
            instance = instanceUseCase.updateEServiceInstanceDescriptor(
                    instance.getRef(), descriptor.getRef(), descriptor.getState(),
                    requestFactory.defaultUpdateDescriptorInstanceCommand(descriptor.getState())
            );
            descriptor = instance.findDescriptor(descriptor.getId());
            descriptor = descriptorUseCase.configureTemplateInstanceRESTInterface(
                    instance,
                    descriptor,
                    new RestInterfaceConfiguration(
                            List.of("https://erogatore.example/api"),
                            "Referente Erogatore",
                            "referente@erogatore.example",
                            "https://erogatore.example/contatti",
                            "https://erogatore.example/condizioni"
                    )
            );
            EServiceDescriptor publishedDescriptor = descriptorUseCase.publishDescriptor(instance, descriptor);
            assertEquals(
                    EServiceDescriptorState.PUBLISHED,
                    publishedDescriptor.getState(),
                    "The template instance must be published before the contract request"
            );
            return Map.of(
                    "eserviceId", instance.getId(),
                    "descriptorId", publishedDescriptor.getId()
            );
        } finally {
            interopJourney.resetChannel(Channel.M2M_V3);
        }
    }

    private EService createDraftEServiceInstance() {
        EService instance = instanceUseCase.createEServiceInstance(createPublishedEServiceTemplate());
        assertEquals(
                EServiceDescriptorState.DRAFT,
                instance.getLatestDescriptor().getState(),
                "The template instance must be in draft before the contract request"
        );
        return instance;
    }

    private EServiceTemplate createPublishedEServiceTemplate() {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);
    }
}
