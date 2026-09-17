package it.pagopa.interop.web.purpose_template.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateGeneralInfoPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebPurposeTemplateConfig {

    @Bean
    @ScenarioScope
    public PurposeTemplateCatalogPage purposeTemplateCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(PurposeTemplateCatalogPage.class);
    }

    @Bean
    @ScenarioScope
    public PurposeTemplateGeneralInfoPage purposeTemplateGeneralInfoPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(PurposeTemplateGeneralInfoPage.class);
    }
}

