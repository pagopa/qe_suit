package it.pagopa.interop.suite;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectClasspathResource("feature-expanded")
@SelectPackages("it.pagopa.interop.suite.contract")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/report.json," + "html:target/cucumber-reports/report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.interop"),

        // abilita parallelismo JUnit
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),

        // tag cucumber
        @ConfigurationParameter(
                key = FILTER_TAGS_PROPERTY_NAME,
                value = """
                        (@debug-client-assertion-page-ui-flow
                         or @agreement
                         or @debug-client-assertion-page-ui-behavior
                         or @dev-tools-page-ui-behavior)
                        and not @wait_for_fix
                        and not @ignore
                        """
        )
})
public class NrtStandardSuiteTest {
}
