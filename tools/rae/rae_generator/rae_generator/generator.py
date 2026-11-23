"""Build a single YAML per execution using approved develop history."""
from __future__ import annotations
from pathlib import Path
import logging
import re
import yaml
from yaml.emitter import ScalarAnalysis
from .history import load_history
from .progressive import ProgressiveMatcher, analysis_digest
from .parsers import collect_reports
from .ignore_rules import IgnoreRules
from .review_evidence import snapshot  # RAE_REVIEW_PANEL_V1

FIELDS = ('id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')


def create_rae(report_root: Path, history_dir: Path, *, suite: str, environment: str,
               executor: str, source_branch: str, run_url: str, data_ora: str,
               durata: str, ignore_rules_path: Path | None = None) -> dict:
    if not re.fullmatch(r'[A-Za-z0-9_.-]+', suite):
        raise ValueError('Runner name non valido')
    if environment not in ('qa', 'dev'):
        raise ValueError('Ambiente non valido')
    if ignore_rules_path is None:
        ignore_rules_path = Path(__file__).resolve().parents[1] / 'config' / 'ignore_rules.yaml'
    rules = IgnoreRules.load(ignore_rules_path)
    failures = collect_reports(report_root)
    history = load_history(history_dir, suite)
    matcher = ProgressiveMatcher(history, ignore_rules=rules) if history and failures else None
    data = {
        'rae': {'release': '', 'scope': 'NIGHTLY', 'issue_jira': '', 'data_ora': data_ora,
                'durata': durata, 'numero_ko': len(failures), 'run': run_url,
                'suite': suite, 'environment': environment, 'executor': executor,
                'source_branch': source_branch},
        'ko_know': [], 'ko': []
    }
    for failure in failures:
        decision, previous = (matcher.classify(failure.id_test, failure.error_log, suite=suite)
                              if matcher else ('new', None))
        entry = {'id_test': failure.id_test, 'error_log': failure.error_log}
        for field in FIELDS[2:]:
            entry[field] = str(previous.get(field, '') or '') if previous else ''
        if previous is not None:
            matching = {
                'status': 'auto_matched' if decision == 'known' else 'pending_confirmation',
                'source_rae': previous['_source_file'],
                'source_id_test': previous['id_test'],
            }
            if decision == 'proposed':
                matching['proposed_digest'] = analysis_digest(entry)
                # Diagnostic snapshot only; never changes classifier decisions.
                if matcher is not None:
                    evidence = snapshot(matcher.last_review_reasons)
                    if evidence:
                        matching['review_evidence'] = evidence
            entry['matching'] = matching
            logging.info('RAE %s per %s da %s', decision, failure.id_test, previous['_source_file'])
        # The ticket alone can never promote a suggestion to a known KO.
        is_known = (decision == 'known' and bool(entry['link_prod_bug'] or entry['link_suite_bug']))
        data['ko_know' if is_known else 'ko'].append(entry)
    return data


class RaeDumper(yaml.SafeDumper):
    """Prefer readable literal blocks while preserving Java stack-trace tabs."""

    def analyze_scalar(self, scalar):
        analysis = super().analyze_scalar(scalar)
        # PyYAML disables the literal style when a scalar contains tabs.
        # Tabs *inside* the block content are valid YAML. Permit them only
        # for multiline strings without other control characters.
        if ('\n' in scalar and '\t' in scalar
                and all(ord(ch) >= 32 or ch in '\t\n' for ch in scalar)):
            return ScalarAnalysis(
                analysis.scalar, analysis.empty, analysis.multiline,
                analysis.allow_flow_plain, analysis.allow_block_plain,
                analysis.allow_single_quoted, analysis.allow_double_quoted,
                True,
            )
        return analysis


def _represent_string(dumper, value):
    return dumper.represent_scalar(
        'tag:yaml.org,2002:str', value, style='|' if '\n' in value else None,
    )


RaeDumper.add_representer(str, _represent_string)


def write_yaml(data: dict, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(yaml.dump(data, Dumper=RaeDumper, allow_unicode=True, sort_keys=False, width=110), encoding='utf-8')
