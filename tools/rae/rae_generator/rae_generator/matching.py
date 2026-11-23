"""Conservative matching: same test ID, exact fingerprint or guarded TF-IDF."""
from __future__ import annotations

import hashlib
from dataclasses import dataclass
import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

DYNAMIC_PATTERNS = [
    (r'\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b', '<uuid>'),
    (r'\b\d{4}-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}(?:[.,]\d+)?(?:Z|[+-]\d{2}:?\d{2})?\b', '<timestamp>'),
    (r'\b(?:0x)?[a-f0-9]{32,64}\b', '<hash>'),
    (r'(?<=\.java:)\d+\b', '<line>'),
]


def normalized_error(value: str) -> str:
    text = value.casefold()
    for pattern, replacement in DYNAMIC_PATTERNS:
        text = re.sub(pattern, replacement, text, flags=re.I)
    return re.sub(r'\s+', ' ', text).strip()


def fingerprint(error: str) -> str:
    return hashlib.sha256(normalized_error(error).encode('utf-8')).hexdigest()


def _signature(text: str) -> str:
    """Require the exception/message class to match before fuzzy matching."""
    primary = next((line.strip() for line in text.splitlines() if line.strip()), '')
    primary = normalized_error(primary)
    match = re.search(r'\b[\w.$]*(?:exception|error|failure)\b', primary)
    return match.group(0) if match else primary[:65]


@dataclass(frozen=True)
class Match:
    candidate: dict
    method: str  # "fingerprint" or "similarity"
    score: float


def find_match(test_id: str, error: str, candidates: list[dict], threshold: float = 0.90) -> Match | None:
    """Return the candidate and match level; both fingerprint and confident similarity can identify known bugs."""
    viable = [c for c in candidates if c.get('id_test', '').strip().casefold() == test_id.strip().casefold()
              and c.get('error_log') and c.get('procedure') and c.get('result')]
    if not viable:
        return None
    normalized = normalized_error(error)
    digest = fingerprint(error)
    # History is newest-first: prefer the most recent exact match.
    for candidate in viable:
        if fingerprint(candidate['error_log']) == digest:
            return Match(candidate, 'fingerprint', 1.0)
    viable = [c for c in viable if _signature(c['error_log']) == _signature(error)]
    if not viable or not normalized:
        return None
    texts = [normalized] + [normalized_error(c['error_log']) for c in viable]
    matrix = TfidfVectorizer(analyzer='char', ngram_range=(3, 5), lowercase=False).fit_transform(texts)
    scores = cosine_similarity(matrix[0:1], matrix[1:]).ravel()
    top = max(enumerate(scores), key=lambda pair: (pair[1], -pair[0]))
    score = float(top[1])
    return Match(viable[top[0]], 'similarity', score) if score >= threshold else None


def choose_known(test_id: str, error: str, candidates: list[dict], threshold: float = 0.90) -> dict | None:
    """Compatibility helper for callers needing only the matching historical entry."""
    match = find_match(test_id, error, candidates, threshold)
    return match.candidate if match else None
