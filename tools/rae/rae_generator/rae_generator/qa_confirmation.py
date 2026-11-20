"""One QA confirmation policy for rendering and runtime historical learning.

Never mutate matching.status during CI. The generator's pending state is
preserved as an immutable record of the proposed match.
"""
from __future__ import annotations
import hashlib
import json

ANALYSIS_FIELDS = ('procedure', 'result', 'link_prod_bug', 'link_suite_bug')


def analysis_digest(entry: dict) -> str:
    """The generator's fingerprint of the prefilled QA fields."""
    values = {k: str(entry.get(k, '') or '') for k in ANALYSIS_FIELDS}
    encoded = json.dumps(values, ensure_ascii=False, sort_keys=True,
                         separators=(',', ':')).encode('utf-8')
    return hashlib.sha256(encoded).hexdigest()


def is_qa_confirmed(entry: dict, *, approved_history: bool = False,
                    original: dict | None = None, qa_reviewed: bool = False) -> bool:
    """Decide QA equivalence once for CI/view and history/learning.

    - On a PR: require verified human QA edit in the PR, trusted initial
      generated proposal and unchanged QA analysis fields/identity/metadata.
    - On develop: only approved historical pending entries whose stored
      proposal digest still matches the final analysis count as implicit QA
      confirmations. Explicit confirmed is retained for legacy compatibility.
    - Auto matches NEVER count as QA training evidence.

    Future tolerance for minor procedure edits belongs only in this function.
    """
    matching = entry.get('matching')
    if not isinstance(matching, dict):
        return False
    status = matching.get('status')
    if status == 'auto_matched':
        return False
    if approved_history:
        if status == 'confirmed':
            return True
        return (status == 'pending_confirmation'
                and isinstance(matching.get('proposed_digest'), str)
                and matching['proposed_digest'] == analysis_digest(entry))
    if not qa_reviewed or original is None or status != 'pending_confirmation':
        return False
    initial = original.get('matching')
    if not isinstance(initial, dict) or initial.get('status') != 'pending_confirmation':
        return False
    if (str(entry.get('id_test', '')) != str(original.get('id_test', ''))
            or str(entry.get('error_log', '')) != str(original.get('error_log', ''))):
        return False
    for name in ('source_rae', 'source_id_test', 'proposed_digest'):
        if matching.get(name) != initial.get(name):
            return False
    initial_digest = initial.get('proposed_digest')
    return (isinstance(initial_digest, str)
            and initial_digest == analysis_digest(original)
            and initial_digest == analysis_digest(entry))


def original_entries_by_id(data: dict) -> dict[str, dict | None]:
    """Avoid confirming duplicate id_test entries using a wrong baseline."""
    result: dict[str, dict | None] = {}
    for section in ('ko', 'ko_know'):
        for row in data.get(section, []):
            key = str(row.get('id_test', ''))
            result[key] = None if key in result else row
    return result
