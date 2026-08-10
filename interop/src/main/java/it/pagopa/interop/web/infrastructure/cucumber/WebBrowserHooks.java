package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.After;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebBrowserHooks {

    private final WebPresentationGateway webPresentationGateway;

    @After
    public void afterScenario() {
        log.info("Closing web presentation gateway for scenario: {}", MDC.get("scenario"));
        webPresentationGateway.close();
    }
}