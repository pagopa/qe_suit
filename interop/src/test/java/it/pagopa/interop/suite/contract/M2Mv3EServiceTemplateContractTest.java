package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.InstanceEServiceSeed;
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

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, M2Mv3ApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class M2Mv3EServiceTemplateContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;

    public M2Mv3EServiceTemplateContractTest(
            ApiClient apiClient,
            @Qualifier("m2mV3InteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            CurrentChannel<Channel> currentChannel
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        currentChannel.setCurrentChannel(Channel.M2M_V3);
    }

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

    private UUID createPublishedEServiceTemplateId() {
        interopJourney.switchChannel(Channel.BFF);
        try {
            return interopJourney
                    .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                    .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                    .get(EServiceTemplate.class)
                    .getId();
        } finally {
            interopJourney.resetChannel(Channel.M2M_V3);
        }
    }
}
