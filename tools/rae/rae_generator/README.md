# RAE Generator — guida operativa e integrazione GitHub Actions

Il **Report Analisi Esiti (RAE)** raccoglie i KO di una esecuzione NRT, propone analisi storiche quando disponibili, permette la revisione QA e produce un Markdown deterministico a partire dallo YAML. Il generatore è condiviso tra pipeline; **namespace, suite, report e workflow sono specifici della pipeline**.

## 1. Requisiti e installazione

- Python 3.12 per i job GitHub Actions esistenti; per lo sviluppo locale usare un ambiente compatibile con le dipendenze del repository.
- Accesso al repository e ai report prodotti dalla pipeline.
- Permessi GitHub Actions per leggere e creare branch e PR.
- Dipendenze Python del generatore.

Dalla root di `qe_suit`:

```bash
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r tools/rae/rae_generator/requirements-dev.txt
export PYTHONPATH="$PWD/tools/rae/rae_generator${PYTHONPATH:+:$PYTHONPATH}"
python -m pytest -q tools/rae/rae_generator/tests
python -m rae_generator --help
```

## 2. Architettura in breve

1. La suite può produrre report Cucumber JSON in `target/cucumber-reports/`, report contratti `target/contract-test/report.json`, oppure entrambi.
2. Il workflow carica gli artifact e genera un file `run_metadata.json` con data/ora di avvio e durata effettiva del comando Maven.
3. Il job RAE scarica gli artifact, legge lo storico **dal checkout di `develop`** e invoca `rae_generator generate`.
4. Il job crea `rae/<namespace>/rae_<suite>_<timestamp>.yaml` su un branch `rae/<namespace>/rae_<suite>_<timestamp>` e apre una Draft PR verso `develop`.
5. Il QA completa/conferma il contenuto dello YAML e lo committa sul branch RAE.
6. Il workflow `rae-validation.yaml` verifica lo YAML e produce/aggiorna il `.md` accanto allo YAML.
7. Soltanto dopo il merge su `develop` la RAE diventa parte dello storico disponibile alle esecuzioni successive.

**Responsabilità distinte:** lo YAML contiene le analisi modificabili dal QA; il Markdown è un derivato. Non aggiornare manualmente il Markdown senza rigenerarlo.

## 3. Attivare la RAE per una nuova pipeline o progetto

La pipeline Interop è un esempio reale: [`.github/workflows/interop-pipeline.yaml`](../../.github/workflows/interop-pipeline.yaml). La validazione comune è [`.github/workflows/rae-validation.yaml`](../../.github/workflows/rae-validation.yaml). Per una pipeline nuova, adeguare i seguenti elementi nel *suo* workflow.

### 3.1 Scegliere namespace, suite e input

- Assegnare un namespace distinto, per esempio `send`, diverso da `interop`.
- Stabilire i valori di `--suite`, `--environment` (`qa`/`dev`), `--executor` e `--source-branch`.
- La pipeline può produrre **solo Cucumber**, **solo Contract** oppure **entrambi**. Il generatore elabora tutti i JSON supportati presenti; fallisce se mancano entrambi o se uno dei file presenti è malformato. Non è richiesto `--cucumber-only`.
- Il report contratti deve essere conforme allo schema `schemaVersion: 1` del `ContractReport` Java. Il formato JUnit XML non è più un input della RAE, anche se può restare fra gli artifact diagnostici.
- Ogni ID test deve essere non ambiguo; il parser rifiuta duplicati fra le sorgenti.

### 3.2 Aggiungere i passaggi al workflow NRT

Prendere a riferimento i job `run_tests` e `generate_rae` di `interop-pipeline.yaml`. Il workflow di una nuova pipeline deve:

1. Eseguire test e salvare i report nella cartella `target` del progetto.
2. Registrare `data_ora` e `durata` (formato `HH:MM:SS`) **della reale esecuzione**, anche in caso di test KO. Interop utilizza `trap ... EXIT` per scrivere `target/rae/run_metadata.json`.
3. Usare `actions/upload-artifact` con `if: always()` per pubblicare i report disponibili e il metadata; la pipeline Interop usa il nome artifact `qa-test-artifacts` e retention di **10 giorni**.
4. Aggiungere un job generatore con `needs: run_tests` e condizione che consenta i fallimenti dei test ma non l’assenza di **tutti** i report supportati; usare Python e `tools/rae/rae_generator/requirements.txt`.
5. Scaricare gli artifact, effettuare il checkout **di `develop` per lo storico e la creazione del branch**, chiamare `rae_generator generate` con `--namespace <namespace>` e scrivere lo YAML dentro `rae/<namespace>/`.
6. Creare il branch `rae/<namespace>/rae_<suite>_<YYYYMMDDHHMMSS>`, effettuare commit/push e aprire una Draft PR verso `develop` con `gh pr create --draft`.
7. Assicurarsi che le label richieste esistano: Interop usa `rae` e `interop`; una pipeline nuova può richiedere la label del proprio namespace.

Il job condiviso di validazione riconosce i branch `rae/<namespace>/rae_*` e deriva **dal nome del branch** il percorso YAML. Non accetta PR da fork e non esegue codice Python preso dal branch dati della PR.

### 3.3 Configurare token e permessi GitHub

Nel repository: **Settings → Secrets and variables → Actions → New repository secret**. Configurare `RAE_AUTOMATION_TOKEN` con un **fine-grained PAT** sul repository interessato, oppure una credenziale equivalente approvata dall'organizzazione.

Permessi richiesti dal flusso di riferimento:

- **Contents: Read and write** per i commit/push sul branch RAE.
- **Pull requests: Read and write** per l'apertura della Draft PR.

Nel workflow, i job che creano PR richiedono `permissions: contents: write, pull-requests: write` (oltre alla policy del repository). Il workflow Interop usa `secrets.RAE_AUTOMATION_TOKEN || github.token` per creare la PR; il job di validazione richiede invece `RAE_AUTOMATION_TOKEN` quando deve effettuare un push del Markdown.

**Attenzione:** i push effettuati usando `GITHUB_TOKEN` non attivano normalmente nuovi workflow `push`/`pull_request`; per l'automazione completa serve il token dedicato. Se si usa il fallback con `GITHUB_TOKEN`, il repository deve consentire la creazione di PR da Actions e l'organizzazione può limitarla. Non inserire token nei file YAML/MD o negli artifact. Non concedere il token a codice non attendibile.

### 3.4 Checklist di collaudo

- Esecuzione NRT con report validi e 0 KO → YAML con `ko` e `ko_know` vuote.
- Esecuzione NRT con KO → artifact completo e Draft PR automatica nel namespace corretto.
- Modifica QA allo YAML → validazione e Markdown deterministico aggiornato.
- Merge su `develop` → storico consultabile nella successiva RAE della stessa pipeline.
- Namespace differente → nessuna contaminazione incrociata dello storico.
- Nessun report oppure report presente ma non valido → errore esplicito. L’assenza di un solo formato è consentita.

Non rendere il check `rae-validate` obbligatorio nella ruleset di `develop` prima di aver verificato una PR completa di prova.

## 4. Comandi CLI: generare, finalizzare, verificare

La CLI attuale espone `generate`, `finalize` e `check`. **Non esiste un comando `regenerate`**: rigenerare uno YAML significa rieseguire `generate` con gli stessi input.

### 4.1 Generare uno YAML da report e metadata

Il percorso passato a `--reports` deve contenere almeno uno tra `cucumber-reports/*.json` e `contract-test/report.json`; può contenere entrambi. Non deve necessariamente coincidere con `interop/target` se si usano artifact scaricati.

```bash
cd ~/qe_suit
source .venv/bin/activate
export PYTHONPATH="$PWD/tools/rae/rae_generator${PYTHONPATH:+:$PYTHONPATH}"

python -m rae_generator generate \
  --reports /percorso/report-estratti \
  --history rae --namespace interop \
  --suite NrtStandardSuiteTest --environment qa \
  --executor USER --source-branch develop \
  --run-url 'https://github.com/pagopa/qe_suit/actions/runs/RUN_ID' \
  --metadata /percorso/run_metadata.json \
  --output rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml
```

Il file `--metadata` è un JSON **obbligatorio** con questi due campi:

```json
{"data_ora":"2026-11-16T18:00:00Z","durata":"00:01:10"}
```

`data_ora` identifica l'inizio dell'esecuzione; `durata` è la durata reale, non il tempo trascorso dalla creazione della PR. Non inventare o sostituire l'ora storica con quella attuale senza esplicitarlo.

### 4.2 Sono già sul branch RAE: dove recupero `run_metadata.json`?

**È normale che non sia nel branch.** Interop lo crea in `interop/target/rae/run_metadata.json` durante il job Maven e lo carica nell'artifact `qa-test-artifacts`, non nel commit dello YAML. Per recuperare gli input esatti:

```bash
# Richiede GitHub CLI autenticata: gh auth login
# Recupera il run ID dal campo rae.run dello YAML o dall'URL nella Draft PR.
RUN_ID='INSERISCI_RUN_ID'
mkdir -p /tmp/rae-recovery

gh run download "$RUN_ID" -R pagopa/qe_suit \
  -n qa-test-artifacts -D /tmp/rae-recovery

find /tmp/rae-recovery -type f \( \
  -name run_metadata.json -o -name report.json \)
```

La cartella scaricata è normalmente direttamente `/tmp/rae-recovery/` con `cucumber-reports/`, `contract-test/report.json` e `rae/run_metadata.json`, ma controllare con `find` perché la struttura dell'artifact può cambiare. I percorsi devono essere coerenti con il root passato a `--reports`.

**Nota:** la retention dichiarata nel workflow Interop è di 10 giorni. Oltre tale periodo l'artifact potrebbe non essere più disponibile. Non è possibile ricostruire i report completi dal solo YAML.

Se l'artifact è scaduto, puoi **recuperare i valori metadata già salvati nello YAML** senza spacciarli per una nuova misurazione:

```bash
YAML='rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml'
export YAML
python - <<'PY'
import os
from pathlib import Path
import yaml, json
path = Path(os.environ['YAML'])
rae = yaml.safe_load(path.read_text(encoding='utf-8'))['rae']
out = Path('/tmp/rae-restored-metadata.json')
out.write_text(json.dumps({'data_ora': rae['data_ora'], 'durata': rae['durata']}) + '\n', encoding='utf-8')
print(out)
PY
```

Questo ripristina **solo** il metadata salvato nella RAE precedente, non i report: per rigenerare davvero lo YAML occorrono anche i JSON dell'esecuzione originale. Se i report non sono più disponibili, non eseguire `generate` con report di un'altra run.

### 4.3 Rigenerare lo YAML senza perdere le analisi QA

`generate` **riscrive il file di destinazione** e riparte dagli input e dallo storico: non conserva automaticamente le modifiche QA fatte nello YAML esistente.

Sul branch RAE:

```bash
YAML='rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml'
mkdir -p /tmp/rae-backup
cp "$YAML" "/tmp/rae-backup/$(basename "$YAML")"

# Prima verificare git status e il checkout dello storico approvato:
git status --short
```

Per una riproduzione fedele, usare i report e metadata della stessa run e lo storico che era disponibile su `develop` al momento dell'esecuzione; il generatore locale legge semplicemente i file presenti in `rae/<namespace>/` nel checkout passato a `--history`. Se eseguito sul branch RAE che include una proposta nuova, potrebbe leggere anche file non ancora approvati: **per un confronto corretto usare un checkout/worktree separato di `develop`** oppure isolare lo storico approvato.

Eseguire `generate` con `--output` su un **nuovo file temporaneo nello stesso namespace** e nome valido, confrontare il risultato con `git diff --no-index` e solo dopo scegliere se sostituire lo YAML del branch. Non sovrascrivere la revisione QA per errore.

### 4.4 Rigenerare solo Markdown a partire dallo YAML esistente

Questo è il caso più comune quando sei già sul branch RAE: **non richiede `run_metadata.json` né report JSON**.

```bash
YAML='rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml'
python -m rae_generator finalize "$YAML" --write
python -m rae_generator check "$YAML"
```

`finalize` richiede uno YAML valido e completo (`release`, `issue_jira`, `procedure`, `result` secondo lo schema). Puoi visualizzare il Markdown senza scriverlo omettendo `--write`.

In un workflow di revisione QA verificata, `finalize`/`check` possono ricevere `--qa-reviewed --original-yaml <file-originale>`: questi flag **non vanno impostati manualmente per simulare una conferma**. Il workflow identifica l'originale dalla cronologia Git e verifica che esista una modifica umana successiva alla generazione.

## 5. Stato attuale del Markdown e messaggi QA

Il renderer `rae_generator/renderer.py` separa i KO in bug di prodotto noti, bug di suite noti e KO da analizzare. Ogni sezione è collassabile. Per una proposta con `matching.status: pending_confirmation` mostra **«MATCH PROPOSTO — CONFERMA QA RICHIESTA»**, la RAE di provenienza, `RESULT`, `PROCEDURE`, link e `ERROR LOG`.

**Limite attuale:** il renderer non presenta le motivazioni strutturate del matcher T7/T8. Non esiste ancora un «pannello warning unico» con tutte le invarianti violate; il generatore YAML non persiste i motivi e il validator non contempla il relativo campo. Il simulatore è più dettagliato del Markdown di produzione.

**Evolutiva prevista, non ancora rilasciata:** un solo riquadro informativo per ogni KO `proposed`, con sintesi visibile e dettagli espandibili, che unifichi tutti i motivi, senza ripetere per ciascuno un avviso completo. L'integrazione richiede modifiche coordinate a `generator.py`, `validator.py`, `renderer.py` e ai test; i motivi devono essere salvati nello YAML senza alterare il digest dei campi QA o il processo di approvazione.

## 6. Regole di matching e approvazione

Leggere [RAE_MATCHING_BUSINESS.md](RAE_MATCHING_BUSINESS.md) per il processo e gli esempi. In sintesi:

- `known`: un gruppo QA univoco e sufficientemente confermato autorizza il riconoscimento automatico; il link al ticket è necessario per la sezione dei bug noti nel report.
- `proposed`: esiste una candidata analisi storica, ma occorre conferma QA per la variazione o l'ambiguità.
- `new`: nessun candidato storico plausibile.
- Drain3 individua strutture ricorrenti, ma **cluster Drain3 ≠ riconoscimento approvato**.
- Le evolutive T7/T8 controllano le identità QA concorrenti e imparano varianti strutturali dalle conferme, non dalle classificazioni automatiche.
- Il controllo fuzzy TF-IDF (ngrammi 3–5, soglia > 0.90 nel test storico) propone candidati ma non autorizza da solo `known`.
- Lo storico è isolato per namespace. Il loader attuale consulta fino a 7 RAE recenti per suite, con eventuale inclusione della suite generale `NrtStandardSuiteTest`.

**Nota tecnica importante:** in `qa_confirmation.py` una proposta `pending_confirmation` è considerata confermata nello storico approvato solo se il suo digest continua a coincidere con i campi QA; in PR servono invece la provenienza iniziale verificata e una revisione QA attestata dal workflow. I record `auto_matched` non sono nuove evidenze di addestramento.

## 7. Diagnostica e problemi comuni

| Sintomo | Controllo |
| --- | --- |
| `Missing Maven execution metadata` | Verificare `run_metadata.json` nell'artifact e il job Maven. |
| Nessun report / report non valido | Occorre almeno un formato tra Cucumber JSON e Contract JSON; ogni report presente deve essere valido. |
| `Output RAE fuori dal namespace` | `--output` deve risiedere esattamente in `rae/<namespace>/`. |
| RAE non usa storico atteso | Controllare checkout `develop`, namespace, suite e presenza YAML approvati. |
| Workflow non aggiorna `.md` | Verificare `RAE_AUTOMATION_TOKEN`, permessi e log di `rae-validation.yaml`. |
| `Markdown assente o non sincronizzato` | Eseguire `finalize YAML --write` e poi `check YAML`. |
| I motivi T8 appaiono nel simulatore ma non nel Markdown | Comportamento attuale: manca ancora il passaggio motivazioni → YAML → renderer. |

## 8. Sicurezza e limiti

- Non usare PR da fork per l'automazione con credenziali di scrittura.
- I workflow validano i nomi dei branch e i percorsi namespace. Non usare symlink come directory di namespace.
- Il validatore RAE carica il codice fidato dal commit base di `develop`, non dalla PR dati.
- Il push del renderer rifiuta di sovrascrivere modifiche QA concorrenti; un non-fast-forward non deve essere forzato.
- Retention artifact attuale: 10 giorni. Per conservazione a 12 mesi verificare la policy e valutare storage esterno.
- Prima di attivare una nuova pipeline: testare sia l'errore di report mancante sia il flusso completo dalla NRT alla PR e al merge.


### Input supportati: nessun vincolo tra Cucumber e Contract

Il generatore accetta **solo Cucumber**, **solo Contract** o **entrambi**.
Cerca tutti i JSON `cucumber-reports/*.json` e l'eventuale `contract-test/report.json`
nel percorso `--reports`. Tutti i file trovati vengono validati; un JSON invalido
blocca la generazione anche quando l'altro formato è valido. Se nessuno dei due
formati è presente, la generazione fallisce esplicitamente. **Zero KO è valido**
solo quando è stato effettivamente letto almeno un report valido. I report JUnit
XML e HTML non sostituiscono questi input. `--cucumber-only`, nelle versioni
che ancora lo espongono, è deprecato e non filtra i report Contract.
