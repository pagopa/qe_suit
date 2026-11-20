"""Adapters around PRODUCTION rae_generator code, never a cloned classifier."""
from __future__ import annotations
from pathlib import Path
from tempfile import TemporaryDirectory
import json


def require_production_matcher():
    try:
        from rae_generator.progressive import ProgressiveMatcher
    except ImportError as exc:
        raise RuntimeError('Impossibile importare rae_generator.progressive dal repository qe_suit. '
                           'Avvia con PYTHONPATH puntato a tools/rae/rae_generator e usa la .venv del progetto.') from exc
    return ProgressiveMatcher


def make_manual_history(events: list[dict], suite: str, test_id: str) -> list[dict]:
    """Synthesize QA-approved history in memory for an explicit sandbox only.

    A new group starts with its first event; later events in the same group
    get an explicit QA confirmation link to its preceding event.
    """
    seen: dict[str, dict] = {}
    items = []
    for index, event in enumerate(events, 1):
        group = str(event['group']).strip()
        ticket = str(event['ticket']).strip()
        if not group or not ticket:
            raise ValueError(f'Evento {index}: gruppo e ticket obbligatori')
        if not str(event['error']).strip():
            raise ValueError(f'Evento {index}: error_log obbligatorio')
        if group in seen and seen[group]['link_prod_bug'] != ticket:
            raise ValueError(f'Evento {index}: il ticket del gruppo {group} è cambiato senza nuova identità')
        name = f'rae_{suite}_202611{index:02d}120000.yaml'  # Valid local test file identity
        row = {
            '_source_file': name, '_source_suite': suite, '_source_environment': 'qa',
            'id_test': test_id, 'error_log': str(event['error']),
            'procedure': f'[SIMULATO] Analisi QA del gruppo {group}',
            'result': f'[SIMULATO] Difetto {ticket}',
            'link_prod_bug': ticket, 'link_suite_bug': '',
        }
        if group in seen:
            previous = seen[group]
            row['matching'] = {
                'status': 'confirmed', 'source_rae': previous['_source_file'],
                'source_id_test': test_id,
            }
        seen[group] = row
        items.append(row)
    return list(reversed(items))


def run_match(history: list[dict], suite: str, test_id: str, error: str) -> dict:
    Matcher = require_production_matcher()
    matcher = Matcher(history)
    decision, prior = matcher.classify(test_id, error, suite=suite)
    reasons = tuple(getattr(matcher, 'last_review_reasons', ()) or ())
    return {
        'status': decision,
        'ticket': (prior.get('link_prod_bug') or prior.get('link_suite_bug')) if prior else None,
        'source_rae': prior.get('_source_file') if prior else None,
        'source_test_id': prior.get('id_test') if prior else None,
        'reasons': reasons,
        'signature_count': sum(len(g.approvals) >= 2 for g in getattr(matcher.qa_engine, 'groups', {}).values()),
        'history_count': len(history),
        'production_class': f'{Matcher.__module__}.{Matcher.__qualname__}',
        'production_file': str(Path(__import__(Matcher.__module__, fromlist=['_']).__file__).resolve()),
    }


def load_uploaded_raes(uploaded: list[tuple[str, bytes]], suite: str, limit: int = 7) -> list[dict]:
    """Uses production history.load_history, with a temporary checkout-like dir.
    The tool does not verify that uploads were actually merged on develop.
    """
    from rae_generator.history import load_history
    with TemporaryDirectory(prefix='rae_simulator_') as tmp:
        directory = Path(tmp)
        for filename, content in uploaded:
            basename = Path(filename).name
            if not basename.startswith('rae_') or not basename.endswith('.yaml'):
                raise ValueError(f'Il file non sembra una RAE YAML: {basename}')
            (directory / basename).write_bytes(content)
        return load_history(directory, suite, limit=limit)


def snapshot_json(events: list[dict], suite: str, test_id: str, current_error: str) -> str:
    return json.dumps({'suite': suite, 'test_id': test_id, 'events': events,
                       'current_error': current_error}, ensure_ascii=False, indent=2)
