package it.pagopa.interop.suite;

import it.pagopa.interop.suite.contract.BffPurposeTemplateContractTest;
import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@GenerateContractReport // opzionale, se vuoi generare il report dei contract test
@IncludeEngines("junit-jupiter")
@SelectClasses(BffPurposeTemplateContractTest.class)
public class BffPurposeTemplateContractSuiteTest {
}