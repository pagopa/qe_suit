package it.pagopa.interop.bff.eservice.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.context.InMemoryLastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogRequesterDelegationRole;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;
import static org.assertj.core.api.Assertions.assertThat;

class BffEServiceCatalogTransportTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void serializesEveryFilterOnInitialAndSubsequentRequestsWithoutFilteringResponses(boolean enabled)
            throws IOException {
        ObjectMapper json = new ObjectMapper();
        List<JsonNode> bodies = new CopyOnWriteArrayList<>();
        List<String> routes = new CopyOnWriteArrayList<>();
        List<UUID> resultIds = IntStream.range(0, 201).mapToObj(index -> UUID.randomUUID()).toList();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            JsonNode body = json.readTree(exchange.getRequestBody());
            bodies.add(body);
            routes.add(exchange.getRequestMethod() + " " + exchange.getRequestURI());
            int offset = body.get("offset").asInt();
            int limit = body.get("limit").asInt();
            ObjectNode responseBody = json.createObjectNode();
            var results = responseBody.putArray("results");
            resultIds.subList(offset, Math.min(offset + limit, resultIds.size())).forEach(id ->
                    results.addObject().put("id", id.toString()).put("name", "Unrelated server result"));
            responseBody.putObject("pagination").put("offset", offset).put("limit", limit)
                    .put("totalCount", resultIds.size());
            byte[] response = responseBody.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            ApiClient apiClient = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(() ->
                    new RequestSpecBuilder()
                            .setBaseUri("http://127.0.0.1:" + server.getAddress().getPort())
                            .setConfig(config().objectMapperConfig(
                                    objectMapperConfig().defaultObjectMapper(jackson())))));
            var factory = new BffEServiceRequestFactory();
            var gateway = new BffEServiceCatalogGateway(new BffEServiceCatalogRestClient(
                    new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore()), apiClient),
                    Mappers.getMapper(BffEServiceCatalogMapper.class), factory);
            var catalog = new EServiceCatalogUseCase(gateway);
            var command = (BffSearchEServiceCatalogCommand) factory.defaultSearchEServiceCatalogCommand();
            UUID producerId = UUID.randomUUID();
            UUID templateId = UUID.randomUUID();
            command.keyword("E-service di Gamma")
                    .sortBy(EServiceCatalogSortBy.NAME_ASC)
                    .onlyActiveEservices(enabled)
                    .availableForRequester(enabled)
                    .subscribedByRequester(enabled)
                    .onlyTemplateInstances(enabled)
                    .hasLinkedPurposeTemplates(enabled)
                    .producerCategories(List.of(TenantCategory.COMUNI))
                    .asyncExchange(enabled)
                    .mode(EServiceMode.DELIVER)
                    .onlySignalHubEnabled(enabled)
                    .requesterDelegationRoles(List.of(EServiceCatalogRequesterDelegationRole.DELEGATE))
                    .templateId(templateId)
                    .producersIds(List.of(producerId));

            var first = catalog.search(command);
            var next = catalog.search(factory.searchEServiceCatalogCommandForOffset(command, 200));

            assertThat(first.getResults()).extracting(entry -> entry.getId())
                    .containsExactlyElementsOf(resultIds.subList(0, 200));
            assertThat(next.getResults()).extracting(entry -> entry.getId())
                    .containsExactlyElementsOf(resultIds.subList(200, 201));
            assertThat(routes).containsExactly("POST /catalog", "POST /catalog");
            ObjectNode expected = json.readValue("""
                    {
                      "offset": 0, "limit": 200, "keyword": "E-service di Gamma",
                      "sortBy": "NAME_ASC", "onlyActiveEservices": %s,
                      "availableForRequester": %s, "subscribedByRequester": %s,
                      "onlyTemplateInstances": %s, "hasLinkedPurposeTemplates": %s,
                      "producerCategories": ["COMUNI"], "asyncExchange": %s,
                      "mode": "DELIVER", "onlySignalHubEnabled": %s,
                      "requesterDelegationRoles": ["DELEGATE"],
                      "templateId": "%s", "producersIds": ["%s"]
                    }
                    """.formatted(enabled, enabled, enabled, enabled, enabled, enabled, enabled,
                    templateId, producerId), ObjectNode.class);
            assertThat(bodies).hasSize(2);
            assertThat(bodies.get(0)).isEqualTo(expected);
            assertThat(bodies.get(1)).isEqualTo(expected.deepCopy().put("offset", 200));
            assertThat(command.getOffset()).isZero();
        } finally {
            server.stop(0);
        }
    }
}
