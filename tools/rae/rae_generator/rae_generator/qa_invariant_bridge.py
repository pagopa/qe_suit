"""QA-approved lineage -> conservative invariant engine.

The bridge uses only approved links from historical RAE entries.  It does not
learn from today's failures, pending unapproved proposals, or auto-matches.
"""
from __future__ import annotations
from collections import defaultdict
import re
from .qa_confirmation import is_qa_confirmed
from .qa_invariant_engine import Approval, Observation, QALearningMatcher, FRAME


# Cucumber step frames contain spaces and do not match Java FRAME.
_CUCUMBER_FRAME = re.compile(r"^\s*at\s+✽\..+\(classpath:[^()\n]+\)\s*$")


def _key(row):
    return (str(row.get('_source_file', '')),
            str(row.get('id_test', '')).strip().casefold())


def build_learning(history, ignore_rules=None):
    # A historical proposal is QA evidence only after the confirmation policy
    # explicitly approves it. Auto-matches must never train or veto QA groups.
    # Legacy manual analyses without matching metadata remain eligible.
    def eligible(row):
        if not row.get('id_test') or not row.get('error_log'):
            return False
        matching = row.get('matching')
        return (not matching or is_qa_confirmed(row, approved_history=True))

    # Resolve duplicate keys first (last-row-wins), then filter QA eligibility.
    # A later generated row must not resurrect an overwritten legacy approval.
    retained = {_key(row): row for row in history
                if row.get('id_test') and row.get('error_log')}
    # Normalize historical ticket fields without mutating the original rows.
    # Whitespace-only URLs are not tickets, and a blank product URL must not
    # override a valid suite ticket when a candidate is copied to a new RAE.
    indexed = {
        key: {**row,
              'link_prod_bug': str(row.get('link_prod_bug') or '').strip(),
              'link_suite_bug': str(row.get('link_suite_bug') or '').strip()}
        for key, row in retained.items() if eligible(row)
    }
    parent = {k: k for k in indexed}

    def root(k):
        while parent[k] != k:
            parent[k] = parent[parent[k]]
            k = parent[k]
        return k

    for row in indexed.values():
        if not is_qa_confirmed(row, approved_history=True):
            continue
        matching = row.get('matching') or {}
        source = (str(matching.get('source_rae', '')),
                  str(matching.get('source_id_test', '')).strip().casefold())
        current = _key(row)
        if (source in indexed and current in indexed and source != current
                and source[1] == current[1]
                and (indexed[source].get('matching') or {}).get('status') != 'auto_matched'
                and indexed[source].get('_source_suite') == row.get('_source_suite')):
            parent[root(current)] = root(source)

    # Old manually analyzed RAEs have no ``matching`` lineage.  Multiple
    # independent QA-approved occurrences of the *same normalized full log*
    # and the *same ticket identity* are equivalent evidence, not competing
    # defect groups. Never merge unconfirmed proposals or bot auto-matches.
    identical = {}
    for row in indexed.values():
        key = _key(row)
        ticket = (str(row.get('link_prod_bug') or ''),
                  str(row.get('link_suite_bug') or ''))
        if ticket == ('', ''):
            continue
        log = (ignore_rules.normalize(str(row['error_log']))
               if ignore_rules else str(row['error_log']))
        # Preserve all content except indentation on recognized stack frames.
        # Legacy YAMLs may represent Java and Cucumber frames with spaces or
        # tabs. Never normalize arbitrary diagnostic/trailing text.
        log_identity = '\n'.join(
            line.strip() if FRAME.match(line) or _CUCUMBER_FRAME.match(line)
            else line for line in log.splitlines()
        )
        identity = (str(row.get('_source_suite', '')),
                    str(row['id_test']).strip().casefold(), ticket, log_identity)
        previous = identical.setdefault(identity, key)
        if previous != key:
            parent[root(key)] = root(previous)

    groups = defaultdict(list)
    for k, row in indexed.items():
        groups[root(k)].append(row)

    # Prepare all ticketed QA groups before registering them. Splitting a
    # conflicting lineage produces singletons, which must be interleaved with
    # unrelated groups in the original newest-first history order.
    position = {key: i for i, key in enumerate(indexed)}
    ordered_groups = []
    for k, rows in groups.items():
        ticketed = [r for r in rows
                    if r['link_prod_bug'] or r['link_suite_bug']]
        tickets = {(r['link_prod_bug'], r['link_suite_bug']) for r in ticketed}
        if len(tickets) > 1:
            # Keep conflicting approved tickets visible to the QA veto rather
            # than dropping the group and allowing a Drain3 fallback to KNOWN.
            ordered_groups.extend((_key(r), [r]) for r in ticketed)
        elif ticketed:
            # Unticketed entries cannot become independent QA approvals.
            ordered_groups.append((k, ticketed))
    ordered_groups.sort(key=lambda group: min(position[_key(r)] for r in group[1]))

    engine = QALearningMatcher()
    candidates = {}
    for k, rows in ordered_groups:
        prod = rows[0]['link_prod_bug']
        suite_bug = rows[0]['link_suite_bug']
        ticket = prod or suite_bug
        suite = str(rows[0].get('_source_suite', ''))
        test = str(rows[0]['id_test'])
        group_id = '|'.join(k)
        for row in rows:
            try:
                engine.approve(Approval(
                    Observation(suite, test, ignore_rules.normalize(str(row['error_log'])) if ignore_rules else str(row['error_log'])),
                    ticket, group_id, 'approved-history', '|'.join(_key(row))))
            except ValueError:
                break
        else:
            candidates[(suite, test, group_id)] = rows[0]
    return engine, candidates


def evaluate(engine, candidates, test_id, error, suite, ignore_rules=None):
    """Return (status, historical candidate, diagnostics) or None if no QA group.

    A conservative QA result vetoes any more permissive Drain3 auto-match.
    """
    if not any(s == suite and t.casefold() == test_id.strip().casefold()
               for s, t, _ in candidates):
        return None
    decision = engine.classify(Observation(suite, test_id, ignore_rules.normalize(error) if ignore_rules else error))
    historical = candidates.get((suite, test_id, decision.group_id)) if decision.group_id else None
    if historical is None and decision.status == 'proposed' and decision.group_id:
        historical = next((r for (s, t, gid), r in candidates.items()
                           if s == suite and t.casefold() == test_id.strip().casefold()
                           and gid == decision.group_id), None)
    return decision.status, historical, decision.reasons
