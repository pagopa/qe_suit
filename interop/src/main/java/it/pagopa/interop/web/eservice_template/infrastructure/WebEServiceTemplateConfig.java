package it.pagopa.interop.web.eservice_template.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.eservice_template.infrastructure.page.TemplateEServiceDetailErogatorePage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class WebEServiceTemplateConfig {

    @Bean
    @ScenarioScope
    public TemplateEServiceDetailErogatorePage templateEServiceDetailErogatorePage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(TemplateEServiceDetailErogatorePage.class);
    }
}

