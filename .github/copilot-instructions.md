# Istruzioni Copilot — qe_suit

Il repository contiene un framework di test Java 17 organizzato in moduli
Maven. Prima di proporre modifiche, leggi:

- `docs/architecture/repository-map.md`
- `docs/architecture/component-responsibilities.md`
- `docs/flows/test-implementation-flow.md`

## Regole tecniche comuni

- Mantieni le dipendenze coerenti: `common` e il framework non devono dipendere dai moduli prodotto.
- Non inserire segreti né cambiare endpoint o credenziali di ambiente nelle risorse.
- Non introdurre dipendenze o accessi a servizi esterni senza un requisito esplicito.
- Se mancano decisioni sul requisito o servono modifiche fuori perimetro, dichiaralo; non inventare il comportamento.
- Mantieni test deterministici e coerenti con i criteri di accettazione; non indebolire test esistenti.
- Preferisci modifiche piccole e verificabili.

## Moduli

- `common/`
- `interop/`
- `send/`
- `pst-maven-plugin/`
- `frontend-e2e-framework/`

Le regole operative del workflow AI, inclusi branch, base Jira, percorsi
modificabili e comandi di verifica, sono definite nei workflow GitHub Actions
che avviano quell'automazione. Il flusso di sviluppo manuale e la normale
Copilot review seguono le convenzioni del repository e del team.
