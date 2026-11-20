# RAE QA Matcher Simulator

Simulatore locale Streamlit del **matcher di produzione**. Non contiene una copia
indipendente dell'algoritmo: importa `rae_generator.progressive.ProgressiveMatcher`
dal checkout del repository. Il simulatore serve a verificare `KNOWN`, `PROPOSED`
e `NEW`, le differenze strutturali e le protezioni T7/T8.

## Prerequisiti

Dalla **root** del repository `qe_suit`, con Python e `.venv` già configurati:

```bash
source .venv/bin/activate
python -m pip install -r tools/rae/rae_generator/requirements-dev.txt
python -m pip install -r tools/rae/rae_generator_simulator/requirements.txt
```

## Avvio

```bash
cd ~/qe_suit
source .venv/bin/activate
export PYTHONPATH="$PWD/tools/rae/rae_generator:$PWD/tools/rae/rae_generator_simulator${PYTHONPATH:+:$PYTHONPATH}"
python -m streamlit run tools/rae/rae_generator_simulator/app.py
```

Apri **http://localhost:8501**. Interrompi con `Ctrl+C`.

> Prima della migrazione potevi avviarlo da `~/Downloads/rae_debug_simulator/app.py`.
> La posizione ufficiale nel repository diventa `tools/rae/rae_generator_simulator/app.py`.
> Non serve più il vecchio percorso in `PYTHONPATH`.

## Modalità 1: scenario QA manuale

Inserisci uno o più eventi con **gruppo difetto**, **ticket** e **log storico**.
Gli eventi dello stesso gruppo simulano una sequenza di conferme QA; gruppi
diversi rappresentano identità di difetto concorrenti. Inserisci il nuovo log,
quindi premi **Classifica con il ProgressiveMatcher di produzione**.

Le conferme inserite sono **simulate in memoria**: non attestano una vera
revisione GitHub, non scrivono nello storico, non attivano apprendimento in
`develop` e non autorizzano l'automazione fuori dalla sessione.

Puoi esportare/importare scenari JSON per riprodurre casi di regressione.

## Modalità 2: caricamento RAE YAML

Carica le RAE YAML che vuoi trattare come storico approvato in questo test.
Il simulatore usa il loader storico del progetto, ma **non verifica**
che i file provengano realmente da `develop`. Per una verifica produttiva
vanno utilizzate soltanto le RAE già approvate e integrate.

## Interpretare la risposta

- `KNOWN`: riconoscimento autorizzato dalle regole T7/T8 e dallo storico.
- `PROPOSED`: esiste una possibile corrispondenza che richiede verifica QA.
- `NEW`: non è stata individuata una corrispondenza storica utile.

Le motivazioni dettagliate della GUI sono diagnostiche e **non modificano**
la decisione. Nel Markdown di produzione l'avviso è invece condensato in
un singolo riquadro prima di `RESULT` e omette le motivazioni generiche quando
sono già presenti differenze concrete. È possibile che la GUI conservi
più dettagli tecnici del Markdown: è voluto.

## Test

```bash
cd ~/qe_suit
source .venv/bin/activate
export PYTHONPATH="$PWD/tools/rae/rae_generator:$PWD/tools/rae/rae_generator_simulator${PYTHONPATH:+:$PYTHONPATH}"
python -m pytest -q tools/rae/rae_generator_simulator/tests
python -m pytest -q tools/rae/rae_generator/tests
```

## Limiti e sicurezza

- Non esegue la pipeline, non genera PR e non riscrive i file RAE.
- Le conferme manuali non equivalgono a review QA su GitHub.
- La modalità upload assume che i file siano affidabili.
- Il simulatore usa il matcher importato **dal checkout attuale**; testare
  sul branch corretto e con le dipendenze del progetto installate.
- I dati inseriti sono locali alla sessione Streamlit, salvo esportazione JSON.
