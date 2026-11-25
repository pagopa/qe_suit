"""RAE CLI: generate incomplete YAML, validate, render Markdown if complete."""
import argparse
import logging
import json
from pathlib import Path
import sys
from .generator import create_rae, write_yaml
from .namespace import namespace_dir, validate_output_path
from .renderer import render
from .validator import read_yaml, validate, ValidationError, validate_original_evidences  # RAE_REVIEW_PANEL_V1
import yaml
from .parsers import ReportError
from .rae_settings import load_append_report_name


def main(argv: list[str] | None = None) -> int:
    logging.basicConfig(level=logging.INFO, format='%(levelname)s %(message)s')
    parser = argparse.ArgumentParser(description='RAE Generator')
    sub = parser.add_subparsers(dest='command', required=True)
    generate = sub.add_parser('generate', help='Generate initial YAML after NRT')
    for flag in ('reports', 'history', 'suite', 'environment', 'executor', 'source-branch', 'run-url', 'metadata', 'output'):
        generate.add_argument('--' + flag, required=True)
    generate.add_argument('--namespace', required=True, help='RAE storage namespace (e.g. interop, send)')
    generate.add_argument('--ignore-rules', type=Path, help='Regole di normalizzazione versionate per il matching')
    generate.add_argument('--rae-settings', type=Path, help='Politica degli ID test')
    finalize = sub.add_parser('finalize', help='Validate YAML and generate Markdown')
    finalize.add_argument('yaml_file', type=Path)
    finalize.add_argument('--write', action='store_true', help='Write MD next to YAML')
    finalize.add_argument('--qa-reviewed', action='store_true', help='QA review verified by workflow')
    finalize.add_argument('--original-yaml', type=Path, help='Immutable generator-created original YAML')
    check = sub.add_parser('check', help='Validate YAML and verify committed Markdown')
    check.add_argument('yaml_file', type=Path)
    check.add_argument('--qa-reviewed', action='store_true', help='QA review verified by workflow')
    check.add_argument('--original-yaml', type=Path, help='Immutable generator-created original YAML')
    args = parser.parse_args(argv)
    try:
        if args.command == 'generate':
            metadata = json.loads(Path(args.metadata).read_text(encoding='utf-8'))
            output = validate_output_path(Path(args.history), args.namespace, Path(args.output))
            settings = (args.rae_settings or Path(__file__).resolve().parents[1]
                        / 'config' / 'rae_settings.yaml')
            append_report_name = load_append_report_name(settings)
            data = create_rae(Path(args.reports), namespace_dir(Path(args.history), args.namespace),
                              suite=args.suite, environment=args.environment,
                              executor=args.executor, source_branch=args.source_branch,
                              run_url=args.run_url, data_ora=metadata['data_ora'],
                              durata=metadata['durata'], ignore_rules_path=args.ignore_rules,
                              append_report_name=append_report_name)
            write_yaml(data, output)
            print(f'Generated {args.output}: {data["rae"]["numero_ko"]} KO')
        else:
            data = read_yaml(args.yaml_file)
            if args.qa_reviewed and not args.original_yaml:
                raise ValidationError('--qa-reviewed richiede --original-yaml')
            original = validate(yaml.safe_load(args.original_yaml.read_text(encoding='utf-8')), draft=True) if args.original_yaml else None
            if original is not None:
                validate_original_evidences(data, original)
            md = args.yaml_file.with_suffix('.md')
            expected = render(data, original_data=original, qa_reviewed=args.qa_reviewed)
            if args.command == 'check':
                if not md.is_file() or md.read_text(encoding='utf-8') != expected:
                    raise ValidationError(f'Markdown assente o non sincronizzato: {md}')
                print(f'Validated {args.yaml_file} and {md}')
            elif args.write:
                if not md.is_file() or md.read_text(encoding='utf-8') != expected:
                    md.write_text(expected, encoding='utf-8')
                    print(f'Updated {md}')
                else:
                    print(f'Already up to date: {md}')
            else:
                print(expected)
        return 0
    except (ValidationError, ReportError, ValueError, KeyError, OSError) as exc:
        print(f'RAE ERROR: {exc}', file=sys.stderr)
        return 1


if __name__ == '__main__':
    raise SystemExit(main())
