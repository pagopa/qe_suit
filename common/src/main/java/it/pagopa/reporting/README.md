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

Options:

- `-i, --input` required input file or directory
- `-o, --output` optional output html path
- `--no-dto-dump` disables recursive DTO debug panel
- `-h, --help` prints usage

## Notes

- Input can be a single Surefire XML file or a directory containing multiple `TEST-*.xml` files.
- The output HTML includes sidebar navigation and client-side filters.

