package it.pagopa.interop.web.eservice.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceEditPage;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceViewPage;
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

    @Bean
    @ScenarioScope
    public ProducerEServiceDetailPage producerEServiceDetailPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(ProducerEServiceDetailPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceEditPage eServiceEditPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceEditPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceViewPage eServiceViewPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceViewPage.class);
    }

    @Bean
    @ScenarioScope
    public ProducerAgreementDetailPage producerAgreementDetailPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(ProducerAgreementDetailPage.class);
    }
}
