package it.pagopa.interop.suite.contract;

import io.restassured.response.Response;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDocumentRequestFactory;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDocumentRequestFactory.DocumentRequest;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Contract tests for {@code POST /eservices/{eServiceId}/descriptors/{descriptorId}/documents}
 * with WSDL interfaces.
 * <p>
 * The validator DSL serializes payloads as JSON and cannot send a multipart {@code File}, nor assert
 * a status on a known-invalid payload. These tests therefore call the generated {@code apiClient}
 * directly inside {@link DynamicTest}s. The preconditions run through the
 * Journey as COMUNE_DI_MILANO / ADMIN, which is also the identity of the final call.
 * <p>
 */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffEServiceDescriptorDocumentContractTest {

    private static final String VALID_WSDL = "valid.wsdl";
    private static final String MISSING_SOAP_ADDRESS_WSDL = "missing-soap-address.wsdl";
    private static final String NOT_PARSABLE_WSDL = "not-parsable.wsdl";
    private static final String MISSING_SOAP_OPERATION_WSDL = "missing-soap-operation.wsdl";

    private static final String UNEXPECTED_ERROR = "Unexpected error";

    private final ApiClient apiClient;
    private final InteropJourney interopJourney;
    private final BffEServiceDocumentRequestFactory requestFactory;

    public BffEServiceDescriptorDocumentContractTest(
            ApiClient apiClient,
            InteropJourney interopJourney,
            BffEServiceDocumentRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> createEServiceDocument() {
        Stream<DynamicTest> scenarios = Stream.of(
                // T10 - baseline
                dynamicTest("valid.wsdl -> 200 CreatedResource", () -> {
                    Draft draft = newDraftDescriptor();
                    Response response = postDocument(draft, requestFactory.interfaceDocumentRequest(VALID_WSDL));
                    assertEquals(200, response.statusCode(), response.asPrettyString());
                    assertNotNull(response.jsonPath().getString("id"), "CreatedResource.id must be present");
                }),

                // T11 - C1: soap:address missing
                dynamicTest("missing soap:address -> 400 / 10017", () -> {
                    Draft draft = newDraftDescriptor();
                    Response response = postDocument(draft, requestFactory.interfaceDocumentRequest(MISSING_SOAP_ADDRESS_WSDL));
                    assertProblem(response, "10017");
                    assertEquals("Error extracting field from SOAP file", response.jsonPath().getString("title"));
                    assertEquals("Error extracting field soap:address from SOAP file", response.jsonPath().getString("detail"));
                }),

                // T12 - C2: WSDL not parsable
                dynamicTest("not parsable WSDL -> 400 / 10016", () -> {
                    Draft draft = newDraftDescriptor();
                    Response response = postDocument(draft, requestFactory.interfaceDocumentRequest(NOT_PARSABLE_WSDL));
                    assertProblem(response, "10016");
                }),

                // T13 - C3: soap:operation missing
                dynamicTest("missing soap:operation -> 400 / 10017", () -> {
                    Draft draft = newDraftDescriptor();
                    Response response = postDocument(draft, requestFactory.interfaceDocumentRequest(MISSING_SOAP_OPERATION_WSDL));
                    assertProblem(response, "10017");
                    assertTrue(
                            response.jsonPath().getString("detail").contains("soap:operation"),
                            "detail must mention soap:operation: " + response.asPrettyString()
                    );
                })
        );

        // T14 - C5: no document is created by a rejected upload, and the descriptor stays usable
        Stream<DynamicTest> invalidUploadScenarios = Stream.of(MISSING_SOAP_ADDRESS_WSDL, NOT_PARSABLE_WSDL, MISSING_SOAP_OPERATION_WSDL)
                .map(fixture -> dynamicTest("no document created for " + fixture, () -> {
                    Draft draft = newDraftDescriptor();
                    int docsBefore = readDescriptor(draft).getDocs().size();

                    Response rejected = postDocument(draft, requestFactory.interfaceDocumentRequest(fixture));
                    assertEquals(400, rejected.statusCode(), rejected.asPrettyString());

                    ProducerEServiceDescriptor after = readDescriptor(draft);
                    assertNull(after.getInterface(), "interface must not be set after a rejected upload");
                    assertEquals(docsBefore, after.getDocs().size(), "docs must be unchanged");

                    Response retry = postDocument(draft, requestFactory.interfaceDocumentRequest(VALID_WSDL));
                    assertEquals(200, retry.statusCode(), retry.asPrettyString());
                }));

        return Stream.concat(scenarios, invalidUploadScenarios);
    }

    private void assertProblem(Response response, String expectedCode) {
        String body = response.asPrettyString();

        assertEquals(400, response.statusCode(), body);
        assertNotEquals(500, response.statusCode(), body);
        assertEquals(expectedCode, response.jsonPath().getString("errors[0].code"), body);

        String title = response.jsonPath().getString("title");
        String detail = response.jsonPath().getString("detail");
        assertNotEquals(UNEXPECTED_ERROR, title, body);
        assertNotEquals(UNEXPECTED_ERROR, detail, body);
        assertNotNull(detail, body);
        assertFalse(detail.isBlank(), body);
        assertFalse(
                detail.contains("at ") || detail.contains(".java") || detail.contains("Exception"),
                "detail must not expose a stack trace: " + detail
        );
    }

    private Draft newDraftDescriptor() {
        EService eService = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.DRAFT, EServiceTechnology.SOAP)
                .get(EService.class);

        return new Draft(eService.getId(), eService.getLastDraftDescriptor().getId());
    }

    private Response postDocument(Draft draft, DocumentRequest request) {
        return apiClient.eservices()
                .createEServiceDocument()
                .eServiceIdPath(draft.eServiceId())
                .descriptorIdPath(draft.descriptorId())
                .kindForm(request.kind())
                .prettyNameForm(request.prettyName())
                .reqSpec(reqSpec -> reqSpec.addMultiPart("doc", request.doc(), "application/octet-stream"))
                .execute(Function.identity());
    }

    private ProducerEServiceDescriptor readDescriptor(Draft draft) {
        Response response = apiClient.eservices()
                .getProducerEServiceDescriptor()
                .eserviceIdPath(draft.eServiceId())
                .descriptorIdPath(draft.descriptorId())
                .execute(Function.identity());

        assertEquals(200, response.statusCode(), response.asPrettyString());
        return response.as(ProducerEServiceDescriptor.class);
    }

    private record Draft(UUID eServiceId, UUID descriptorId) {
    }
}

