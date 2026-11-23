"""Strict pre-merge RAE validation, independent of Markdown generation."""
from __future__ import annotations
import re
import os
from urllib.parse import urlsplit
from pathlib import Path
import yaml

METADATA = ('release', 'scope', 'issue_jira', 'data_ora', 'durata', 'numero_ko', 'run', 'suite', 'environment', 'executor', 'source_branch')
FIELDS = ('id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')
NAME = re.compile(r'^rae_[A-Za-z0-9_.-]+_\d{14}(?:_\d+)?\.yaml$')
MATCH_STATUSES = {'pending_confirmation', 'confirmed', 'auto_matched'}
MATCH_FIELDS = {'status', 'source_rae', 'source_id_test', 'proposed_digest', 'review_evidence'}  # RAE_REVIEW_PANEL_V1


_TICKET = re.compile(r'[A-Za-z][A-Za-z0-9]*-\d+\Z')
_EVIDENCE_CODE = re.compile(r'[A-Z][A-Z0-9_]{0,79}\Z')
_EVIDENCE_FIELDS = {'code', 'historical_value', 'current_value',
                    'historical_context', 'current_context', 'location'}


def _valid_jira_url(value: str) -> bool:
    """Validate Jira-style URL syntax without network requests or tenant lock-in.

    Allows canonical Atlassian /browse/KEY-123 and legacy Jira server /KEY-123
    links used in historical tests. Does not imply a ticket actually exists.
    """
    if not isinstance(value, str) or not value or value != value.strip() or any(c.isspace() for c in value):
        return False
    try:
        url = urlsplit(value)
        if url.scheme != 'https' or not url.hostname or url.username or url.password or url.query or url.fragment:
            return False
        if url.port is not None and url.port != 443:
            return False
        if not re.fullmatch(r'[A-Za-z0-9.-]+', url.hostname):
            return False
        allowed = {h.strip().casefold() for h in os.environ.get('RAE_JIRA_HOSTS', '').split(',') if h.strip()}
        if allowed and url.hostname.casefold() not in allowed:
            return False
        suffix = url.path.removeprefix('/browse/').removeprefix('/')
        return bool(_TICKET.fullmatch(suffix)) and (url.path.startswith('/browse/') or url.path == '/' + suffix)
    except ValueError:
        return False


def _validate_evidences(matching: dict, prefix: str, errors: list[str]) -> None:
    if 'review_evidence' not in matching:
        return
    items = matching['review_evidence']
    if matching.get('status') != 'pending_confirmation':
        errors.append(f'{prefix}.review_evidence: consentito solo per pending_confirmation')
    if not isinstance(items, list) or not (1 <= len(items) <= 40):
        errors.append(f'{prefix}.review_evidence: lista di 1-40 evidenze obbligatoria')
        return
    seen = set()
    for idx, item in enumerate(items, 1):
        place = f'{prefix}.review_evidence[{idx}]'
        if not isinstance(item, dict) or not item or set(item) - _EVIDENCE_FIELDS:
            errors.append(f'{place}: struttura o campi non validi')
            continue
        if not isinstance(item.get('code'), str) or not _EVIDENCE_CODE.fullmatch(item['code']):
            errors.append(f'{place}.code: codice non valido')
        for k, v in item.items():
            if k == 'code':
                continue
            if not isinstance(v, str) or not v.strip() or len(v) > 1200 or '\x00' in v:
                errors.append(f'{place}.{k}: valore non valido')
        identity = repr(sorted(item.items(), key=lambda x: str(x[0])))
        if identity in seen:
            errors.append(f'{place}: evidenza duplicata')
        seen.add(identity)


def validate_original_evidences(data: dict, original: dict) -> None:
    """QA may edit its analysis, never the machine-collected review evidence."""
    def index(document):
        return {(section, str(entry.get('id_test')), str(entry.get('error_log'))):
                entry.get('matching', {}).get('review_evidence')
                for section in ('ko', 'ko_know')
                for entry in document.get(section, [])
                if isinstance(entry, dict) and isinstance(entry.get('matching'), dict)}
    baseline = index(original)
    current = index(data)
    for identity, evidence in baseline.items():
        if current.get(identity) != evidence:
            raise ValidationError('review_evidence originale alterato: ' + identity[1])
    for identity, evidence in current.items():
        if evidence is not None and identity not in baseline:
            raise ValidationError('review_evidence aggiunto o spostato senza origine verificabile: ' + identity[1])


class ValidationError(ValueError):
    pass


def validate(data: object, filename: str | None = None, *, draft: bool = False) -> dict:
    errors = []
    if not isinstance(data, dict):
        raise ValidationError('RAE deve essere una mappa YAML')
    if set(data) != {'rae', 'ko', 'ko_know'}:
        errors.append('La root deve contenere esattamente rae, ko, ko_know')
    meta = data.get('rae')
    if not isinstance(meta, dict):
        errors.append('rae deve essere una mappa')
        meta = {}
    for field in METADATA:
        if field not in meta:
            if field != 'scope':
                errors.append(f'rae.{field}: mancante')
        elif field == 'release' and not isinstance(meta[field], str):
            errors.append('rae.release: deve essere una stringa, anche vuota')
        elif field not in ('numero_ko', 'release', 'scope') and (not isinstance(meta[field], str) or (not meta[field].strip() and not (draft and field == 'issue_jira'))):
            errors.append(f'rae.{field}: valore obbligatorio non valido')
    if set(meta) not in (set(METADATA), set(METADATA) - {'scope'}):
        errors.append('rae: campi inattesi o mancanti')
    issue = meta.get('issue_jira')
    if isinstance(issue, str) and issue.strip() and not _valid_jira_url(issue):
        errors.append('rae.issue_jira: link Jira non valido')
    if meta.get('scope', 'NIGHTLY') not in ('NO-PROMO', 'NIGHTLY', 'PROMO'):
        errors.append('rae.scope: deve essere NO-PROMO, NIGHTLY o PROMO')
    if meta.get('environment') not in ('qa', 'dev'):
        errors.append('rae.environment: deve essere dev o qa')
    if not re.fullmatch(r'\d{2}:\d{2}:\d{2}', str(meta.get('durata', ''))):
        errors.append('rae.durata: formato HH:MM:SS non valido')
    for section in ('ko', 'ko_know'):
        records = data.get(section)
        if not isinstance(records, list):
            errors.append(f'{section}: deve essere una lista')
            continue
        for n, entry in enumerate(records, 1):
            prefix = f'{section}[{n}]'
            if not isinstance(entry, dict):
                errors.append(f'{prefix}: deve essere una mappa')
                continue
            if not set(FIELDS).issubset(entry) or set(entry) - (set(FIELDS) | {'matching'}):
                errors.append(f'{prefix}: campi obbligatori mancanti o inattesi')
            for field in FIELDS:
                value = entry.get(field)
                if not isinstance(value, str) or (field in FIELDS[:2] and not value.strip()) or (not draft and field in ('procedure', 'result') and not value.strip()):
                    errors.append(f'{prefix}.{field}: valore non valido o vuoto')
            for link_field in ('link_prod_bug', 'link_suite_bug'):
                url = entry.get(link_field)
                if isinstance(url, str) and url.strip() and not _valid_jira_url(url):
                    errors.append(f'{prefix}.{link_field}: link Jira non valido; atteso URL https con ticket KEY-123')
            matching = entry.get('matching')
            if matching is not None:
                if not isinstance(matching, dict):
                    errors.append(f'{prefix}.matching: deve essere una mappa')
                else:
                    if (matching.get('status') not in MATCH_STATUSES
                            or set(matching) - MATCH_FIELDS):
                        errors.append(f'{prefix}.matching: stato o campi non validi')
                    if (not isinstance(matching.get('source_rae'), str)
                            or not NAME.fullmatch(matching['source_rae'])):
                        errors.append(f'{prefix}.matching.source_rae: nome non valido')
                    if (not isinstance(matching.get('source_id_test'), str)
                            or not matching['source_id_test'].strip()):
                        errors.append(f'{prefix}.matching.source_id_test: mancante')
                    digest = matching.get('proposed_digest')
                    if digest is not None and (not isinstance(digest, str)
                                              or not re.fullmatch(r'[0-9a-f]{64}', digest)):
                        errors.append(f'{prefix}.matching.proposed_digest: SHA-256 non valido')
                    if matching.get('status') == 'pending_confirmation' and digest is None:
                        errors.append(f'{prefix}.matching.proposed_digest: obbligatorio per pending')
                    if section == 'ko_know' and matching.get('status') == 'pending_confirmation':
                        errors.append(f'{prefix}: pending_confirmation deve restare in ko')
                    _validate_evidences(matching, prefix + '.matching', errors)
    records = (data.get('ko') if isinstance(data.get('ko'), list) else []) + (data.get('ko_know') if isinstance(data.get('ko_know'), list) else [])
    if type(meta.get('numero_ko')) is not int or meta.get('numero_ko') != len(records):
        errors.append('rae.numero_ko: non coincide con la somma dei KO')
    seen = set()
    for record in records:
        if isinstance(record, dict):
            identity = (str(record.get('id_test', '')).strip().casefold(), str(record.get('error_log', '')).strip())
            if identity in seen:
                errors.append(f'KO duplicato: {identity[0]}')
            seen.add(identity)
    if filename and not NAME.fullmatch(Path(filename).name):
        errors.append(f'Filename non conforme: {filename}')
    if errors:
        raise ValidationError('\n'.join(errors))
    return data


def read_yaml(path: Path) -> dict:
    try:
        return validate(yaml.safe_load(path.read_text(encoding='utf-8')), path.name)
    except yaml.YAMLError as exc:
        raise ValidationError(f'YAML non valido: {path}: {exc}') from exc
