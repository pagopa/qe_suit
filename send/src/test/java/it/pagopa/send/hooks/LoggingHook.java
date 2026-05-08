package it.pagopa.send.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHook {
    private static final Logger logger = LoggerFactory.getLogger("SCENARIO_TRACKING");

    @Before
    public void beforeScenario(Scenario scenario) {
        // Estrai il nome del file dal path in modo sicuro
        String featureFileName = extractFeatureFileName(scenario);

        // Genera un ID univoco per lo scenario
        String scenarioId = String.format("%s::%s::%d",
                featureFileName,
                scenario.getName(),
                System.currentTimeMillis()
        );

        // Inserisci nel MDC
        MDC.put("scenario", scenarioId);
        MDC.put("feature", featureFileName);
        MDC.put("scenarioName", scenario.getName());

        // Log di inizio
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("SCENARIO STARTED: [{}]", scenario.getName());
        logger.info("Feature: {}", scenario.getUri());
        logger.info("Tags: {}", scenario.getSourceTagNames());
        logger.info("═══════════════════════════════════════════════════════");
    }

    @After
    public void afterScenario(Scenario scenario) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("SCENARIO COMPLETED: [{}] - Status: {}",
                scenario.getName(),
                scenario.getStatus()
        );
        logger.info("═══════════════════════════════════════════════════════");

        MDC.clear();
    }

    private String extractFeatureFileName(Scenario scenario) {
        try {
            String uriString = scenario.getUri().toString();

            if (uriString.contains("/")) {
                return uriString.substring(uriString.lastIndexOf("/") + 1);
            }
            return uriString;
        } catch (Exception e) {
            logger.warn("Could not extract feature name, using scenario name instead", e);
            return scenario.getName().replace(" ", "_");
        }
    }
}