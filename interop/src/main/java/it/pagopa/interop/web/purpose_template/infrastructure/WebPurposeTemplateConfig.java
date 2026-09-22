package it.pagopa.interop.web.purpose_template.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class WebPurposeTemplateConfig {
    @Bean
    @ScenarioScope
    public PurposeTemplateCatalogPage purposeTemplatePage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(PurposeTemplateCatalogPage.class);
    }
}

