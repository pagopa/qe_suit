package it.pn.frontend.e2e.suite;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResources({
        @SelectClasspathResource("features")
})
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/report.json," +
                "html:target/cucumber-reports/report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pn.frontend.e2e"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"loadPage"})
public class RunCucumberTest {

}
