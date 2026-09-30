package it.pagopa.infrastructure.template.action;

import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.domain.Identifiable;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.context.InMemoryLastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateActionTest {

    @Test
    void independentTestChainsReceiveDistinctPollingActions() {
        TestChainFactory factory = new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore());

        TestChain<String> first = factory.build(() -> apiResponse(200), String.class);
        TestChain<String> second = factory.build(() -> apiResponse(200), String.class);

        PollingAction<String> firstPolling = first.withPolling(PollingStrategy.UNTIL_SUCCESS);
        PollingAction<String> secondPolling = second.withPolling(PollingStrategy.UNTIL_SUCCESS);

        assertNotSame(firstPolling, secondPolling);
        assertNotSame(firstPolling.getContext(), secondPolling.getContext());
    }

    @Test
    void withPollingAndWithoutPollingProduceFreshInstances() {
        TestChainFactory factory = new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore());
        TestChain<String> chain = factory.build(() -> apiResponse(200), String.class);

        PollingAction<String> polling = chain.withPolling(PollingStrategy.UNTIL_SUCCESS);
        PollingAction<String> direct = chain.withoutPolling();

        assertNotSame(polling, direct);
        assertEquals("ok", polling.get());
        assertEquals("ok", direct.get());
    }

    @Test
    void responseFinalizerMapsAndUpdatesEntityStore() {
        EntityStore entityStore = new InMemoryEntityStore();
        RawResponse raw = apiResponse(200, "ok");
        TestData expected = new TestData(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        ResponseFinalizer<TestData> finalizer = new MappedResponseFinalizer<>(
                new ResolvedResponseFinalizer<>(expected, raw, entityStore),
                value -> new TestData(value.id())
        );

        assertEquals(expected.id(), finalizer.get().id());
        assertSame(raw, finalizer.getRaw());

        finalizer.updateContext();
        assertTrue(entityStore.getById(expected.id(), TestData.class).isPresent());
    }

    @Test
    void assertStatusCodeFailsWhenRawResponseDoesNotMatch() {
        TestChainFactory factory = new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore());
        TestChain<String> chain = factory.build(() -> apiResponse(201), String.class);

        PollingAction<String> action = chain.withoutPolling();

        AssertionError error = assertThrows(AssertionError.class, () -> action.assertStatusCode(200));
        assertTrue(error.getMessage().contains("Expected status code 200") || error.getMessage().contains("201"));
    }

    @Test
    void noSharedMutableStateAcrossParallelChains() {
        TestChainFactory factory = new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore());
        AtomicInteger seen = new AtomicInteger();

        TestChain<String> first = factory.build(() -> {
            seen.incrementAndGet();
            return apiResponse(200);
        }, String.class);
        TestChain<String> second = factory.build(() -> {
            seen.incrementAndGet();
            return apiResponse(200);
        }, String.class);

        PollingAction<String> firstAction = first.withoutPolling();
        PollingAction<String> secondAction = second.withoutPolling();

        assertNotSame(firstAction, secondAction);
        assertEquals(2, seen.get());
        assertEquals("ok", firstAction.get());
        assertEquals("ok", secondAction.get());
    }

    private static RawResponse apiResponse(int statusCode) {
        return apiResponse(statusCode, "ok");
    }

    private static RawResponse apiResponse(int statusCode, String raw) {
        Response restAssuredResponse = mock(Response.class);
        when(restAssuredResponse.getStatusCode()).thenReturn(statusCode);
        when(restAssuredResponse.asString()).thenReturn(raw);
        when(restAssuredResponse.as(String.class)).thenReturn(raw);
        return new ApiResponse(restAssuredResponse);
    }

    private record TestData(UUID id) implements Identifiable {
        @Override
        public UUID getId() {
            return id;
        }
    }
}
