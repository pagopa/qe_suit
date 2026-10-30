package it.pagopa.send.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.send.suite.contract.BffOwnerPgAuthorizationContractTest;
import it.pagopa.send.suite.contract.BffRecipientAuthorizationContractTest;
import it.pagopa.send.suite.contract.BffSenderPaAuthorizationContractTest;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        BffOwnerPgAuthorizationContractTest.class,
        BffRecipientAuthorizationContractTest.class,
        BffSenderPaAuthorizationContractTest.class,
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
public class BffRoleAuthorizationTest {
}
