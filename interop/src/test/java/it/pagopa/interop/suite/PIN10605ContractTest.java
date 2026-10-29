package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.WebPurposeTemplateContractTest;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        WebPurposeTemplateContractTest.class
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
/* Parte 2 di Hyperlink injection: raccoglie i test di contratto implementati successivamente alla parte 1. */
public class PIN10605ContractTest {
}

