package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
/*21/09/2026: al momento il test per URL injection fallisce, coerentemente con il fatto che
 * qui https://pagopa.atlassian.net/browse/PIN-6881 l'api non risulta coperta per
 * questo rilascio. Presumibilmente verrà coperta in futuro. */
public class BffCertifiedDiscreteAttributeContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final BffAttributeRequestFactory requestFactory;

    public BffCertifiedDiscreteAttributeContractTest(
            ApiClient apiClient,
            InteropHttpContractValidator httpContractValidator,
            BffAttributeRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> createCertifiedDiscreteAttribute() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.attributes().createCertifiedDiscreteAttribute())
                .payload(requestFactory::creationRequest)
                .tests();
    }
}

