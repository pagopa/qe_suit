package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.WebEServiceAPISpecsDownloadContractTest;
import it.pagopa.interop.suite.contract.WebEServiceTemplateAPISpecsDownloadContractTest;
import it.pagopa.interop.suite.contract.WebProducerEServiceSummaryContractTest;
import it.pagopa.interop.suite.contract.WebProducerEServiceTemplateSummaryContractTest;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        WebEServiceAPISpecsDownloadContractTest.class,
        WebEServiceTemplateAPISpecsDownloadContractTest.class,
        WebProducerEServiceSummaryContractTest.class,
        WebProducerEServiceTemplateSummaryContractTest.class
})
@ConfigurationParameters({
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
public class PIN10900SuiteTest {
}
