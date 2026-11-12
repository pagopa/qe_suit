package it.pagopa.interop.suite.contract;

import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BffEServiceConflictRetryTest {
    private HttpServer server;
    private final List<String> requests = new CopyOnWriteArrayList<>();
    private final AtomicInteger attempts = new AtomicInteger();

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void retriesConflictsWithTheSameRequest() throws IOException {
        startServer(2, 200);

        sendRequest().then().statusCode(200);

        assertEquals(3, attempts.get());
        assertEquals(List.of(expectedRequest(), expectedRequest(), expectedRequest()), requests);
    }

    @Test
    void returnsConflictAfterThreeRetries() throws IOException {
        startServer(Integer.MAX_VALUE, 200);

        sendRequest().then().statusCode(409);

        assertEquals(4, attempts.get());
    }

    @Test
    void doesNotRetryOtherErrors() throws IOException {
        startServer(0, 400);

        sendRequest().then().statusCode(400);

        assertEquals(1, attempts.get());
    }

    private void startServer(int conflicts, int finalStatus) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI()
                    + " " + exchange.getRequestHeaders().getFirst("Authorization") + " " + body);
            int status = attempts.incrementAndGet() <= conflicts ? 409 : finalStatus;
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
    }

    private Response sendRequest() {
        return RestAssured.given()
                .baseUri("http://127.0.0.1")
                .port(server.getAddress().getPort())
                .header("Authorization", "Bearer local-test")
                .contentType("application/json")
                .pathParam("eServiceId", "service")
                .pathParam("descriptorId", "draft")
                .queryParam("test", "value")
                .body("{\"attributes\":{\"certified\":[]}}")
                .filter(BffEServiceContractTest.RETRY_ON_CONFLICT)
                .put("/eservices/{eServiceId}/descriptors/{descriptorId}");
    }

    private String expectedRequest() {
        return "PUT /eservices/service/descriptors/draft?test=value"
                + " Bearer local-test {\"attributes\":{\"certified\":[]}}";
    }
}
