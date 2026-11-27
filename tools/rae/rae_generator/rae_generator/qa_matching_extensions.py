"""QA-backed conservative normalization of dynamic tokens and structured log fields.

Only QA-approved historical variations are generalized. This module does not
train on the incoming KO and does not ignore changed JSON types or keys.
"""
from __future__ import annotations
import copy
import json
import re
from datetime import datetime

ID_TAIL = re.compile(r'^(?P<anchor>.*?[A-Za-z][A-Za-z0-9]*(?:-[A-Za-z0-9]+)*-)(?P<value>[A-Za-z0-9]{6,})(?P<suffix>[^A-Za-z0-9]*)$')
JSON_MARKER = re.compile(r'(?<!\S)-\s+(?P<name>responseBody|payload):\s*')
UUID = re.compile(r'(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$')
STAMP_VALUE = re.compile(r'^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d+)?(?:Z|[+-]\d\d:?\d\d)$')
STAMP = re.compile(r'(?P<prefix>"(?P<key>[^"\\]+)"\s*:\s*")(?P<value>\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d+)?(?:Z|[+-]\d\d:?\d\d))(?P<suffix>")')

def extract_json(head):
    """Find JSON values after structured diagnostic labels, preserving the text around them."""
    def unique_keys(pairs):
        result = {}
        for key, value in pairs:
            if key in result:
                raise ValueError('Duplicate JSON object key')
            result[key] = value
        return result
    # Duplicate keys make the structured representation lossy and unsafe.
    decoder = json.JSONDecoder(object_pairs_hook=unique_keys)
    found = {}
    for match in JSON_MARKER.finditer(head):
        start = match.end()
        try:
            value, consumed = decoder.raw_decode(head[start:])
        except (json.JSONDecodeError, ValueError):
            continue
        if not isinstance(value, (dict, list)):
            continue
        name = match['name']
        if name in found:  # Duplicate labels: do not guess which payload is meant.
            return {}
        found[name] = (start, start + consumed, value)
    return found

def shape(value):
    if isinstance(value, dict):
        return ('dict', tuple(sorted((k, shape(v)) for k, v in value.items())))
    if isinstance(value, list):
        return ('list', tuple(shape(v) for v in value))
    return type(value).__name__

def walk_strings(value, path=()):
    if isinstance(value, dict):
        for k, v in value.items():
            yield from walk_strings(v, path + (k,))
    elif isinstance(value, list):
        for i, v in enumerate(value):
            yield from walk_strings(v, path + (i,))
    elif isinstance(value, str):
        yield path, value

def at(value, path):
    for part in path:
        value = value[part]
    return value

def put(value, path, replacement):
    node = value
    for part in path[:-1]:
        node = node[part]
    node[path[-1]] = replacement

def approved_dynamic(values, current, char_family, matches_family):
    """True only for a QA-demonstrated variation of exactly the same shape."""
    if len(set(values)) < 2 or not isinstance(current, str):
        return False
    # A timestamp is approved only at this exact JSON label + full path,
    # never by its leaf property name alone.
    if all(isinstance(v, str) and STAMP_VALUE.fullmatch(v) and valid_stamp(v)
           for v in values):
        return bool(STAMP_VALUE.fullmatch(current) and valid_stamp(current))
    if all(UUID.fullmatch(v) for v in values):
        return bool(UUID.fullmatch(current))
    observed = [ID_TAIL.fullmatch(v) for v in values]
    incoming = ID_TAIL.fullmatch(current)
    if not incoming or any(match is None for match in observed):
        return False
    if len({match['anchor'] for match in observed}) != 1:
        return False
    if len({match['suffix'] for match in observed}) != 1:
        return False
    if (incoming['anchor'] != observed[0]['anchor']
            or incoming['suffix'] != observed[0]['suffix']):
        return False
    tails = [match['value'] for match in observed]
    if len(set(tails)) < 2:
        return False
    family = char_family(tails)
    return bool(family is not None and matches_family(
        incoming['value'], family, {len(s) for s in tails}))

def json_normalized_heads(historical, current, char_family, matches_family):
    """Normalize ONLY approved dynamic JSON field values at identical JSON paths.

    If JSON keys, array lengths, or value types change, leave the original
    messages untouched so the original matcher can flag the difference.
    """
    old_parts = [extract_json(head) for head in historical]
    new_parts = extract_json(current)
    if not old_parts or not all(old_parts) or not new_parts:
        return historical, current, []
    names = set(new_parts)
    if any(set(parts) != names for parts in old_parts):
        return historical, current, []
    copied_old = [{name: copy.deepcopy(parts[name][2]) for name in names} for parts in old_parts]
    copied_new = {name: copy.deepcopy(new_parts[name][2]) for name in names}
    accepted = []
    for name in sorted(names):
        old_roots = [parts[name][2] for parts in old_parts]
        new_root = new_parts[name][2]
        if len({repr(shape(root)) for root in [*old_roots, new_root]}) != 1:
            continue
        for path, current_value in walk_strings(new_root):
            values = [at(root, path) for root in old_roots]
            if not approved_dynamic(values, current_value, char_family, matches_family):
                continue
            # Same placeholder in approved + incoming, keeping all other values exact.
            placeholder = '__RAE_QA_APPROVED_DYNAMIC__'
            for root in copied_old:
                put(root[name], path, placeholder)
            put(copied_new[name], path, placeholder)
            accepted.append({'json': name, 'path': '/' + '/'.join(map(str, path)),
                             'approved': sorted(set(values)), 'incoming': current_value})
    if not accepted:
        return historical, current, []

    def rewrite(head, spans, objects):
        for name, (start, end, _value) in sorted(spans.items(), key=lambda p:p[1][0], reverse=True):
            # Canonical serialization only on a JSON value with exact same approved shape.
            # This also makes strings independent of incidental comma spacing/key order.
            original = spans[name][2]
            replacement = objects[name]
            if shape(original) != shape(replacement):
                raise AssertionError('JSON normalization changed structure')
            head = head[:start] + json.dumps(replacement, separators=(',', ':'), ensure_ascii=True,
                                             sort_keys=True) + head[end:]
        return head
    return ([rewrite(head, parts, copied) for head, parts, copied in zip(historical, old_parts, copied_old)],
            rewrite(current, new_parts, copied_new), accepted)

def valid_stamp(value):
    try:
        datetime.fromisoformat(value.replace('Z','+00:00'))
        return True
    except ValueError:
        return False

def normalize_stamps(history, current):
    """Fail-closed fallback for timestamp literals *outside* parsed JSON.

    Parsed JSON timestamps are handled by json_normalized_heads() at the exact
    diagnostic label + full path. Here we may generalize only if the entire
    surrounding diagnostic is byte-for-byte identical after masking timestamps.
    Every timestamp occurrence is tied to its ordinal position and context;
    timestamp values from expected and actual cannot train each other.
    """
    if not history:
        return history, current, []
    old_matches = [list(STAMP.finditer(head)) for head in history]
    new_matches = list(STAMP.finditer(current))
    if not new_matches or any(len(row) != len(new_matches) for row in old_matches):
        return history, current, []

    def skeleton(head):
        return STAMP.sub(lambda m: m['prefix'] + '<RAE_TIMESTAMP_SLOT>' + m['suffix'], head)

    if any(skeleton(head) != skeleton(current) for head in history):
        return history, current, []
    accepted = []
    for i, current_match in enumerate(new_matches):
        historic_matches = [row[i] for row in old_matches]
        if any(m['key'] != current_match['key'] for m in historic_matches):
            continue
        values = [m['value'] for m in historic_matches]
        if (len(set(values)) >= 2 and all(valid_stamp(v) for v in values)
                and valid_stamp(current_match['value'])):
            accepted.append((i, current_match['key'], values, current_match['value']))
    if not accepted:
        return history, current, []
    accepted_slots = {item[0] for item in accepted}

    def rewrite(head):
        spans = list(STAMP.finditer(head))
        for i in sorted(accepted_slots, reverse=True):
            match = spans[i]
            head = (head[:match.start('value')] + '__RAE_APPROVED_TIMESTAMP__'
                    + head[match.end('value'):])
        return head

    return [rewrite(head) for head in history], rewrite(current), accepted
