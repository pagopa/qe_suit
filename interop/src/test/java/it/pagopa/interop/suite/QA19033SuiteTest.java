package it.pagopa.interop.suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectPackages("it.pagopa.interop.suite.contract")
@IncludeTags("private-compact-catalog")
@ExcludeTags({"wait_for_qa", "ignore"})
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
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
        )
})
public class QA19033SuiteTest {
}
