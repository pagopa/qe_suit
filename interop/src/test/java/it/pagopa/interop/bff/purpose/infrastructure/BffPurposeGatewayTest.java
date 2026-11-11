package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BffPurposeGatewayTest {
    private final BffPurposeRestClient restClient = mock(BffPurposeRestClient.class);
    private final LastApiResponseStore lastApiResponseStore = mock(LastApiResponseStore.class);
    private final BffPurposeGateway gateway = new BffPurposeGateway(
            restClient, mock(BffPurposeMapper.class), mock(BffPurposeCommandFactory.class),
            lastApiResponseStore);

    @Test
    void acceptsReceiveEServiceLinkRejectionWithConflictStatus() {
        ApiResponse response = mock(ApiResponse.class);
        when(response.getStatusCode()).thenReturn(409);
        when(lastApiResponseStore.getLastResponse()).thenReturn(response);

        assertThatCode(gateway::shouldFailLinkToReceiveEService).doesNotThrowAnyException();

        verifyNoInteractions(restClient);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204, 302, 400, 401, 403, 404, 422, 500, 503})
    void rejectsUnexpectedReceiveEServiceLinkStatus(int status) {
        ApiResponse response = mock(ApiResponse.class);
        when(response.getStatusCode()).thenReturn(status);
        when(lastApiResponseStore.getLastResponse()).thenReturn(response);

        assertThatThrownBy(gateway::shouldFailLinkToReceiveEService)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("409");
    }

    @Test
    void rejectsMissingReceiveEServiceLinkResponse() {
        assertThatThrownBy(gateway::shouldFailLinkToReceiveEService)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Risposta");
    }
}
