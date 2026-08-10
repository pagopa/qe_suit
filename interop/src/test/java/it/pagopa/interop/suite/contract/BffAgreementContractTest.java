package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.agreement.infrastructure.BffAgreementRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.config.JunitSupportConfig;
import it.pagopa.interop.common.infrastructure.contract.http.HttpContract;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitSupportConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffAgreementContractTest {

    private final ApiClient apiClient;
    private final HttpContract httpContract;
    private final InteropJourney interopJourney;
    private final BffAgreementRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createAgreement() {

        return httpContract
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.agreements().createAgreement();
                })
                .payload(() -> {
                    EService createdEservice = interopJourney
                            .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                            .createEService(EServiceDescriptorState.PUBLISHED)
                            .get(EService.class);

                    return requestFactory.creationRequest(createdEservice, createdEservice.getActiveDescriptor(), null);
                })
                .tests();
    }
}