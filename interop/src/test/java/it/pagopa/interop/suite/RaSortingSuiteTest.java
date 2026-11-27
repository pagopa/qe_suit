package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines({"junit-jupiter"})
@SelectPackages("it.pagopa.interop.suite.integration")
@IncludeTags("risk-analysis-sorting")
@ExcludeTags({"wait_for_qa", "ignore"})
public class RaSortingSuiteTest {
}
