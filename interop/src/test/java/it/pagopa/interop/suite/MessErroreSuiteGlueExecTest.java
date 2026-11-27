package it.pagopa.interop.suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

// FIXME release 2.25: temporaneo per permettere il collaudo dei soli scenari interessati dall'allineamento/implementazione del glue. Rimuovere.
@Suite
@GenerateContractReport
@IncludeEngines({"cucumber"})
@SelectClasspathResource("feature-expanded")
@ExcludeTags({"wait_for_qa", "ignore"})
@IncludeTags({"err_msg_glue_exec"})
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty," + "json:target/cucumber-reports/report-errorMsg-glueExec.json," + "html:target/cucumber-reports/report-errorMsg-glueExec.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.interop"),

        // disabilita parallelismo JUnit
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false"),
        @ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "false"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "same_thread"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.fixed.parallelism", value = "1"),

        // disabilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
})
public class MessErroreSuiteGlueExecTest {
}
