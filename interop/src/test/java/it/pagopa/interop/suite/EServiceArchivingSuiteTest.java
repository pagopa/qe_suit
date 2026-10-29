package it.pagopa.interop.suite;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("feature-expanded/eservice/archive-eservice.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.interop")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@eservice-archiving")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,json:target/cucumber-reports/eservice-archiving.json,html:target/cucumber-reports/eservice-archiving.html"
)
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
public class EServiceArchivingSuiteTest {
}
