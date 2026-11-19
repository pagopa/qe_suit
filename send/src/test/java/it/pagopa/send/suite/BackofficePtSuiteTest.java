package it.pagopa.send.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectClasspathResources;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Test del BackOffice dei Partner Tecnologici, che oggi esiste solo su dev: gli scenari Cucumber {@code @backofficePt}
 * (login SPID reale) e i contract test con tag {@code backofficePt} (sessione forzata con i session token dei segreti
 * dell'ambiente): per aggiungere un test alla suite basta il tag.
 */
@Suite
@GenerateContractReport
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectClasspathResources({
        @SelectClasspathResource("features-expanded")
})
@SelectPackages("it.pagopa.send.suite")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/report.json," +
                "html:target/cucumber-reports/report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.send"),
        // esecuzione sequenziale: con molti browser in parallelo il login di SelfCare non è stabile
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "same_thread"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"backofficePt"})
public class BackofficePtSuiteTest {
}
