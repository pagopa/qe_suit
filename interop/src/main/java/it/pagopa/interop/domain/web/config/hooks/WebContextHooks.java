package it.pagopa.interop.domain.web.config.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebContextHooks {

    private final WebPresentationGateway webPresentationGateway;

    @Before
    public void beforeScenario(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
    }

    @After
    public void afterScenario() {
        try {
            webPresentationGateway.close();
        } finally {
            MDC.remove("scenario");
        }
    }
}