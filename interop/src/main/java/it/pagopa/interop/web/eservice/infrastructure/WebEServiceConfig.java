package it.pagopa.interop.web.eservice.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCatalogPage;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceDetailPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class WebEServiceConfig {

    @Bean
    @ScenarioScope
    public EServiceCatalogPage eServiceCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCatalogPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceCreatePage eServiceCreationPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCreatePage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceDetailPage eServiceDetailPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceDetailPage.class);
    }

}
