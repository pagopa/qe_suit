package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.WebEServiceContractTest;
import it.pagopa.interop.suite.contract.WebProducerAgreementContractTest;
import it.pagopa.interop.suite.contract.WebProducerPurposeContractTest;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        WebEServiceContractTest.class,
        WebProducerAgreementContractTest.class,
        WebProducerPurposeContractTest.class
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
public class WebBrokenAccessControlApiSuiteTest {
}
