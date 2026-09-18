package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.bff.attribute.infrastructure.BffAttributeRequestFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractBffAttributeContractTest {

    protected final ApiClient apiClient;
    protected final HttpContractValidator httpContractValidator;
    protected final InteropJourney interopJourney;
    protected final BffAttributeRequestFactory requestFactory;

    protected AbstractBffAttributeContractTest(
            ApiClient apiClient,
            HttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            BffAttributeRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
    }

    protected abstract Supplier<?> createAttributeImpl();

    @TestFactory
    Stream<DynamicTest> createAttribute() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return createAttributeImpl().get();
                })
                .payload(requestFactory::creationRequest)
                .tests();
    }
}
