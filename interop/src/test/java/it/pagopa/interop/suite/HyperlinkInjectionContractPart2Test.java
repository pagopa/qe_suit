package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.BffDelegationContractTest;
import it.pagopa.interop.suite.contract.BffPurposeContractTest;
import it.pagopa.interop.suite.contract.BffPurposeTemplateContractTest;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
    BffDelegationContractTest.class,
    BffPurposeContractTest.class,
    BffPurposeTemplateContractTest.class,
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
public class HyperlinkInjectionContractPart2Test {
}

