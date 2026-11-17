package it.pagopa.interop.suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectClasspathResource("feature-expanded")
@ExcludeTags({"wait_for_qa", "ignore"})
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
                value = "@work-item:QA-17458"

        )
})
public class QA17458SuiteTest {
}
