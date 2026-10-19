package it.pagopa.interop.common.infrastructure;

import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestChainStatusExpectationTest {
    @Test
    void shouldFail_status_assertion_rejects_unexpected_success_after_one_request() {
        Response response = successfulResponse();
        LastApiResponseStore responseStore = mock(LastApiResponseStore.class);
        EntityStore entityStore = mock(EntityStore.class);
        AtomicInteger calls = new AtomicInteger();
        var chain = new TestChainFactory(responseStore, entityStore)
                .build(() -> {
                    calls.incrementAndGet();
                    return new ApiResponse(response);
                }, Void.class);

        assertThatThrownBy(() -> chain.withoutPolling().assertStatusCode(400))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected status code 400 but was 200");
        org.assertj.core.api.Assertions.assertThat(calls).hasValue(1);
        verify(responseStore).setLastResponse(org.mockito.ArgumentMatchers.any(ApiResponse.class));
    }

    @Test
    void positive_until_success_does_not_return_a_definitive_error_as_success() {
        Response errorResponse = mock(Response.class);
        when(errorResponse.getStatusCode()).thenReturn(400);
        when(errorResponse.asString()).thenReturn("{\"title\":\"bad request\"}");
        AtomicInteger calls = new AtomicInteger();
        var chain = new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class))
                .build(() -> {
                    calls.incrementAndGet();
                    return new ApiResponse(errorResponse);
                }, Void.class);

        assertThatThrownBy(() -> chain.withPolling(
                        PollingStrategy.UNTIL_SUCCESS,
                        Duration.ofMillis(25),
                        Duration.ofMillis(2)
                ).get())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Polling in timeout");
        org.assertj.core.api.Assertions.assertThat(calls.get()).isGreaterThan(1);
    }

    private static Response successfulResponse() {
        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(200);
        when(response.asString()).thenReturn("");
        return response;
    }
}
