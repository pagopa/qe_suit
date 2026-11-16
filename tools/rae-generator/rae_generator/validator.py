"""Strict pre-merge RAE validation, independent of Markdown generation."""
from __future__ import annotations
import re
from pathlib import Path
import yaml

METADATA = ('release', 'issue_jira', 'data_ora', 'durata', 'numero_ko', 'run', 'suite', 'environment', 'executor', 'source_branch')
FIELDS = ('id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')
NAME = re.compile(r'^rae_[A-Za-z0-9_.-]+_\d{14}(?:_\d+)?\.yaml$')


class ValidationError(ValueError):
    pass


def validate(data: object, filename: str | None = None) -> dict:
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
            errors.append(f'rae.{field}: mancante')
        elif field != 'numero_ko' and (not isinstance(meta[field], str) or not meta[field].strip()):
            errors.append(f'rae.{field}: valore obbligatorio non valido')
    if set(meta) != set(METADATA):
        errors.append('rae: campi inattesi o mancanti')
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
            if set(entry) != set(FIELDS):
                errors.append(f'{prefix}: campi obbligatori mancanti o inattesi')
            for field in FIELDS:
                value = entry.get(field)
                if not isinstance(value, str) or (field in FIELDS[:4] and not value.strip()):
                    errors.append(f'{prefix}.{field}: valore non valido o vuoto')
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
