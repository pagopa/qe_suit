# Workflow AI su qe_suit

Questo documento descrive il percorso automatizzato che collega una AI Coding
Story Jira a una GitHub Issue, a un agente di sviluppo e a una Pull Request.
Non sostituisce il flusso manuale del repository.

## Avvio

La GitHub Issue deve avere l'etichetta `ready-for-ai` e contenere i requisiti
della card. Il workflow `Copilot implement issue` legge anche il campo:

```text
- **Base branch:** <branch>
```

Se il campo manca, usa `develop`. Sono ammesse `main`, `develop`,
`feature/*`, `feat/*`, `release/*` e `hotfix/*`, purché il branch esista.

## Esecuzione

Il workflow:

1. legge la Issue e le istruzioni tecniche del repository;
2. verifica modello e base branch;
3. crea `feature/ai-issue-<issue>-<run>` dal commit della base selezionata;
4. chiede all'agente una modifica limitata ai sorgenti, test e risorse dei moduli autorizzati;
5. verifica i file modificati e il numero di righe;
6. esegue i controlli Maven previsti;
7. crea una Draft PR verso la stessa base.

Le correzioni richieste dalla review vengono applicate dal workflow
`Copilot rework PR` sullo stesso feature branch e sulla stessa PR.

## Controlli

Il percorso esegue nell'ordine:

1. `frontend-e2e-framework`: `bash ./mvnw -B install`;
2. `common`: `bash ./mvnw -B install`;
3. `pst-maven-plugin`: `bash ./mvnw -B install`;
4. `interop`: `bash ./mvnw -B -Pci-rules test`;
5. `send`: `bash ./mvnw -B -DskipTests package`.

`send` viene compilato e pacchettizzato, ma le suite E2E non vengono eseguite.
Il profilo `ci-rules` di Interop non equivale a una copertura E2E completa.

## Confini

Il percorso AI non esegue merge automatici e non sostituisce la review umana.
Gli sviluppatori possono continuare a lavorare manualmente sulle card, aprire
PR secondo le convenzioni del team e usare la Copilot review su qualsiasi PR
ammessa dal flusso canonico.
