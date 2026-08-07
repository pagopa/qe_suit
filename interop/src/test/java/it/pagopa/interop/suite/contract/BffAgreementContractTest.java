package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.utils.async.DelayUtils;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.bff.agreement.infrastructure.BffAgreementRequestFactory;
import it.pagopa.interop.common.infrastructure.config.JunitSupportConfig;
import it.pagopa.interop.common.infrastructure.http.contract.engine.ContractTestEngine;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitSupportConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffAgreementContractTest extends ContractTestEngine {

    private final ApiClient apiClient;
    private final InteropJourney interopJourney;
    private final BffAgreementRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createAgreement() {
        EService createdEservice = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        // Attesa per permettere al sistema di gestire un eventual consistency
        DelayUtils.waitForSeconds(2);

        AgreementPayload validPayload = requestFactory.creationRequest(createdEservice, createdEservice.getLastDraftDescriptor(), null);

        return fuzz(validPayload)
                .expectValid(200)
                .execute((request, expectedStatus) -> {
                    var operation = apiClient.agreements().createAgreement();
                    injectRawBody(operation, request.body());

                    operation.execute(response -> {
                        response.then().statusCode(expectedStatus);
                        return response;
                    });
                });
    }
}