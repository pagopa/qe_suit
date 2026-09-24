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
- Current implementation uses a single primary expandable navigation in the main panel (`class -> factory -> case`) without legacy sidebar rendering.
- Report title is `QE SUIT Contract Test HTML Report`.
- Top section order is: `Run summary`, `Filters`, `Navigation`.
- Factory details render concrete cases in a 2-column table (`test/case`, `status`) with inline expansion by clicking the whole case row.
- The `Environment properties (safe subset)` panel keeps a short always-visible security note and uses collapsible blocks (closed by default) for both safe properties and excluded keys.
- The report exposes the exact environment property keys excluded in the specific run, under a dedicated collapsible section (`Excluded property keys for this run`).
- Aggregate color precedence for class/factory nodes: dark red if any `ERROR`, red if any `FAILED` (and no `ERROR`), yellow if no `FAILED`/`ERROR` but at least one `UNKNOWN` or `SKIPPED`, green only if all `PASSED`; with compact `F/E/U/S` badge.
- Status colors are applied to badges only, keeping case names readable against the row background.

### Low-priority objective

- Once functional and UX behavior is stable, optimize report size by adding compressed output support to reduce disk footprint.

### DTO annotations retrospective

- Original purpose: use DTO annotations as declarative metadata for rendering semantics (role, labels, order, format, filtering hints), with the option to reduce hardcoded writer logic.
- What is retained and useful today: `@ReportField` metadata is effectively used by the recursive DTO debug dump (`label`, `order`, `format`) and improves deep-debug readability with low maintenance cost.
- What is not applied yet in the primary UX path: `@ReportNode` / `ReportNodeRole` and `filterable` metadata do not currently drive main HTML navigation, filters, or aggregate behavior.
- Current decision: keep annotations because they provide concrete debug value at low cost, while preserving an explicit main writer for domain-specific UX rules.
- Near-term direction: prefer a hybrid approach (explicit primary writer plus annotation-driven technical/debug sections) instead of a fully dynamic reflection-only writer.


