package it.pagopa.send.bff.infrastructure.config;

import io.restassured.filter.Filter;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.infrastructure.http.restassured.contract.ContractValidationFilterFactory;
import it.pagopa.infrastructure.http.restassured.flow.FlowValidationFilterFactory;

import it.pagopa.send.generated.openapi.clients.internal.template.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TemplateApiClientConfig {

    @Value("${template.api.base-url}")
    private String basePath;

    @Value("${template.api.openapi-url}")
    private String openApiSpecUrl;

    private final TestContext testContext;

    @Bean
    public ApiClient apiClient(TemplateRequestSpecFactory requestSpecFactory) {
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
}