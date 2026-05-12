package it.pagopa.interop.domain.web.config.hooks;

import io.cucumber.java.After;
import it.frontend.e2e.framework.core.config.SuiteContext;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebContextHooks {

    private final WebPresentationGateway webPresentationGateway;

    @After
    public void afterScenario() {
       webPresentationGateway.close();
    }
}