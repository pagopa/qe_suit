package it.pagopa.send.suite.contract;

import io.restassured.response.Response;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.generated.openapi.clients.safestorage.ApiClient;
import it.pagopa.send.generated.openapi.clients.safestorage.model.FileCreationRequest;
import it.pagopa.send.safestorage.infrastructure.config.SafeStorageRestApiClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, SafeStorageRestApiClientConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SafeStorageRegexBoundaryContractTest {

    private final ApiClient apiClient;

    @Value("${safestorage.api.client-id}")
    private String configuredClientId;

    SafeStorageRegexBoundaryContractTest(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // ---------------------- PATH clientId: ^[A-Za-z0-9\\-]{1,50}$ ----------------------

    @Test
    void clientIdPath_withValidLength_shouldWork() {
        Response response = apiClient.cfg()
                .getCurrentClientConfig()
                .clientIdPath(configuredClientId) // <= 50
                .execute(Function.identity());

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    void clientIdPath_over50_shouldFail() {
        String over50 = "a".repeat(51);

        Response response = apiClient.cfg()
                .getCurrentClientConfig()
                .clientIdPath(over50)
                .execute(Function.identity());

        assertThat(response.getStatusCode()).isIn(400, 404);
    }

    // ---------------------- HEADER x-pagopa-safestorage-cx-id: ^[a-zA-Z0-9\\-_]{5,50}$ ----------------------

    @Test
    void clientIdHeader_withValidLength_shouldWork() {
        Response response = apiClient.fileUpload()
                .createFile()
                .xPagopaSafestorageCxIdHeader(configuredClientId) // 5..50
                .xChecksumValueHeader("MTIzNDU2Nzg5MDEyMzQ1Ng==")
                .xChecksumHeader("SHA256")
                .body(validCreateFileRequest())
                .execute(Function.identity());

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    void clientIdHeader_over50_shouldFail() {
        String over50 = "pn-".repeat(51);

        Response response = apiClient.fileUpload()
                .createFile()
                .xPagopaSafestorageCxIdHeader(over50)
                .xChecksumValueHeader("MTIzNDU2Nzg5MDEyMzQ1Ng==")
                .xChecksumHeader("SHA256")
                .body(validCreateFileRequest())
                .execute(Function.identity());

        assertThat(response.getStatusCode()).isIn(400, 404);
    }

    private FileCreationRequest validCreateFileRequest() {
        return new FileCreationRequest()
                .contentType("application/pdf")
                .documentType("PN_NOTIFICATION_ATTACHMENTS")
                .status("PRELOADED");
    }
}