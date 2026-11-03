package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.channel.CurrentChannel;
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
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.interop.m2m.v3.infrastructure.config.M2Mv3ApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, M2Mv3ApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class M2Mv3EServiceTemplateInterfaceContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;

    public M2Mv3EServiceTemplateInterfaceContractTest(
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
    Stream<DynamicTest> downloadEServiceTemplateVersionInterface() {

        EServiceTemplate createdEServiceTemplate = interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate()
                .publishVersion(EServiceTemplateVersionState.PUBLISHED)
                .waitUntilEServiceTemplate(template -> template.getLastPublishedVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .resetChannel(Channel.M2M_V3)
                .get(EServiceTemplate.class);

        return httpContractValidator
            .as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)
            .apiCall(() -> apiClient.eserviceTemplates().downloadEServiceTemplateVersionInterface())
            .pathParams(() -> {
                return Map.of(
                    "templateId", createdEServiceTemplate.getRef().id(),
                    "versionId", createdEServiceTemplate.getLatestVersion().getRef().versionId()
                );
            })
            .tests();
    }
}
