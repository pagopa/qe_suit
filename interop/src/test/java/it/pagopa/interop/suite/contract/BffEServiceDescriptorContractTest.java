package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.application.BffEServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.function.Function.identity;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffEServiceDescriptorContractTest {

    private final ApiClient apiClient;
    private final CurrentUserSession currentUserSession;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> updateDraftDescriptorTemplateInstance() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().updateDraftDescriptorTemplateInstance())
                .pathParams(() -> {
                    EService eServiceInstance = createDraftEServiceInstance();
                    return Map.of(
                            "eServiceId", eServiceInstance.getId(),
                            "descriptorId", eServiceInstance.getLastDraftDescriptor().getId()
                    );
                })
                .payload(() -> {
                    DescriptorAttributesSeed seed = new DescriptorAttributesSeed()
                            .certified(List.of(List.of()))
                            .declared(List.of())
                            .verified(List.of());

                    var command = (BffEServiceTemplateInstanceUpdateDescriptorCommand)
                            requestFactory.defaultUpdateDescriptorInstanceCommand(EServiceDescriptorState.DRAFT);
                    return command.getBffUpdateDraftServiceInstancePayload().attributes(seed);
                })
                /*.targets(
                        FuzzScenario.REMOVED,
                        response -> {
                            response.then().statusCode(400);
                            assertThat(response.jsonPath().getString("title"))
                                    .contains("...");
                        },
                        List.of(payload -> payload.getAttributes().getCertified().get(0).get(0))
                )*/
                .tests();
    }

    @Test
    void updateDraftDescriptorTemplateInstance_rejectsEmptyCertifiedAttributeGroup() {
        EService eServiceInstance = createDraftEServiceInstance();
        DescriptorAttributesSeed attributes = new DescriptorAttributesSeed()
                .certified(List.of(List.of()))
                .declared(List.of())
                .verified(List.of());
        var command = (BffEServiceTemplateInstanceUpdateDescriptorCommand)
                requestFactory.defaultUpdateDescriptorInstanceCommand(EServiceDescriptorState.DRAFT);
        var response = apiClient.eservices()
                .updateDraftDescriptorTemplateInstance()
                .eServiceIdPath(eServiceInstance.getId())
                .descriptorIdPath(eServiceInstance.getLastDraftDescriptor().getId())
                .body(
                        command.getBffUpdateDraftServiceInstancePayload().attributes(attributes)
                )
                .execute(identity());

        response.then().statusCode(400);
        assertThat(response.jsonPath().getString("title"))
                // Template instance attributes cannot be changed
                .contains("TemplateId must be undefined");
    }

    private EService createDraftEServiceInstance() {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .createEServiceInstanceTemplate()
                .waitUntilEService(eService -> eService.getLastDraftDescriptor() != null)
                .get(EService.class);
    }
}
