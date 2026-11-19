# Come la RAE riconosce i KO — spiegazione business

## Obiettivo

La RAE aiuta il QA a distinguere **problemi già analizzati**, **somiglianze che richiedono una conferma** e **problemi nuovi**. Il fine non è massimizzare il numero di KO riconosciuti, ma **ridurre il lavoro ripetitivo senza assegnare automaticamente un ticket sbagliato**.

Un ticket Jira e il nome di un test **non sono da soli la prova** che due errori hanno la stessa causa. L'equivalenza viene costruita progressivamente a partire dalle analisi approvate dal QA.

## Le tre decisioni

| Esito | Significato operativo | Cosa fa il QA |
| --- | --- | --- |
| **KNOWN** | Il log rispetta un modello strutturale già confermato, sufficientemente distinto da altri difetti. | Controllo ordinario del report; nessuna nuova conferma richiesta per quella firma. |
| **PROPOSED** | Esiste una corrispondenza plausibile ma una differenza o un conflitto impedisce il riconoscimento automatico. | Verifica, conferma o corregge l'analisi proposta. |
| **NEW** | Non è emerso un candidato storico plausibile. | Analizza il KO come nuovo. |

Una proposta non diventa `known` solo perché ha una similarità alta o un ticket precompilato. Una RAE `known` può essere elencata come bug noto di prodotto/suite soltanto se il campo ticket pertinente è valorizzato.

## Da dove viene la conoscenza

1. Una pipeline esegue i test e produce la RAE con i KO.
2. Il generatore recupera soltanto analisi storiche disponibili nel checkout approvato di `develop` e nel namespace della pipeline.
3. Il QA può confermare che nuove occorrenze appartengono allo stesso difetto; il sistema registra il collegamento alla RAE precedente.
4. Dalle occorrenze così collegate nasce una **famiglia di esempi confermati**, da cui si apprende la struttura.
5. A ogni nuova approvazione QA, la famiglia può ampliare le varianti ammesse.
6. Le risposte automatiche `known` **non addestrano da sole** il modello; si evita così che un errore si auto-rinforzi.

La conferma è sottoposta a controlli di provenienza e validazione; l'appartenenza al branch `develop` è un prerequisito del dato storico, non una garanzia matematica di equivalenza.

## Come ragiona sull'errore senza conoscere il prodotto

Il motore non ha bisogno di sapere cosa siano IUN, UUID, AWS, Java o Cucumber. Confronta le sequenze dei log approvati e distingue:

- **Invarianti:** porzioni rimaste uguali nelle osservazioni approvate.
- **Varianti approvate:** porzioni già viste cambiare nelle osservazioni QA, entro una struttura riconoscibile.
- **Nuove variazioni:** differenze che superano quanto appreso e richiedono controllo.

Drain3 aiuta a identificare una struttura generale ricorrente, ma un suo cluster **non equivale** a un difetto confermato. Il livello di apprendimento QA verifica invarianti, relazioni fra valori e compatibilità prima di autorizzare `known`.

L'analisi considera l'intero log: anche il testo prima e dopo uno stack trace può contenere invarianti importanti. Non si applica una regola diversa solo perché una riga contiene `passed` o `skipped`.

### Esempio 1 — Apprendimento progressivo di un identificativo

- RAE 1, QA conferma `QA-100`: `IUN_ABCD-WMWX-AAA-202611-F-1`.
- RAE 2, QA conferma `QA-100`: `IUN_XYQE-MMQW-AAA-202611-G-1`.
- RAE 3, arriva `IUN_9K7P-Q2MX-NPYQ-202612-Z-8`: alcune porzioni fino ad allora costanti (`AAA`, `202611`, `1`) cambiano. **PROPOSED**.
- Il QA verifica e conferma RAE 3 come lo stesso difetto. La famiglia di esempi si amplia.
- RAE 4, arriva un valore nuovo che rispetta la struttura ampliata: può diventare **KNOWN**, purché gli altri vincoli e il controllo dei conflitti siano soddisfatti.

**Non significa che qualsiasi testo arbitrario sia automaticamente autorizzato.** L'ampliamento deve rimanere ancorato a strutture ed evidenze approvate.

### Esempio 2 — Più manifestazioni dello stesso difetto

Tre occorrenze QA riconducono tutte a `QA-100`:

- Una `ConditionTimeoutException` con 14 frame.
- Due `AssertionFailedError` con 5 frame e diagnostica simile.

Il modello può conservare **due famiglie strutturali** dello stesso difetto. Un quarto log deve essere confrontato con la famiglia pertinente, non rifiutato soltanto perché gli stack delle altre famiglie sono differenti.

Le combinazioni non osservate rimangono soggette a verifica: l'approvazione di eccezione A con stack A e di eccezione B con stack B **non autorizza automaticamente** eccezione A con stack B.

### Esempio 3 — Un'invariante cambia

I KO QA approvati contengono sempre:

```text
negli ultimi 40 minuti
```

Il log corrente contiene:

```text
negli ultimi 44 minuti
```

Il sistema deve segnalare `40 → 44` come variazione non ancora approvata e proporre il caso al QA. Quando l'allineamento lo permette, il feedback può includere anche «negli ultimi 40 minuti» → «negli ultimi 44 minuti». Quando il contesto è ambiguo, **è corretto mostrare soltanto `40` e `44`**, senza inventare frasi o posizioni.

La stessa regola vale per stringhe in qualsiasi altra posizione del log, per esempio `/aws/ecs/pn-national-registries` → `/aws/ecs/pn-national-registriez`.

## Come si evitano i falsi positivi (T7)

Se `QA-100` è compatibile con un nuovo KO ma anche un'altra identità storica, per esempio `QA-104`, è compatibile, **non si sceglie automaticamente il ticket più recente**. Il risultato deve rimanere `proposed` per conflitto, anche se il gruppo concorrente ha una sola occorrenza.

Il controllo dei conflitti avviene **prima** della promozione automatica; la corrispondenza esatta con un esempio approvato non deve bypassarlo.

## Cosa succede dopo una nuova approvazione (T8)

L'approvazione QA aggiunge evidenza alla famiglia di appartenenza e permette il ricalcolo del modello strutturale. Questo può allargare le varianti ammesse ma **non deve rendere la firma genericamente permissiva**.

Se una variazione diventa troppo ambigua oppure appare un altro difetto compatibile, il matcher torna prudente. Un caso un tempo riconoscibile può richiedere nuove verifiche quando cambia il contesto storico.

## Spiegazioni fornite al QA

L'obiettivo è distinguere le motivazioni dalla decisione:

- **Motivo:** quale invariante o famiglia ha impedito `known` (per esempio una nuova riga, una parte di testo modificata, evidenza insufficiente, identità QA concorrenti).
- **Evidenza:** valore storico e corrente esatti; contesto opzionale, solo quando ricavabile in modo affidabile.
- **Decisione:** `known`, `proposed`, `new`.

Esempio di spiegazione: «Rilevata una variazione `40 → 44`. La famiglia dello stack è già osservata ma dispone di una sola conferma QA per generalizzare nuovi valori. È richiesta una verifica». Il feedback deve condensare tutti i motivi in una sola nota per KO.

### Stato della visualizzazione nel Markdown di produzione

**Ad oggi il renderer Markdown non mostra ancora queste motivazioni dettagliate.** Per i `proposed` indica soltanto che la corrispondenza è suggerita e che serve una conferma QA, oltre alla provenienza e al log completo.

Il pannello unico di warning con riepilogo + motivazioni espandibili è **l'evolutiva ancora da implementare**, non una caratteristica già rilasciata. Il simulatore può mostrare più motivi perché legge direttamente i risultati del matcher, mentre lo YAML di produzione non li persiste ancora.

## Responsabilità e limiti

- **Algoritmo:** propone e riconosce entro le evidenze disponibili; non decide la causa reale del difetto.
- **QA:** conferma o corregge l'equivalenza e la documentazione dell'analisi.
- **GitHub Actions:** conserva la tracciabilità di run, PR, revisione e Markdown, senza trasformare il push automatico in una conferma umana.
- **Storico:** è circoscritto per namespace e alle RAE considerate dal loader; il modello non dispone di una conoscenza illimitata.
- **Sicurezza:** `known` è una decisione conservativa, non la conseguenza automatica di una soglia di similarità.

Per i dettagli pratici su installazione, collegamento di nuove pipeline e rigenerazione CLI consultare [README.md](README.md).
