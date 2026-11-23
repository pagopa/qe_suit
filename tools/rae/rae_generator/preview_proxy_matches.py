#!/usr/bin/env python3
"""Read-only replay of proxy-only proposed entries against approved history."""
from __future__ import annotations
import argparse
from collections import Counter
from pathlib import Path
import yaml

from rae_generator.history import load_history
from rae_generator.ignore_rules import IgnoreRules
from rae_generator.progressive import ProgressiveMatcher


def main():
    parser = argparse.ArgumentParser(description='Anteprima read-only, non riscrive la RAE')
    parser.add_argument('--yaml', type=Path, required=True)
    parser.add_argument('--history', type=Path, required=True,
                        help='Directory rae/<namespace> di un checkout develop approvato')
    parser.add_argument('--ignore-rules', type=Path,
                        default=Path(__file__).resolve().parent / 'config' / 'ignore_rules.yaml')
    args = parser.parse_args()
    data = yaml.safe_load(args.yaml.read_text(encoding='utf-8'))
    suite=data['rae']['suite']
    history=load_history(args.history, suite)
    if not history:
        raise SystemExit('ERRORE: nessuno storico QA approvato: verifica --history')
    rules=IgnoreRules.load(args.ignore_rules)
    matcher=ProgressiveMatcher(history, ignore_rules=rules)
    stats=Counter()
    for entry in data.get('ko', []):
        matching=entry.get('matching') or {}
        if matching.get('status') != 'pending_confirmation':
            continue
        evidence=matching.get('review_evidence') or []
        if not evidence or {e.get('code') for e in evidence} != {'PROXY_ORDINAL_UNSEEN'}:
            continue
        status, candidate=matcher.classify(entry['id_test'], entry['error_log'], suite=suite)
        stats[status]+=1
        reasons=sorted({r.code for r in matcher.last_review_reasons})
        print(f'{status.upper():8}  {entry["id_test"][:95]}')
        if status != 'known':
            print('         Motivi:', ', '.join(reasons) or 'candidato storico non confermato')
        if candidate and status == 'known' and not (candidate.get('link_prod_bug') or candidate.get('link_suite_bug')):
            print('         ATTENZIONE: known senza bug link, resterà tra i KO da analizzare')
    print('\nRiepilogo proposte causate solo da proxy:', dict(stats))
    print('Nessun file modificato. Il numero di known dipende dalle conferme QA in develop.')

if __name__=='__main__':
    main()
