"""Italian QA-facing messages for existing production Reason codes.

Presentation only. Never influences ProgressiveMatcher classifications.
"""
from __future__ import annotations
from dataclasses import dataclass
from typing import Iterable

@dataclass(frozen=True)
class Explanation:
    code: str
    title: str
    explanation: str
    historical: str
    current: str
    qa_action: str
    detail: str = ''
    historical_context: str = ''
    current_context: str = ''
    location: str = ''

_RULES = {
    'INVARIANT_CHANGED': ('Invariante testuale modificata', 'Un valore rimasto stabile nelle conferme QA è cambiato nel log corrente.', 'Verifica il valore storico e quello corrente prima di confermare.'),
    'FULL_LOG_STRUCTURE_CHANGED': ('Struttura del log modificata', 'Righe o token del log completo non coincidono con le strutture QA approvate.', 'Confronta le righe inserite, eliminate o modificate.'),
    'UNAPPROVED_VARIANT': ('Nuova variante testuale', 'Il nuovo valore non è incluso tra le varianti confermate dal QA.', 'Valuta se questa nuova variante appartiene allo stesso difetto.'),
    'UNAPPROVED_STACK_VARIANT': ('Conferme insufficienti per la variante di stack', 'La struttura dello stack può essere già presente nello storico, ma le nuove differenze non sono automaticamente autorizzate.', 'Valuta le differenze puntuali e conferma se appartengono allo stesso difetto.'),

    'STACK_METHOD_CHANGED': ('Metodo dello stack non confermato', 'Un metodo della chiamata è diverso dai metodi già associati a questo difetto dal QA.', 'Verifica che il punto di fallimento e la causa siano gli stessi del ticket proposto.'),
    'STACK_SOURCE_CHANGED': ('File sorgente differente', 'Il file Java del frame non compare nelle occorrenze approvate.', 'Controlla se il codice interessato esegue lo stesso percorso diagnostico.'),
    'STACK_SHAPE_CHANGED': ('Struttura dello stack differente', 'Il numero di frame è diverso; il matcher non può allinearli con sicurezza.', 'Confronta i frame aggiunti o mancanti e la catena di chiamate.'),
    'LINE_NUMBER_UNSEEN': ('Numero di riga non ancora generalizzabile', 'Questa riga non è stata osservata e le conferme QA non provano ancora che il campo possa variare liberamente.', 'Verifica che il punto di esecuzione e la causa siano equivalenti.'),
    'PROXY_ORDINAL_UNSEEN': ('Nuovo identificativo di proxy Java', 'L’ordinale del proxy è cambiato, ma non abbiamo ancora conferme sufficienti per generalizzarlo.', 'Verifica che il metodo delegato e il comportamento sottostante siano identici.'),
    'MESSAGE_CHANGED': ('Messaggio diagnostico differente', 'La parte diagnostica dell’errore non coincide con le forme QA approvate.', 'Confronta eccezione, codici di risposta e dettagli funzionali prima di confermare.'),
    'UNAPPROVED_METHOD_COMBINATION': ('Combinazione di metodi mai approvata', 'Ogni metodo può comparire nello storico, ma questa specifica sequenza non è stata validata.', 'Controlla la sequenza completa: non approvare solo perché i metodi sono già noti singolarmente.'),
    'AMBIGUOUS_QA_GROUPS': ('Più difetti storici compatibili', 'Sono compatibili più gruppi QA; una scelta automatica del ticket sarebbe arbitraria.', 'Confronta le analisi candidate e assegna il difetto corretto, eventualmente uno nuovo.'),
    'NO_HISTORY': ('Nessuna firma QA disponibile', 'Non risultano conferme QA per questo test e questa suite.', 'Esegui un’analisi completa e registra la decisione nello storico.'),
    'NO_SIMILAR_QA_GROUP': ('Nessuna firma QA sufficientemente simile', 'Le firme confermate non raggiungono la soglia minima per una proposta affidabile.', 'Analizza il KO come potenzialmente nuovo senza riutilizzare automaticamente il ticket.'),
    'NO_APPROVAL': ('Conferme QA insufficienti', 'Il gruppo non possiede evidenze approvate sufficienti.', 'Completa la revisione e verifica il riferimento storico.'),
}

def explanations(reasons: Iterable) -> list[Explanation]:
    output = []
    for reason in reasons:
        code = str(getattr(reason, 'code', 'UNKNOWN'))
        title, meaning, action = _RULES.get(
            code, ('Differenza diagnostica da verificare',
                   'Il matcher ha bloccato la promozione automatica per una differenza.',
                   'Esamina il dettaglio storico e corrente prima della conferma.'))
        output.append(Explanation(
            code=code, title=title, explanation=meaning,
            historical=str(getattr(reason, 'historical', '') or ''),
            current=str(getattr(reason, 'current', '') or ''), qa_action=action,
            detail=str(getattr(reason, 'detail', '') or ''),
            historical_context=str(getattr(reason, 'historical_context', '') or ''),
            current_context=str(getattr(reason, 'current_context', '') or ''),
            location=str(getattr(reason, 'location', '') or '')))
    return output

def summary(status: str, *, has_candidate: bool, reasons: list[Explanation]) -> str:
    if status == 'known':
        return 'Firma QA verificata: il KO è riconosciuto automaticamente; non è richiesta una nuova conferma ordinaria.'
    if status == 'proposed':
        if reasons:
            return ('Analisi storica suggerita, ma la conferma automatica è bloccata dalle differenze sotto elencate.'
                    if has_candidate else
                    'Possibili firme storiche, ma nessun ticket può essere scelto in modo univoco.')
        return ('Analisi storica proposta dal confronto di produzione; manca una firma QA sufficiente per il match automatico.'
                if has_candidate else 'Confronto non conclusivo: eseguire una revisione QA.')
    return 'Nessuna analisi storica selezionata dal matcher: serve una nuova analisi QA (non significa necessariamente un difetto nuovo).'
