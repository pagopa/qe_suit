package it.pagopa.interop.bff.infrastructure.config;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.response.Response;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.infrastructure.http.restassured.contract.ContractValidationFilterFactory;
import it.pagopa.infrastructure.http.restassured.flow.FlowValidationFilterFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;

@Configuration
@RequiredArgsConstructor
public class BffRestApiClientConfig {

    public static final int CONFLICT_MAX_RETRIES = 3;
    public static final long CONFLICT_RETRY_MIN_DELAY_MS = 1000;
    public static final long CONFLICT_RETRY_MAX_DELAY_MS = 4000;

    // Aggiornamenti ravvicinati della stessa risorsa possono restituire 409: si ritenta prima di accettare il fallimento.
    public static final Filter RETRY_ON_CONFLICT =
            retryOnConflict(CONFLICT_MAX_RETRIES, CONFLICT_RETRY_MIN_DELAY_MS, CONFLICT_RETRY_MAX_DELAY_MS);

    @Value("${interop.api.base-url.bff}")
    private String basePath;

    @Value("${interop.api.openapi-url.bff}")
    private String openApiSpecUrl;

    private final TestContext testContext;

    @Bean
    public ApiClient apiClient(BffRequestSpecFactory requestSpecFactory) {
        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create);

        return ApiClient.api(apiConfig);
    }

    @Bean
    public TestPolicyFilterResolver testPolicyFilterResolver() {
        Filter contractFilter = ContractValidationFilterFactory.create(basePath, openApiSpecUrl);
        Filter flowFilter = FlowValidationFilterFactory.create(testContext::addEventualConsistencyError);
        return new TestPolicyFilterResolver(contractFilter, flowFilter);
    }

    public static Filter retryOnConflict(int maxRetries, long minDelayMs, long maxDelayMs) {
        Filter[] self = new Filter[1];
        self[0] = (requestSpec, responseSpec, ctx) -> {
            String method = requestSpec.getMethod();
            String path = requestSpec.getUserDefinedPath();
            Response response = ctx.next(requestSpec, responseSpec);
            for (int attempt = 0; attempt < maxRetries && response.statusCode() == 409; attempt++) {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs + 1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while retrying a conflicting request", e);
                }
                // ctx.next() consuma la catena: ogni retry ne richiede una nuova, senza questo filtro.
                response = RestAssured.given()
                        .spec(requestSpec)
                        .noFiltersOfType(self[0].getClass())
                        .request(method, path);
            }
            return response;
        };
        return self[0];
    }
}
