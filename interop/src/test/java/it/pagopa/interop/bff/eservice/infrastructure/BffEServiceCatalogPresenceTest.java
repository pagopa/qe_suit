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
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffEServiceCatalogPresenceTest {
    private static final UUID OTHER = UUID.randomUUID();

    @Test
    void retriesOnlyMissingPresenceWithTheSameQueryAndOneRequestPerAttempt() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UUID unrelatedId = UUID.randomUUID();
        var command = command(5, 2);
        String firstPage = response(5, 2, 7, unrelatedId, firstId);
        String visiblePage = response(5, 2, 7, firstId, secondId);
        Fixture fixture = fixture(new int[]{200, 200}, firstPage, visiblePage);

        List<EServiceCatalog> pages = fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(firstId, secondId))
        );

        assertThat(pages).hasSize(1);
        EServiceCatalog page = pages.get(0);
        assertThat(page.getOffset()).isEqualTo(5);
        assertThat(page.getLimit()).isEqualTo(2);
        assertThat(page.getResults()).extracting(Entry::getId).containsExactly(firstId, secondId);
        assertThat(command.getBffPayload().getKeyword()).isEqualTo("catalog-presence");
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isTrue();
        assertThat(command.getBffPayload().getOffset()).isEqualTo(5);
        assertThat(command.getBffPayload().getLimit()).isEqualTo(2);
        verify(fixture.operation, times(2)).body(same(command.getBffPayload()));
        verify(fixture.operation, times(2)).execute(any());
        assertThat(fixture.gateway.supports(Channel.BFF)).isTrue();
    }

    @Test
    void findsExpectedIdsSplitAcrossThreePagesAndStopsAsSoonAsAllAreFound() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture((call, payload) -> switch (payload.getOffset()) {
            case 0 -> response(0, 2, 10, first, UUID.randomUUID());
            case 2 -> response(2, 2, 10, UUID.randomUUID(), second);
            case 4 -> response(4, 2, 10, third, UUID.randomUUID());
            default -> throw new AssertionError("Page after early success requested: " + payload.getOffset());
        }, Duration.ofSeconds(3));

        List<EServiceCatalog> pages = fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(third, first, second)));

        assertThat(pages).extracting(EServiceCatalog::getOffset).containsExactly(0, 2, 4);
        assertThat(fixture.offsets).containsExactly(0, 2, 4);
        assertOriginalFiltersOnEveryRequest(fixture, command);
        assertThat(fixture.payloads.get(0)).isSameAs(command.getBffPayload());
        assertThat(fixture.payloads.subList(1, 3)).noneMatch(payload -> payload == command.getBffPayload());
        assertThat(command.getBffPayload().getOffset()).isZero();
        assertThat(command.getBffPayload().getLimit()).isEqualTo(2);
    }

    @Test
    void aMissingIdFoundOnlyByALaterFreshScanSucceedsWithThatScanOnly() {
        UUID first = UUID.randomUUID();
        UUID late = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture((call, payload) -> {
            boolean fresh = call >= 3;
            return switch (payload.getOffset()) {
                case 0 -> response(0, 2, 6, first, UUID.randomUUID());
                case 2 -> response(2, 2, 6, UUID.randomUUID(), UUID.randomUUID());
                default -> response(4, 2, 6, fresh ? late : UUID.randomUUID(), UUID.randomUUID());
            };
        }, Duration.ofSeconds(3));

        List<EServiceCatalog> pages = fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(first, late)));

        assertThat(fixture.offsets).containsExactly(0, 2, 4, 0, 2, 4);
        assertThat(pages).extracting(EServiceCatalog::getOffset).containsExactly(0, 2, 4);
        assertThat(pages.get(2).getResults()).extracting(Entry::getId).contains(late);
        assertOriginalFiltersOnEveryRequest(fixture, command);
    }

    @Test
    void neverAccumulatesExpectedIdsAcrossAttemptsAndTimesOutWithPageDiagnostics() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UUID unrelatedId = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture((call, payload) -> payload.getOffset() == 0
                ? response(0, 2, 4, call % 4 == 0 ? firstId : OTHER, unrelatedId)
                : response(2, 2, 4, call % 4 == 1 ? UUID.randomUUID() : secondId, UUID.randomUUID()),
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(firstId, secondId))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(firstId.toString())
                .hasMessageContaining(secondId.toString())
                .hasMessageContaining("missing IDs")
                .hasMessageContaining(unrelatedId.toString())
                .hasMessageContaining("last scanned offsets [0, 2]")
                .hasMessageContaining("offset 0")
                .hasMessageContaining("limit 2")
                .hasMessageContaining("duration");
        assertThat(fixture.offsets.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    void growingTotalsAreNotChasedAndAnomaliesAreReportedAsInconclusive() {
        UUID expected = UUID.randomUUID();
        var command = command(0, 2);
        Fixture fixture = fixture((call, payload) -> response(payload.getOffset(), 2,
                payload.getOffset() == 0 ? 4 : 6, UUID.randomUUID(), UUID.randomUUID()), Duration.ofMillis(200));

        assertThatThrownBy(() -> fixture.gateway.awaitPresence(
                command, new EServiceCatalogPresenceCommand(List.of(expected))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("last scanned offsets [0, 2]")
                .hasMessageContaining("inconclusive: offset pagination anomalies")
                .hasMessageContaining("total changed");
        assertThat(fixture.offsets).allMatch(offset -> offset == 0 || offset == 2);
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
        assertThat(fixture.offsets).hasSize(1);
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
        assertThat(fixture.offsets).hasSize(1);
    }

    private void assertOriginalFiltersOnEveryRequest(Fixture fixture, BffSearchEServiceCatalogCommand command) {
        assertThat(fixture.payloads).allSatisfy(payload -> {
            assertThat(payload.getKeyword()).isEqualTo(command.getBffPayload().getKeyword());
            assertThat(payload.getOnlyActiveEservices()).isTrue();
            assertThat(payload.getProducersIds()).isEqualTo(command.getBffPayload().getProducersIds());
            assertThat(payload.getLimit()).isEqualTo(command.getBffPayload().getLimit());
        });
    }

    private Fixture fixture(int[] statuses, String... bodies) {
        return fixture((call, payload) -> null, Duration.ofSeconds(3), call -> statuses[Math.min(call, statuses.length - 1)],
                call -> bodies[Math.min(call, bodies.length - 1)]);
    }

    private Fixture fixture(BiFunction<Integer, CatalogFilterPayload, String> responder, Duration timeout) {
        return fixture(responder, timeout, call -> 200, null);
    }

    private Fixture fixture(
            BiFunction<Integer, CatalogFilterPayload, String> responder, Duration timeout,
            Function<Integer, Integer> status, Function<Integer, String> body
    ) {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.QueryEServicesCatalogOper operation =
                mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        AtomicReference<CatalogFilterPayload> lastPayload = new AtomicReference<>();
        List<CatalogFilterPayload> payloads = new ArrayList<>();
        List<Integer> offsets = new ArrayList<>();
        when(operation.body(any())).thenAnswer(invocation -> {
            lastPayload.set(invocation.getArgument(0));
            return operation;
        });
        when(operation.execute(any())).thenAnswer(invocation -> {
            int call = offsets.size();
            CatalogFilterPayload payload = lastPayload.get();
            payloads.add(payload);
            offsets.add(payload.getOffset());
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder()
                    .setStatusCode(status.apply(call))
                    .setContentType(ContentType.JSON)
                    .setBody(body == null ? responder.apply(call, payload) : body.apply(call))
                    .build());
        });
        var client = new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)),
                apiClient
        );
        return new Fixture(
                new BffEServiceCatalogGateway(
                        client, Mappers.getMapper(BffEServiceCatalogMapper.class), new BffEServiceRequestFactory(),
                        timeout, Duration.ofMillis(2)),
                operation,
                payloads,
                offsets
        );
    }

    private BffSearchEServiceCatalogCommand command(int offset, int limit) {
        var command = new BffSearchEServiceCatalogCommand();
        command.offset(offset).limit(limit).keyword("catalog-presence")
                .onlyActiveEservices(true)
                .producersIds(List.of(UUID.randomUUID()));
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
        private final BffEServiceCatalogGateway gateway;
        private final EservicesApi.QueryEServicesCatalogOper operation;
        private final List<CatalogFilterPayload> payloads;
        private final List<Integer> offsets;
    }
}
