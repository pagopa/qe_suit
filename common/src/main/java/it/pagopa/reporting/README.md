# Reporting package

This package contains the Surefire-to-HTML reporting tool.

## What it does

- Parses Surefire XML reports (`TEST-*.xml`)
- Builds a canonical Java DTO
- Generates a single self-contained HTML report
- Supports hierarchical navigation:
  - test class
  - test factory
  - concrete runtime case

## Entry point

`it.pagopa.reporting.app.ReportingMain`

## CLI

```bash
java it.pagopa.reporting.app.ReportingMain --input <surefire-xml-or-directory> --output <report.html>
```

PowerShell example from the `common` module:

```powershell
Set-Location "C:\Users\vmassaro\Devel\PagoPa\qe_suit\qe_suit\common"
java -cp target/classes it.pagopa.reporting.app.ReportingMain --input src/test/resources/it/pagopa/reporting/TEST-it.pagopa.interop.suite.contract.BffEServiceTemplateContractTest.xml --output target/reporting-real-sample.html
```

Options:

- `-i, --input` required input file or directory
- `-o, --output` optional output html path
- `--no-dto-dump` disables recursive DTO debug panel
- `-h, --help` prints usage

## Output location

- If `--output` / `-o` is provided, the HTML report is written exactly to that path.
- If `--output` is omitted:
  - when `--input` is a **directory**, output is `<input>/surefire-html-report.html`
  - when `--input` is a **file**, output is created in the same parent directory with `.html` extension (example: `TEST-example.xml` -> `TEST-example.html`)
- The application prints the resolved path on success (`Report generated: ...`).

## Notes

- Input can be a single Surefire XML file or a directory containing multiple `TEST-*.xml` files.
- The output HTML includes hierarchical navigation and client-side filters.
- Current implementation shows primary expandable navigation in the main panel (`class -> factory -> case`) and keeps the legacy sidebar renderer as fallback (not active by default).
- Factory details render concrete cases in a 2-column table (`test/case`, `status`) with expandable detail rows.
- Aggregate color precedence for class/factory nodes: dark red if any `ERROR`, red if any `FAILED` (and no `ERROR`), yellow if no `FAILED`/`ERROR` but at least one `UNKNOWN` or `SKIPPED`, green only if all `PASSED`; with compact `F/E/U/S` badge.

### Agreed UX corrections (pending implementation)

- Concrete case click must expand details inline (no jump to another section of the page).
- Case-level links will be removed/replaced by direct inline expansion behavior.
- The duplicated lower detailed section (`Class -> Factory -> Concrete case`) will be removed.
- Top section order will be: `Run summary`, then `Filters`, then `Navigation`.
- Report title will be updated to `QE SUIT Contract Test HTML Report`.

