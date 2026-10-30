package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.purpose_template.infrastructure.BffPurposeTemplateRequestFactory;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffPurposeTemplateContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffPurposeTemplateRequestFactory requestFactory;

    public BffPurposeTemplateContractTest(
            ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney,
            BffPurposeTemplateRequestFactory requestFactory) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> createPurposeTemplate() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().createPurposeTemplate())
                .payload(asProducer(() -> requestFactory.creationRequest(PRODUCER)))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updatePurposeTemplate() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().updatePurposeTemplate())
                .pathParams(() -> Map.of("purposeTemplateId", createPurposeTemplate(PurposeTemplateState.DRAFT).getId()))
                .payload(asProducer(() -> requestFactory.updateRequest(PRODUCER)))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> addPurposeTemplateRiskAnalysisAnswer() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().addPurposeTemplateRiskAnalysisAnswer())
                .pathParams(() -> Map.of("purposeTemplateId", createPurposeTemplate(PurposeTemplateState.DRAFT).getId()))
                .payload(requestFactory::addRiskAnalysisAnswerRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> addPurposeTemplateRiskAnalysisAnswerAnnotation() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().addPurposeTemplateRiskAnalysisAnswerAnnotation())
                .pathParams(() -> {
                    PurposeTemplate pt = createPurposeTemplate(PurposeTemplateState.DRAFT);
                    UUID answerId = pt.getFirstRiskAnalysisAnswerId();
                    return Map.of("purposeTemplateId", pt.getId(), "answerId", answerId);
                })
                .payload(requestFactory::addRiskAnalysisAnswerAnnotationRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> publishPurposeTemplate() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().publishPurposeTemplate())
                .pathParams(() -> Map.of("purposeTemplateId", createPurposeTemplate(PurposeTemplateState.DRAFT).getId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> unsuspendPurposeTemplate() {
        return httpContractValidator
                .as(PRODUCER, UserRole.ADMIN)
                .apiCall(() -> apiClient.purposeTemplates().unsuspendPurposeTemplate())
                .pathParams(() -> Map.of("purposeTemplateId", createPurposeTemplate(PurposeTemplateState.SUSPENDED).getId()))
                .tests();
    }

    private PurposeTemplate createPurposeTemplate(PurposeTemplateState state) {
        return interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createPurposeTemplate(PRODUCER, state)
                .get(PurposeTemplate.class);
    }

    private <T> Supplier<T> asProducer(Supplier<T> supplier) {
        return () -> {
            interopJourney.withProducer(PRODUCER, UserRole.ADMIN);
            return supplier.get();
        };
    }
}

