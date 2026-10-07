package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.BffCertifiedAttributeContractTest;
import it.pagopa.interop.suite.contract.BffCertifiedDiscreteAttributeContractTest;
import it.pagopa.interop.suite.contract.BffClientContractTest;
import it.pagopa.interop.suite.contract.BffDeclaredAttributeContractTest;
import it.pagopa.interop.suite.contract.BffEServiceContractTest;
import it.pagopa.interop.suite.contract.BffEServiceTemplateContractTest;
import it.pagopa.interop.suite.contract.BffProducerKeychainContractTest;
import it.pagopa.interop.suite.contract.BffVerifiedAttributeContractTest;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
    BffClientContractTest.class,
    BffProducerKeychainContractTest.class,
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
/* TODO 29/09/2026 classe di comodo che raccoglie i soli test di contratto implementati per
*   https://pagopa.atlassian.net/browse/QA-18239 , destinata a essere rimossa insieme al branch
*   che la contiene */
public class QA18239Test {
}
