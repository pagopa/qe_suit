package it.pagopa.interop.suite.contract;

import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.suite.AbstractBffAttributeTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Supplier;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
/*21/09/2026: al momento il test per URL injection fallisce, coerentemente con il fatto che
 * qui https://pagopa.atlassian.net/browse/PIN-6881 l'api non risulta coperta per
 * questo rilascio. Presumibilmente verrà coperta in futuro. */
public class BffCertifiedDiscreteAttributeContractTest extends AbstractBffAttributeTest {

    public BffCertifiedDiscreteAttributeContractTest(
            ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            BffAttributeRequestFactory requestFactory
    ) {
        super(apiClient, httpContractValidator, interopJourney, requestFactory);
    }

    @Override
    protected Supplier<?> createAttributeImpl() {
        return () -> apiClient.attributes().createCertifiedDiscreteAttribute();
    }
}