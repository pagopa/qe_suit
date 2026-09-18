package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementSeed;
import it.pagopa.interop.m2m.v3.infrastructure.config.M2Mv3ApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, M2Mv3ApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class M2Mv3CatalogContractTest {

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;

    public M2Mv3CatalogContractTest(
            ApiClient apiClient,
            @Qualifier("m2Mv3ApiContractValidator") HttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            CurrentChannel<Channel> currentChannel
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        currentChannel.setCurrentChannel(Channel.M2M_V3);
    }

    @TestFactory
    Stream<DynamicTest> createAgreement() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.agreements().createAgreement();
                })
                .payload(() -> {
                    EService createdEservice = interopJourney
                            .switchChannel(Channel.BFF)
                            .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                            .createEService(EServiceDescriptorState.PUBLISHED)
                            .resetChannel(Channel.M2M_V3)
                            .get(EService.class);

                    return new AgreementSeed()
                                    .descriptorId(createdEservice.getActiveDescriptor().getId())
                                    .eserviceId(createdEservice.getId());
                })
                .tests();
    }
}
