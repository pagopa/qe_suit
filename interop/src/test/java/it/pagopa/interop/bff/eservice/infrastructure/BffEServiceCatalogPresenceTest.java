package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPresenceCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog.Entry;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffEServiceCatalogPresenceTest {
    @Test
    void retriesOnlyMissingPresenceWithTheSameQueryAndOneRequestPerAttempt() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UUID unrelatedId = UUID.randomUUID();
        var command = command(5, 2);
        String firstPage = response(5, 2, 7, unrelatedId, firstId);
        String visiblePage = response(5, 2, 7, firstId, secondId);
        Fixture fixture = fixture(new int[]{200, 200}, firstPage, visiblePage);

        EServiceCatalog page = fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(firstId, secondId))
        );

        assertThat(page.getOffset()).isEqualTo(5);
        assertThat(page.getLimit()).isEqualTo(2);
        assertThat(page.getResults()).extracting(Entry::getId).containsExactly(firstId, secondId);
        assertThat(command.getBffPayload().getKeyword()).isEqualTo("catalog-presence");
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isTrue();
        assertThat(command.getBffPayload().getOffset()).isEqualTo(5);
        assertThat(command.getBffPayload().getLimit()).isEqualTo(2);
        assertThat(fixture.attempts.get()).isEqualTo(2);
        verify(fixture.operation, times(2)).body(same(command.getBffPayload()));
        verify(fixture.operation, times(2)).execute(any());
        assertThat(fixture.gateway.supports(Channel.BFF)).isTrue();
    }

    @Test
    void neverAccumulatesExpectedIdsAcrossAttemptsAndTimesOutWithPageDiagnostics() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UUID unrelatedId = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture(new int[]{200, 200},
                response(0, 2, 2, firstId, unrelatedId),
                response(0, 2, 2, secondId, unrelatedId));
        var gateway = new BffEServiceCatalogGateway(
                fixture.client, Mappers.getMapper(BffEServiceCatalogMapper.class),
                Duration.ofSeconds(1), Duration.ofMillis(2));

        assertThatThrownBy(() -> gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(firstId, secondId))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(firstId.toString())
                .hasMessageContaining(secondId.toString())
                .hasMessageContaining("missing IDs")
                .hasMessageContaining(unrelatedId.toString())
                .hasMessageContaining("offset 0")
                .hasMessageContaining("limit 2")
                .hasMessageContaining("duration");
        assertThat(fixture.attempts.get()).isGreaterThan(1);
        verify(fixture.operation, atLeast(2)).body(same(command.getBffPayload()));
    }

    @Test
    void malformedPageCountsFailImmediatelyWithoutRetry() {
        UUID expectedId = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture(new int[]{200},
                response(0, 2, 2, expectedId));

        assertThatThrownBy(() -> fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(expectedId))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expected 2");
        assertThat(fixture.attempts.get()).isEqualTo(1);
    }

    @Test
    void definitiveHttpErrorsFailImmediatelyWithoutRetry() {
        UUID expectedId = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture(new int[]{403},
                response(0, 2, 2, expectedId, UUID.randomUUID()));

        assertThatThrownBy(() -> fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(expectedId))))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected status code 200 but was 403");
        assertThat(fixture.attempts.get()).isEqualTo(1);
    }

    private Fixture fixture(int[] statuses, String... bodies) {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.QueryEServicesCatalogOper operation =
                mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        AtomicInteger attempts = new AtomicInteger();
        when(operation.execute(any())).thenAnswer(invocation -> {
            int attempt = attempts.getAndIncrement();
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder()
                    .setStatusCode(statuses[Math.min(attempt, statuses.length - 1)])
                    .setContentType(ContentType.JSON)
                    .setBody(bodies[Math.min(attempt, bodies.length - 1)])
                    .build());
        });
        var client = new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)),
                apiClient
        );
        return new Fixture(
                client,
                new BffEServiceCatalogGateway(
                        client, Mappers.getMapper(BffEServiceCatalogMapper.class),
                        Duration.ofSeconds(3), Duration.ofMillis(2)),
                operation,
                attempts
        );
    }

    private BffSearchEServiceCatalogCommand command(int offset, int limit) {
        var command = new BffSearchEServiceCatalogCommand();
        command.offset(offset).limit(limit).keyword("catalog-presence")
                .onlyActiveEservices(true);
        return command;
    }

    private String response(int offset, int limit, int total, UUID... ids) {
        String results = java.util.Arrays.stream(ids)
                .map(id -> "{\"id\":\"" + id + "\",\"name\":\"e-service\"}")
                .collect(java.util.stream.Collectors.joining(",", "[", "]"));
        return "{\"results\":" + results + ",\"pagination\":{\"offset\":" + offset
                + ",\"limit\":" + limit + ",\"totalCount\":" + total + "}}";
    }

    @RequiredArgsConstructor
    private static final class Fixture {
        private final BffEServiceCatalogRestClient client;
        private final BffEServiceCatalogGateway gateway;
        private final EservicesApi.QueryEServicesCatalogOper operation;
        private final AtomicInteger attempts;
    }
}
