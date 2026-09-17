package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Supplier;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffCertifiedAttributeContractTest extends AbstractBffAttributeContractTest {

    public BffCertifiedAttributeContractTest(
            ApiClient apiClient,
            HttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            BffAttributeRequestFactory requestFactory
    ) {
        super(apiClient, httpContractValidator, interopJourney, requestFactory);
    }

    @Override
    protected Supplier<?> createAttributeImpl() {
        return () -> apiClient.attributes().createCertifiedAttribute();
    }
}

