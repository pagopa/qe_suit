package it.pagopa.interop.new_arch.web.infrastructure.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebBrowserHooks {

    private final WebPresentationGateway webPresentationGateway;

    @Before
    public void beforeScenario(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
    }

    @After
    public void afterScenario() {
        try {
            log.info("Closing web presentation gateway for scenario: {}", MDC.get("scenario"));
            webPresentationGateway.close();
        } finally {
            MDC.remove("scenario");
        }
    }
}