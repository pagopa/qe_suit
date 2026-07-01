package it.pagopa.send.domain.web.config.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebHooks {

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");

    private final WebPresentationGateway webPresentationGateway;

    @Before
    public void beforeScenario(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                takeScreenshot(scenario);
            }
        } finally {
            log.info("Closing web presentation gateway for scenario: {}", MDC.get("scenario"));
            webPresentationGateway.close();
            MDC.remove("scenario");
        }
    }

    private void takeScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = webPresentationGateway.takeScreenshot();
            scenario.attach(screenshot, "image/png", scenario.getName());

            Files.createDirectories(SCREENSHOT_DIR);
            String fileName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_") + "-" + scenario.getId() + ".png";
            Files.write(SCREENSHOT_DIR.resolve(fileName), screenshot);
        } catch (IOException e) {
            log.warn("Unable to save screenshot for failed scenario: {}", scenario.getName(), e);
        } catch (Exception e) {
            log.warn("Unable to take screenshot for failed scenario: {}", scenario.getName(), e);
        }
    }
}
