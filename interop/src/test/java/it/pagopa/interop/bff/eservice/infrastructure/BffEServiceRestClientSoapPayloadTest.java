package it.pagopa.interop.bff.eservice.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.context.InMemoryLastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

class BffEServiceRestClientSoapPayloadTest {
    @Test
    void raw_soap_payload_reaches_http_as_json_without_local_uri_conversion() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<byte[]> requestBody = new AtomicReference<>();
        server.createContext("/", exchange -> {
            requestBody.set(exchange.getRequestBody().readAllBytes());
            byte[] response = "{\"id\":\"00000000-0000-0000-0000-000000000001\"}"
                    .getBytes(StandardCharsets.UTF_8);
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
                                    objectMapperConfig().defaultObjectMapper(jackson())
                            ))
            ));
            BffEServiceRestClient restClient = new BffEServiceRestClient(
                    new TestChainFactory(new InMemoryLastApiResponseStore(), new InMemoryEntityStore()),
                    apiClient
            );
            Map<String, Object> payload = Map.of("serverUrls", List.of(
                    Map.of("url", "https://host.example/with<markup/>"),
                    Map.of("url", "https://host.example/with\"quote")
            ));

            assertThatNoException().isThrownBy(() -> restClient.addEServiceTemplateInstanceInterfaceSoap(
                            UUID.randomUUID(), UUID.randomUUID(), payload
                    )
                    .withoutPolling()
                    .getRaw());

            Map<?, ?> sent = new ObjectMapper().readValue(requestBody.get(), Map.class);
            List<Map<String, String>> sentUrls = (List<Map<String, String>>) sent.get("serverUrls");
            assertThat(sentUrls).extracting(url -> url.get("url")).containsExactly(
                    "https://host.example/with<markup/>",
                    "https://host.example/with\"quote"
            );
        } finally {
            server.stop(0);
        }
    }
}
