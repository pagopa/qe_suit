package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.BffDelegationContractTest;
import it.pagopa.interop.suite.contract.BffPurposeContractTest;
import it.pagopa.interop.suite.contract.WebProducerEServiceAttributesContractTest;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        WebProducerEServiceAttributesContractTest.class
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
public class PIN10046SuiteTest {
}

