package it.pagopa.interop.suite.business;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@GenerateContractReport
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectClasspathResource("feature-expanded")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/report.json," + "html:target/cucumber-reports/report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.interop"),

        // Jupiter: abilita parallelismo solo quando lancio questa suite
        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.enabled",
                value = "true"
        ),

        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.mode.default",
                value = "concurrent"
        ),

        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.config.strategy",
                value = "dynamic"
        ),

        // Cucumber
        @ConfigurationParameter(
                key = EXECUTION_MODE_FEATURE_PROPERTY_NAME,
                value = "concurrent"
        ),

        // tag cucumber
        @ConfigurationParameter(
                key = FILTER_TAGS_PROPERTY_NAME,
                value = """
                        @eservice-attribute-threshold
                        and not @wait_for_fix
                        and not @ignore
                        """
        )
})
public class EServiceAttributeThresholdTest {
}
