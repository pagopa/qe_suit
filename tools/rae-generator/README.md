# RAE Generator (GitHub-native)

Il modulo Python crea il file YAML della RAE dopo l'esecuzione NRT, recupera le analisi precedentemente approvate da `develop`, valida lo YAML completato dal QA e genera il Markdown in modo deterministico.

## Setup locale

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r tools/rae-generator/requirements-dev.txt
PYTHONPATH=tools/rae-generator pytest -q tools/rae-generator/tests
```

## Comandi

Generazione iniziale (YAML incompleto ammesso; non produce Markdown):

```bash
PYTHONPATH=tools/rae-generator python -m rae_generator generate \
  --reports /path/to/interop/target --history rae --namespace interop \
  --suite NrtStandardSuiteTest --environment qa --executor USER \
  --source-branch develop --run-url https://github.com/pagopa/qe_suit/actions/runs/123 \
  --metadata /path/to/interop/target/rae/run_metadata.json \
  --output rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml
```

Validazione e generazione Markdown (richiede che release, issue_jira, procedure e result siano compilati):

```bash
PYTHONPATH=tools/rae-generator python -m rae_generator finalize rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml --write
PYTHONPATH=tools/rae-generator python -m rae_generator check rae/interop/rae_NrtStandardSuiteTest_20261116190000.yaml
```

## GitHub Actions

- `[INTEROP] QA workflow` acquisisce la durata effettiva del passo Maven, carica Cucumber/JUnit XML e genera un nuovo branch `feature/rae_<suite>_<timestamp>` e una Draft PR, anche se i test falliscono ma producono report validi.
- `[RAE] Validate YAML and generate Markdown` valida lo YAML e committa un Markdown aggiornato sul feature branch. Gli altri PR non RAE producono un check verde senza eseguire la logica RAE.
- Configurare il repository secret **`RAE_AUTOMATION_TOKEN`** con un **fine-grained PAT** del repository con **Contents: Read and write** (o token equivalente di GitHub App) per consentire il push sul **feature branch** e innescare nuovamente i workflow dopo il commit automatico. Il token nativo `GITHUB_TOKEN` non innesca gli stessi workflow quando effettua il push; pertanto la secret è necessaria per l'automazione completa. Non occorre bypass di `develop`. Le PR da fork non vengono elaborate.
- Per la generazione di branch e Draft PR il workflow usa `RAE_AUTOMATION_TOKEN` se presente, altrimenti `GITHUB_TOKEN`. Il token dedicato necessita sia **Contents: Read and write** sia **Pull requests: Read and write**. Con `GITHUB_TOKEN` il repository deve consentire ad Actions la creazione di PR (Settings → Actions → General); la policy organizzativa potrebbe vietarlo.
- **Non** rendere `rae-validate` required prima di aver verificato i workflow con una PR di prova. L'amministratore potrà poi aggiungerlo alla ruleset di `develop` (insieme a `validate-rules`).
- Il `GITHUB_TOKEN` usato per aprire la Draft PR non scatena necessariamente `pull_request.opened`; la prima validazione utile è comunque dopo il push QA o la transizione Ready for review.
- La retention artefatti rimane a **10 giorni**, invariata rispetto alla pipeline esistente. Prima di aumentarla occorre verificare la policy dell'organizzazione; per 12 mesi potrebbe essere necessario storage esterno. Non impostare 365 giorni senza verifica.

## Note sui report

- Cucumber JSON: `interop/target/cucumber-reports/`.
- JUnit Open Test Reporting XML: `interop/target/junit-platform-reports/open-test-report.xml`.
- JUnit/Surefire XML tradizionale: `interop/target/surefire-reports/TEST-*.xml`.
- La presenza di report mancanti o corrotti blocca la generazione. Un report valido con zero fallimenti produce una RAE con liste vuote.
- Per la deduplicazione cross-format usiamo nome test e la prima riga del messaggio di errore; ID diversi non sono fusi automaticamente per evitare falsi positivi. Verificare con esempi reali Cucumber/JUnit per affinare alias fra nomi diversi.
- Lo storico è letto **solo** dal checkout di `develop`; i file YAML non ancora integrati non vengono considerati approvati.
- Il matching fuzzy richiede lo stesso ID test e la stessa firma diagnostica, con TF-IDF ngrammi 3–5 e cosine >= 0.90. Nessun ticket Jira è richiesto per considerare un KO noto.

## Sicurezza / limiti

Il workflow `pull_request` esegue un checkout in sola lettura e concede il PAT soltanto al passo finale di push. Si deve mantenere il repository protetto da PR non attendibili; non eseguire codice proveniente da fork con un token di scrittura. Il PATH originale e il nome del branch vengono verificati. Il workflow può incontrare nuovi push fra fetch e push: il git push non-fast-forward fallirà senza cancellare lavoro QA.

## Evolutiva report JSON dei contratti (QA-17448)

- L'HTML e `contract-test/report.json` sono renderizzati dallo stesso `ContractReport` Java.
- `schemaVersion: 1`, `summary`, `channels`, `groups` e tutti gli `scenarios` (PASS/FAIL/SKIPPED/ABORTED).
- Ogni scenario contiene canale/target nel suo gruppo e `id_test` nel formato `Classe::Factory::Scenario`.
- La RAE legge solo `cucumber-reports/report.json` e `contract-test/report.json`; non usa più XML JUnit.
- Se manca uno dei due JSON o i totali Java non sono coerenti, la generazione fallisce.
- JUnit XML può rimanere nell'artifact per debugging; non è un input RAE.

## Namespace delle RAE (pipeline multiple)

Ogni pipeline configura un namespace distinto: `interop` per Interop, `send` per
la futura pipeline SEND. `--history rae` indica la **radice**; la CLI legge
esclusivamente `rae/<namespace>/` e scrive esclusivamente lì. Non esiste
fallback implicito a `rae/` né lettura degli altri namespace.

Esempio: `--history rae --namespace send --output rae/send/rae_Suite_20261119000000.yaml`.
La pipeline Interop imposta `RAE_NAMESPACE: interop` in `generate_rae.env`.
Le nuove PR usano branch `rae/interop/rae_<suite>_<timestamp>`; il workflow
di validazione ricava da lì il percorso YAML/MD. Branch preesistenti senza
namespace restano gestiti per non rompere PR già aperte.

**Migrazione iniziale**: prima di generare nuove RAE Interop, eseguire lo
script `migrate_legacy.py --namespace interop` incluso nel pacchetto della
patch, verificare `git diff --cached --stat` e committare i `git mv` su un
branch dedicato. Solo lo storico migrato e approvato in develop verrà usato.
