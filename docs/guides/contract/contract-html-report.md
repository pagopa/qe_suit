# Contract Test HTML Report

This guide explains how to enable and use the Contract Test HTML Report in `interop`.

## Purpose

Generate a human-readable HTML report from the JUnit Open Test Reporting output.

```text
Report-enabled runner
    ↓
JUnit Platform
    ↓
target/junit-platform-reports/open-test-report.xml
    ↓
ContractReportGenerator
    ↓
target/contract-test/report.html
```

The report engine lives in the `common` module (`it.pagopa.infrastructure.reporting.contract`), while activation and configuration are module-specific (for example `interop`).

## Scope

The report is **runner-scoped**, not single-test-scoped.

- Running a report-enabled runner: report generated
- Running a single `*ContractTest` class directly: no report
- Running unit tests / ArchUnit / normal tests: no report

## 1. Annotate the runner

In `interop`, annotate the suite runner:

```java
import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;

@Suite
@GenerateContractReport
public class NrtStandardSuiteTest {
}
```

## 2. Enable JUnit Open Test Report

File: `interop/src/main/resources/junit-platform.properties`

```properties
junit.platform.reporting.open.xml.enabled=true
junit.platform.reporting.output.dir=target/junit-platform-reports
```

## 3. Configure report channels

File: `interop/src/main/resources/application.yaml`

```yaml
contract-report:
  channels:
    bff:
      label: BFF
      class-prefix: Bff
      target-type: OPENAPI
      openapi: @bff.openapi.url@
    web:
      label: WEB
      class-prefix: Web
      target-type: PAGE
```

## 4. Run

Example:

```bash
mvn test -Dtest=NrtStandardSuiteTest
```

Output:

- XML: `interop/target/junit-platform-reports/open-test-report.xml`
- HTML: `interop/target/contract-test/report.html`

## Expected behavior

- IntelliJ Run on report-enabled runner: report generated
- `mvn test -Dtest=<report-enabled-runner>`: report generated
- IntelliJ Run on a single `*ContractTest`: no report
- `mvn test -Dtest=BffAgreementContractTest`: no report
- unit tests / ArchUnit / normal tests: no report

## Troubleshooting

1. No `report.html` generated
   - Ensure the executed runner has `@GenerateContractReport`.
   - Ensure `junit.platform.reporting.open.xml.enabled=true`.
1. Open test XML missing
   - Check `target/junit-platform-reports/open-test-report.xml`.
1. Listener disabled by runtime flags
   - Check your JVM/system properties in the run configuration.
1. Channel mapping errors
   - Ensure `class-prefix` values match contract class names.
   - Ensure each `OPENAPI` channel has a valid `openapi` location.
