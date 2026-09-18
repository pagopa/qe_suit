package it.pagopa.interop.m2m.v3.infrastructure.config;

import io.restassured.filter.Filter;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.infrastructure.http.restassured.contract.ContractValidationFilterFactory;
import it.pagopa.infrastructure.http.restassured.flow.FlowValidationFilterFactory;

import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class M2MV3RestApiClientConfig {

    @Value("${interop.api.base-url.m2m.v3}")
    private String basePath;

    @Value("${interop.api.openapi-url.m2m.v3}")
    private String openApiSpecUrl;

    private final TestContext testContext;

    @SneakyThrows
    @Bean
    public ApiClient m2mV3ApiClient(M2Mv3RequestSpecFactory requestSpecFactory) {
        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create);

        return ApiClient.api(apiConfig);
    }

    @Bean
    public TestPolicyFilterResolver m2mV3TestPolicyFilterResolver() {
        Filter contractFilter = ContractValidationFilterFactory.create(basePath, openApiSpecUrl);
        Filter flowFilter = FlowValidationFilterFactory.create(testContext::addEventualConsistencyError);
        return new TestPolicyFilterResolver(contractFilter, flowFilter);
    }
}
