# 20261117 Report Analisi Esiti (RAE) NrtStandardSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release |  |
| Scope | NIGHTLY |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19655 |
| Data e ora | 2026-11-17T21:09:46Z |
| Durata | 00:25:57 |
| Numero KO | 187 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/44176624425/attempts/1 |
| Suite | NrtStandardSuiteTest |
| Ambiente | qa |
| Executor | giuseppe-veldorale |
| Source branch | develop |

### Guida alla compilazione dei campi

- **Result** *(obbligatorio)*: descrivere il motivo del KO e l’esito dell’analisi in modo chiaro e sintetico, senza ripercorrere i singoli passaggi dell’indagine.
- **Procedure** *(obbligatorio)*: documentare passo per passo le verifiche svolte per arrivare al risultato. Se è già stato effettuato un intervento, includere il link alla PR o al ticket relativo.
- **BUG SUITE**: indicare il ticket Jira relativo a un difetto della suite di test. Obbligatorio se non è presente un BUG PRODOTTO, salvo i casi motivati nella nota.
- **BUG PRODOTTO**: indicare il ticket Jira relativo a un difetto del prodotto. Se il problema riguarda componenti esterni (ad esempio SelfCare) e non esiste un ticket, spiegare il motivo nel campo Result. Obbligatorio se non è presente un BUG SUITE, salvo i casi motivati nella nota.

> **Nota:** in assenza di ticket specifici, l’analisi si basa esclusivamente sulle informazioni riportate in **Result** e **Procedure**.

## 70 - KO DOVUTI A BUG DI PRODOTTO NOTI

<details>
<summary>Mostra/nascondi 70 KO</summary>

### 1. [CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per il fruitore [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Confermato un difetto di prodotto intermittente sia in DEV che in QA. Con la richiesta già attiva, il pulsante di download è inizialmente assente, mentre l'atteso è che sia visibile e disabilitato. La stessa assenza è stata osservata nella verifica manuale e spiega il KO della suite durante il controllo dello stato del pulsante. Sulla stessa richiesta il pulsante è poi comparso disabilitato e successivamente si è abilitato. La causa dell'assenza iniziale resta da accertare.

**PROCEDURE**

Rieseguire in locale CONSUMER_AGREEMENT_DOWNLOAD_1 e consultare lo stack trace per verificare se il KO si verifica in Button.isDisabled con una NoSuchElementException. Nella pagina aperta dal test, controllare lo stato della richiesta e la presenza del pulsante «Scarica richiesta di fruizione». Se il pulsante è assente, aprire lo stesso URL in un altro browser con il tenant fruitore e ripetere il controllo al di fuori della suite FE. Aggiornare la pagina ogni 10–15 secondi e annotare se il pulsante compare disabilitato e se successivamente si abilita.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11528

**ERROR LOG**

````text
java.util.NoSuchElementException: Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina.
	at it.pagopa.infrastructure.suit.component.Button.lambda$isDisabled$1(Button.java:17)
	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
	at it.pagopa.infrastructure.suit.component.Button.isDisabled(Button.java:17)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy272.isDisabled(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway.verifyAgreementDownloadAvailability(WebAgreementGateway.java:132)
	at it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway.verifyConsumerAgreementDownloadAvailability(WebAgreementGateway.java:51)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy150.verifyConsumerAgreementDownloadAvailability(Unknown Source)
	at it.pagopa.interop.common.agreement.application.AgreementUseCase.verifyConsumerAgreementDownloadAvailability(AgreementUseCase.java:30)
	at it.pagopa.interop.common.agreement.infrastructure.cucumber.AgreementSteps.verifyAgreementDownloadAvailabilityForConsumer(AgreementSteps.java:61)
	at ✽.il fruitore può scaricare il documento della richiesta di fruizione solo quanto è pronto(classpath:feature-expanded/agreement/download-agreement.feature:10)

````


---

### 2. [PRODUCER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per l'erogatore [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Confermato un difetto di prodotto intermittente sia in DEV che in QA. Con la richiesta già attiva, il pulsante di download è inizialmente assente, mentre l'atteso è che sia visibile e disabilitato. La stessa assenza è stata osservata nella verifica manuale e spiega il KO della suite durante il controllo dello stato del pulsante. Sulla stessa richiesta il pulsante è poi comparso disabilitato e successivamente si è abilitato. La causa dell'assenza iniziale resta da accertare.

**PROCEDURE**

Rieseguire in locale PRODUCER_AGREEMENT_DOWNLOAD_1 e consultare lo stack trace per verificare se il KO si verifica in Button.isDisabled con una NoSuchElementException. Nella pagina aperta dal test, controllare lo stato della richiesta e la presenza del pulsante «Scarica richiesta di fruizione». Se il pulsante è assente, aprire lo stesso URL in un altro browser con il tenant erogatore e ripetere il controllo al di fuori della suite FE. Aggiornare la pagina ogni 10–15 secondi e annotare se il pulsante compare disabilitato e se successivamente si abilita.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11528

**ERROR LOG**

````text
java.util.NoSuchElementException: Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina.
	at it.pagopa.infrastructure.suit.component.Button.lambda$isDisabled$1(Button.java:17)
	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
	at it.pagopa.infrastructure.suit.component.Button.isDisabled(Button.java:17)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy272.isDisabled(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway.verifyAgreementDownloadAvailability(WebAgreementGateway.java:132)
	at it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway.verifyProducerAgreementDownloadAvailability(WebAgreementGateway.java:59)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy150.verifyProducerAgreementDownloadAvailability(Unknown Source)
	at it.pagopa.interop.common.agreement.application.AgreementUseCase.verifyProducerAgreementDownloadAvailability(AgreementUseCase.java:34)
	at it.pagopa.interop.common.agreement.infrastructure.cucumber.AgreementSteps.verifyAgreementDownloadAvailabilityForProducer(AgreementSteps.java:55)
	at ✽.l'erogatore può scaricare il documento della richiesta di fruizione solo quanto è pronto(classpath:feature-expanded/agreement/download-agreement.feature:17)

````


---

### 3. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #2 | line: 52]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale su QA PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [9f4f8f99-7dbd-4709-a58e-8b20a16a0502,
    71b447c4-14d1-440e-bad6-05ee1e12c35a,
    9d2408e9-3e91-44f6-be04-08788ae35106]
to contain exactly (and in same order):
  [9d2408e9-3e91-44f6-be04-08788ae35106,
    9f4f8f99-7dbd-4709-a58e-8b20a16a0502,
    71b447c4-14d1-440e-bad6-05ee1e12c35a]
but there were differences at these indexes:
  - element at index 0: expected "9d2408e9-3e91-44f6-be04-08788ae35106" but was "9f4f8f99-7dbd-4709-a58e-8b20a16a0502"
  - element at index 1: expected "9f4f8f99-7dbd-4709-a58e-8b20a16a0502" but was "71b447c4-14d1-440e-bad6-05ee1e12c35a"
  - element at index 2: expected "71b447c4-14d1-440e-bad6-05ee1e12c35a" but was "9d2408e9-3e91-44f6-be04-08788ae35106"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 4. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #4 | line: 54]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale su QA PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [de60f645-3e5a-4ae3-b708-da1524af2875,
    a14f7d33-c463-4710-8423-42feb2966c5b,
    1c7e4800-27f8-4b5a-806d-29436120a50d]
to contain exactly (and in same order):
  [de60f645-3e5a-4ae3-b708-da1524af2875,
    1c7e4800-27f8-4b5a-806d-29436120a50d,
    a14f7d33-c463-4710-8423-42feb2966c5b]
but there were differences at these indexes:
  - element at index 1: expected "1c7e4800-27f8-4b5a-806d-29436120a50d" but was "a14f7d33-c463-4710-8423-42feb2966c5b"
  - element at index 2: expected "a14f7d33-c463-4710-8423-42feb2966c5b" but was "1c7e4800-27f8-4b5a-806d-29436120a50d"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 5. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.1] - Impossibilità di archiviare un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il rifiuto dell'archiviazione è corretto. L'e-service non può essere archiviato con 60 giorni di preavviso perché una sua versione è già in fase di archiviazione con 120 giorni di preavviso. Atteso da Figma: la snackbar mostra solo il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» Osservato nella UI: la snackbar mostra il messaggio atteso, seguito dalle istruzioni per contattare l'assistenza e da un identificativo copiabile. Il test confronta il testo rilevato nella snackbar con il messaggio atteso tramite un'asserzione di uguaglianza. Il testo aggiuntivo rende le due stringhe diverse e fa fallire il test.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.1. Consultare lo stack trace e confrontare il testo atteso con quello rilevato da WebEServiceGateway.assertArchivingRejection per il caso ARCHIVING_BEFORE_DESCRIPTOR. Durante la riesecuzione, verificare che l'archiviazione venga rifiutata e confrontare il contenuto della snackbar mostrata a video con il design Figma di riferimento. Individuare le parti del messaggio visualizzato che differiscono dal design.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11178

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.
  Se l'errore persiste, apri una richiesta all'assistenza. Inserisci le informazioni di contesto e il codice riportato sotto.
  Identificativo"
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.assertArchivingRejection(WebEServiceGateway.java:133)
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.shouldFailToArchiveEService(WebEServiceGateway.java:77)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy157.shouldFailToArchiveEService(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToArchiveEService(EServiceUseCase.java:43)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchiving(EServiceSteps.java:62)
	at ✽.il sistema rifiuta l'archiviazione dell'e-service da parte dell'erogatore con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:20)

````


---

### 6. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il sistema rifiuta correttamente la richiesta del delegato con 60 giorni di preavviso, perché l'archiviazione dell'e-service precederebbe quella di una versione già pianificata con 120 giorni di preavviso. Da Figma ci si aspetta il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» La snackbar mostra invece «Non è stato possibile richiedere l’archiviazione di questo e-service. Riprova più tardi.», seguito dalle istruzioni per contattare l'assistenza e da un identificativo. Al delegato viene quindi suggerito di riprovare più tardi, mentre per procedere deve selezionare un preavviso più lungo. Il test confronta il testo rilevato nella snackbar con quello atteso tramite un'asserzione di uguaglianza. Il confronto fallisce già sul messaggio principale, che è diverso da quello previsto.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.2 e consultare lo stack trace. Confrontare il testo atteso per ARCHIVING_BEFORE_DESCRIPTOR con quello rilevato da WebEServiceGateway.assertArchivingRejection. Durante la riesecuzione, verificare che la richiesta di archiviazione del delegato venga rifiutata. Confrontare la snackbar mostrata a video con il design Figma e verificare se il messaggio indica il motivo del rifiuto e l'azione da intraprendere.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11179

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non è stato possibile richiedere l’archiviazione di questo e-service. Riprova più tardi.
  Se l'errore persiste, apri una richiesta all'assistenza. Inserisci le informazioni di contesto e il codice riportato sotto.
  Identificativo"
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.assertArchivingRejection(WebEServiceGateway.java:133)
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.shouldFailToRequestEServiceArchiving(WebEServiceGateway.java:89)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy157.shouldFailToRequestEServiceArchiving(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToRequestEServiceArchiving(EServiceUseCase.java:47)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchivingRequest(EServiceSteps.java:69)
	at ✽.il sistema rifiuta la richiesta di archiviazione dell'e-service da parte del delegato con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:36)

````


---

### 7. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_BLANK_STRING @ /answerData/suggestedValues/0

**RESULT**

La suite attende HTTP 400. Il BFF restituisce HTTP 200 e riporta il valore suggerito con tutti e tre gli spazi. L'asserzione fallisce per la differenza tra lo status atteso e quello effettivo. Il 200 è coerente con il contratto OpenAPI attuale, che impone una lunghezza da 1 a 250 caratteri per valore suggerito. Il problema di prodotto è che si può configurare un suggerimento privo di contenuto utile. L'atteso proposto è rifiutare con HTTP 400 i valori composti solo da spazi e aggiornare il contratto OpenAPI. La resa nel portale resta da verificare.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Sostituire /answerData/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11599

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /answerData/suggestedValues/0
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"cb579f1a-3b21-4fa2-8530-08ffab3d28b9","values":[],"editable":false,"annotation":{"id":"3e33de36-9f4e-47ac-8494-39aa7d70cdfa","text":"annotation-UgKvMtmUIRAa7Tfo8nUl","docs":[]},"suggestedValues":["   "]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"annotation-UgKvMtmUIRAa7Tfo8nUl"},"suggestedValues":["   "]}}
- pathParams: {"purposeTemplateId":"33b56339-d75b-416c-96c8-49e7a2135e94"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 8. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

Il BFF restituisce HTTP 400 con codice 002-0005 e dettaglio "EService not found". La suite attende HTTP 404 e l'asserzione fallisce per la differenza di status.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11351

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /eserviceId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"14a95b93-1609-4f02-9a53-200ec8812692","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"8a427881-5e36-4478-8a15-bca2d2ec5f99"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 9. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

**RESULT**

Per il descriptorId inesistente, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0021 e il messaggio "Descriptor provided is not the latest descriptor". Il messaggio presenta il descrittore come una versione non aggiornata, mentre l'ID inviato non corrisponde a un descrittore esistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /descriptorId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11352

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /descriptorId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"4fe6aeb3-1c97-42c9-ab27-0ff4c26e843a","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}]}
- payload: {"eserviceId":"ffed18d5-d656-427b-9213-7a09dee8d86d","descriptorId":"00000000-0000-0000-0000-000000000000"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 10. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

**RESULT**

Per la delega inesistente indicata nel body, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0026 e messaggio "Delegation not found". Il BFF identifica correttamente la delega come inesistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /delegationId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11350

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /delegationId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"566ccba0-5eb3-47cc-af24-aa8bdfc516b5","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"a1227bce-a4b0-4a4b-a957-ea7c77f35211","descriptorId":"abb9e7b5-38ed-478c-9fcd-afa73c2f9ed2","delegationId":"00000000-0000-0000-0000-000000000000"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 11. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

Il BFF accetta una descrizione composta da tre spazi, restituisce HTTP 200 e riporta la descrizione invariata nell'attributo creato. Il test fallisce perché la suite attende HTTP 400. Il 200 è coerente con CertifiedAttributeSeed: description è obbligatoria e lo schema attuale ammette una stringa di soli spazi. Il problema da segnalare è la possibilità di creare un attributo certificato con una descrizione priva di contenuto visibile. L'atteso da confermare con Interop è il rifiuto con HTTP 400 e l'allineamento dello schema OpenAPI.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /description
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"83283517-5958-4ca3-bb4f-400df77a7bcf","code":"d989b467de2fa3e6fbcc4e8ea02d4fb0532f921e2e46b5d09b63c7fc32d5eef9","kind":"CERTIFIED","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-PfXPUvWqjCbms9gsziKZJ89u","creationTime":"2026-11-17T21:20:00.436Z"}
- payload: {"description":"   ","name":"name-PfXPUvWqjCbms9gsziKZJ89u"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 12. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

Il BFF consente la creazione di un attributo certificato con un nome composto solo da spazi e restituisce HTTP 200 in assenza di conflitti. Nella verifica restituisce HTTP 409 con codice 003-0002 ("Duplicate attribute name or code") perché esiste già, per la stessa origine, un attributo con lo stesso nome o codice. La suite attende HTTP 400 per il nome privo di contenuto visibile. Il KO osservato è dovuto al 409 per duplicato. Il difetto di prodotto consiste nella possibilità di creare l'attributo con un nome di soli spazi, consentita dallo schema OpenAPI attuale.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /name
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"046317ee-cf3d-482b-b169-2f2d12a0220d","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}]}
- payload: {"description":"description-4NFUea5CF5AHU7peR4mSOEJlkSpdVR0M","name":"   "}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 13. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /description
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"c315415a-0571-493c-955e-64e7de7bff49","code":"d90b3afb6fecf08f6fafc32ab549bd05b9f56fc8f3964c42efd7e1b094225696","kind":"CERTIFIED_DISCRETE","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-klGuvukLs9PrdPZagiOSL2gT","creationTime":"2026-11-17T21:25:39.299Z"}
- payload: {"description":"   ","name":"name-klGuvukLs9PrdPZagiOSL2gT"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 14. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_URL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'attributo viene creato e il valore contenente l'URL è restituito invariato in /description. Il requisito VAPT VM2 richiede di verificare l'assenza di URL nei campi degli attributi. La mancata validazione è un difetto di prodotto a priorità critica, tracciato in PIN-11608. La propagazione del valore nei punti di visualizzazione richiede una verifica dedicata.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_URL_INJECTION di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Inserire un URL nel solo campo /description, mantenendo gli altri valori validi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11608

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_URL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: A URL injected http://injected.example.test with id URL-aeUJY
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"a2b44c0c-a794-4b3a-afba-bf3fb77c4d93","code":"f4651f5e4cb69d91a17feee9abd295869d2b3a700f95f94243119f9691b6525d","kind":"CERTIFIED_DISCRETE","description":"A URL injected http://injected.example.test with id URL-tsUX8","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-rk3qDug4lrM1Grcbni1jakKY","creationTime":"2026-11-17T21:25:40.544Z"}
- payload: {"description":"A URL injected http://injected.example.test with id URL-tsUX8","name":"name-rk3qDug4lrM1Grcbni1jakKY"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 15. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /name
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"9837b4d9-cdb5-479e-ad31-3a3ce0719430","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}]}
- payload: {"description":"description-NM5zBjxIXCSwtwn3N4yWcAtmC68Eio8E","name":"   "}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 16. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_URL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'attributo viene creato e il valore contenente l'URL è restituito invariato in /name. Il requisito VAPT VM2 richiede di verificare l'assenza di URL nei campi degli attributi. La mancata validazione è un difetto di prodotto a priorità critica, tracciato in PIN-11607. La propagazione del valore nei punti di visualizzazione richiede una verifica dedicata.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_URL_INJECTION di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Inserire un URL nel solo campo /name, mantenendo gli altri valori validi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11607

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_URL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: A URL injected http://injected.example.test with id URL-8pThj
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"31928d2c-6bdb-405b-9791-9de7f613bb58","code":"1379dd9b645d2e24528bae3f912b7424ac766ce966225b28ff0f915203bc9c2a","kind":"CERTIFIED_DISCRETE","description":"description-UhYgo8YcVbLvqElbzyodLFFtvCE4IJlu","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"A URL injected http://injected.example.test with id URL-p4FLi","creationTime":"2026-11-17T21:25:43.519Z"}
- payload: {"description":"description-UhYgo8YcVbLvqElbzyodLFFtvCE4IJlu","name":"A URL injected http://injected.example.test with id URL-p4FLi"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 17. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /description
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"d0e16af5-49d7-466c-9ff6-3c87158132dc","kind":"DECLARED","description":"   ","name":"name-rrQilS0Up63MUU00Co23mNIc","creationTime":"2026-11-17T21:19:10.725Z"}
- payload: {"description":"   ","name":"name-rrQilS0Up63MUU00Co23mNIc"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 18. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /name
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"8a1ea098-4a9f-44e2-b24f-6aea260312d5","kind":"DECLARED","description":"description-IcY9KciUWcgmoReWMN43u920iyjbqpjv","name":"   ","creationTime":"2026-11-17T21:19:13.773Z"}
- payload: {"description":"description-IcY9KciUWcgmoReWMN43u920iyjbqpjv","name":"   "}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 19. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isSignalHubEnabled
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"2e91d343-eebc-436e-b624-75187d25671a","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-oOIHZwvp2aBTzED","description":"description-1NuNo4hG02GsNEM9Fffh","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 20. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isConsumerDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isConsumerDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isConsumerDelegable
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ecee1e92-d541-4d91-8b35-287b670979ce","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-Ykg67iUW9ksh85X","description":"description-37OSwjyRUKwKeRmphxXE","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 21. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isClientAccessDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isClientAccessDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isClientAccessDelegable
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"fd559ec0-0976-47e4-802b-249b7e13377c","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-HTjzlVKmLSL7mB2","description":"description-ZRn622PZqOkKUCA5RcyT","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 22. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /personalData
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6be61e1b-002b-485d-9869-0d1f3192ebc9","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-GwethZDsNj5gWSY","description":"description-dZqD1C3OpoqoAB7jkL6j","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 23. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /asyncExchange
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3ee419c1-613d-4839-a868-8cd0d507b0ea","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-NJb5TMVbtJiI8cW","description":"description-xCWIg6LfOoYU3vhfC1db","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 24. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /version
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3752190b-6fb2-4082-a81f-83a1f756493b","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"version\""}]}
- payload: {"name":"eservice-template-dFmylxUjN4nxEdL","intendedTarget":"target-lQ2RFeEPK1WzDqNf0Q8GAayAPeHGwN","description":"description-nygPhBqP43sV4wrG5PaWSP9zamQKcYB9If4EuG6GSqqKPB7imW","technology":"REST","mode":"DELIVER","version":null,"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 25. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsPerConsumer

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/dailyCallsPerConsumer. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/dailyCallsPerConsumer, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /version/dailyCallsPerConsumer
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3bea2ca0-6808-4e70-bafd-15530fe93fe3","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsPerConsumer\""}]}
- payload: {"name":"eservice-template-xQiZ1ejH9Vt3Vkh","intendedTarget":"target-4eVHTp77xDcCg0jVvTjcW8tZVbMpcj","description":"description-oiq9cXlcES7NW9z0o4HMMMvx6RhRfxpnmvBCMpGFc0SgI07JnD","technology":"REST","mode":"DELIVER","version":{"description":"version-description-eZB9wrFDCTb95pso3ZsX9Sutj14zIvENkeL","voucherLifespan":60,"dailyCallsPerConsumer":null,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 26. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsTotal

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/dailyCallsTotal. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/dailyCallsTotal, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /version/dailyCallsTotal
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"eb10ec68-b58c-4d7f-ba78-afdaac20daeb","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsTotal\""}]}
- payload: {"name":"eservice-template-YTHTBond96qY3jg","intendedTarget":"target-hoRU0czmJR8pnmgtTykiu3bzeZTKJP","description":"description-kBH0ZRL14dW7pyq9j4NuOVOCValo2UsTR9XbhAg3fprcVoZYWW","technology":"REST","mode":"DELIVER","version":{"description":"version-description-RMug3bLzZmRgjpbLzZ5Md1yxKnBuFWsVYF7","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":null,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 27. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/agreementApprovalPolicy

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/agreementApprovalPolicy. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/agreementApprovalPolicy, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /version/agreementApprovalPolicy
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8b1dba70-4336-45e8-9c01-6f3f82ae3971","errors":[{"code":"008-9999","detail":"Validation error: Expected 'AUTOMATIC' | 'MANUAL', received null at \"version.agreementApprovalPolicy\""}]}
- payload: {"name":"eservice-template-rYumf9JhJDxnGgw","intendedTarget":"target-1DFWkToR9LS1ZsoAhpwO5Cro35WGd0","description":"description-O8gb6drP0lcSD98mnbr7ibfBuFiMZNa58bWYrTEMfAJn6EV7zQ","technology":"REST","mode":"DELIVER","version":{"description":"version-description-cjvrSUL9XRcmsuuWCupwxIzOxNzlxapj3Pw","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":null},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 28. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isSignalHubEnabled
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bef0a31e-ab5a-4fdd-9c1b-052a77104acd","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-template-qFQs4t91V7KZWtz","intendedTarget":"target-CSnyTUjoKT9TimnS0DCiiVsO8G4Fov","description":"description-dC4lUvbblfkQtpw7Ik4lPL4DVR0n4hAA9JGeVxc6oredHWxaou","technology":"REST","mode":"DELIVER","version":{"description":"version-description-fggi62GH8IQvIGKX9Pr7Rtnf4rsdfDW6Vm3","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":null,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 29. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /personalData
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f2c9bf59-017b-44b2-a5ad-243ea8849870","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-template-2AjF6YA6fyvRCCd","intendedTarget":"target-Pl1xZabI5VX9QcKT88BRmWzQizTckm","description":"description-wJxHVSGFas4onkhztWtCFj6M6qbRCH3UoP1OWqfqZ6i327iUG4","technology":"REST","mode":"DELIVER","version":{"description":"version-description-K6C9dpC72tyuk0dCWRzqZMOa3ljvsUt133n","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":null,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 30. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11056

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /asyncExchange
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"34071007-d41b-4d25-a7bd-d314dd826c21","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-template-FmR1PChEFXWlkdR","intendedTarget":"target-uFblXDdnWg9oUM03pU3qA1D13OU5Ah","description":"description-e6qCpA5P2QLmv9H8fKCkHXgVvu06oyuplxOIb05srF7nd0t2LW","technology":"REST","mode":"DELIVER","version":{"description":"version-description-8M7i33Je9KnkwhUOWWE1OylTWWHpxQZVUnQ","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":null}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 31. BffClientContractTest::createKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204.La chiave viene accettata con alg vuoto o composto da soli spazi. Il valore non identifica un algoritmo e deve essere rifiutato. Il difetto di prodotto è tracciato in PIN-11354.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Sostituire /alg con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11354

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists","correlationId":"ba000a24-16f4-48b8-929e-caab79e32fb1","errors":[{"code":"006-0017","detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists"}]}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtUzJNQ1NGdEJNbEd4eUsvUFNsNApQQ05XT1E2WW0yUndvNFVyYlJlenh1ZU5STHBYMzNxdGRSQmxRZG1ZbHNTSUQ4K0g4ZXJVMlQxcC9iYm9nSEFXCkNhOWlGZnRrd0FPbnVIcGxzYUhFeDVQUWNVNU1vNU5YWlFHNjRoQUQzTkY0ZE9Bc0dPK0V2NEZCd2pkb055T0sKczZpbUt4cklMNURUWXVBeExHdHN6RlAwTWpIYjNJQnBuWHNKQmUxeFpuTFlYeWRWZ25ucTQ5QzJ6MDhPblJKeApxUUxXa0Z4WUlCWVRYTjZVODZiZm94VFBPU3VyMjRlanlFTDYxR3hBRi9NN0FmaWRJMDFTV1J2cGkxWVB0MG85ClU5VStqV1RHOFpOZExTMmlrOEphVTlqb1I4VUUwdHR0bzBVcHovQk54YzFYRHkvU3JjbzdGY2loOU9iMU1HdmQKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"key-4PRbfymiEhdbKdX"}
- pathParams: {"clientId":"06935927-1511-4f62-80dc-884162b12cb2"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 32. BffClientContractTest::createKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il valore di alg di 5.000 caratteri non rappresenta un identificatore di algoritmo utilizzabile. Occorre validare i valori ammessi e la loro lunghezza. Il conflitto per kid già presente maschera il controllo della lunghezza; la verifica richiede una chiave nuova e un'esecuzione isolata. Il difetto è tracciato in PIN-11053.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Sostituire /alg con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11053

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18851

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists","correlationId":"279ed1e0-f2a3-445b-99bf-d0ec65db3ce1","errors":[{"code":"006-0017","detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists"}]}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtUzJNQ1NGdEJNbEd4eUsvUFNsNApQQ05XT1E2WW0yUndvNFVyYlJlenh1ZU5STHBYMzNxdGRSQmxRZG1ZbHNTSUQ4K0g4ZXJVMlQxcC9iYm9nSEFXCkNhOWlGZnRrd0FPbnVIcGxzYUhFeDVQUWNVNU1vNU5YWlFHNjRoQUQzTkY0ZE9Bc0dPK0V2NEZCd2pkb055T0sKczZpbUt4cklMNURUWXVBeExHdHN6RlAwTWpIYjNJQnBuWHNKQmUxeFpuTFlYeWRWZ25ucTQ5QzJ6MDhPblJKeApxUUxXa0Z4WUlCWVRYTjZVODZiZm94VFBPU3VyMjRlanlFTDYxR3hBRi9NN0FmaWRJMDFTV1J2cGkxWVB0MG85ClU5VStqV1RHOFpOZExTMmlrOEphVTlqb1I4VUUwdHR0bzBVcHovQk54YzFYRHkvU3JjbzdGY2loOU9iMU1HdmQKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"key-4PRbfymiEhdbKdX"}
- pathParams: {"clientId":"7586d2e5-cea7-491a-877c-629bfa7f7115"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 33. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204.La chiave viene accettata con alg vuoto o composto da soli spazi. Il valore non identifica un algoritmo e deve essere rifiutato. Il difetto di prodotto è tracciato in PIN-11354.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11354

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_EMPTY_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: 
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1MnJRVkZpSmhQbUhlZVkxa1lHNApGVGJocm0wK0NMMiszSE9uWmNUa2EvNXdsZkJDUUJKZ0kxMDZ3T0RSQXVhMnpBeTZhNGprR3VXVVJTNjhwMC9oCnB6cjVKclBjN2x2dlg0NlJCemFQZmhBR2VZeEtQS1lLNnJrUjhsM2lIbDAvQ3RNYnM4NHYvVHJUaTBSY01NcnYKcFhpWmdKZFJOdmdGcTZ3WmlLdWVqUERmNk1TL05Rd3V6YlkvV2EvdmJoSWJ3UnJBTzdobTNPcUNPQVZMVG4xeAprdG9KSFFBUVA1QnJIbHdZVTNXYThtcE1rbzd0aTFLbGJEeHRXZjhPaWNEK2drTkZPaWFpT3pUWjNHYTROM3pqCmFjekduWGJQMStmRFFSOFUxSVRzYVpZRGNXOFRXS0JWd29NcDMvOFlXOUMvRVJLQUZ6cU84a2t3OWxsbnp0REcKSHdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"producer-key-4om0m2LCC8oZOaZ"}
- pathParams: {"producerKeychainId":"bd78d171-6577-4a5e-b189-eed375ad0591"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 34. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204.La chiave viene accettata con alg vuoto o composto da soli spazi. Il valore non identifica un algoritmo e deve essere rifiutato. Il difetto di prodotto è tracciato in PIN-11354.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11354

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUErc1J1dkVCNS9tNnViaUU0dGpZOQpJNE0yS2RTMjU2dzlzdER4VWthNHQwZEZrVWI5VmJjS2kxQTRFSERjZk9vTWFHR3doYVEvZlZKT2l5Sm45YUR0Ck1UZGJzeWpYYlpHZ0dscWtPYk84TS8yeCtldnZOU1BRQkdDaHRTKzR2SEt2TzJkazB1eE4yeUI0ZkhHQ2ZVc2wKR0tTYTdJbVBvOWcvbWV2ZHQ4RG1DcDNndzhseFlIRTVSM2JpNjNWT2xvZzZlcU8wb1JKNDU1VWxLS0FLdkNpTQpWSXpNbzRjR1kreW8wMWUwbHNjSFZGMU4xREU4NFlYSElyRWF0TnUyV0FiZXhCOXRxbGFtRjFGemFUaVAweUNsCndEazVmdTFvSUMzMlhCZVVqaGpFK0RBc1pjWjNaYUE4blpRQlJNS2xBcDdiYlFLdlk4bUp0TFRScDlablVJQXcKQVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"producer-key-QIxxeb6sL4c9qra"}
- pathParams: {"producerKeychainId":"bd2e2359-c655-45cc-8e97-9495147cd632"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 35. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. Il valore di alg di 5.000 caratteri non rappresenta un identificatore di algoritmo utilizzabile. Occorre validare i valori ammessi e la loro lunghezza. La registrazione della chiave viene accettata con HTTP 204. Il difetto è tracciato in PIN-11602.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11602

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF4OXp0UzBJLzRMdDVURzVNUjFVeApPNk1TM3JieGJScGs4d0UxRXA4dzJGYXJSVFVqUFJJNmxDN2Z6TTZpcnZzM040emdyUUh0T0Vwc3JHMWcvSHFhCkljYUxqVnpwb3BYeTlJbXlHb0hHeG9yUlU4dkd1czI4b3orQWt2UWMxUXA4Q1g0Q2NhWmIwOUx6dVNtZXRCVGYKdGJwcWpPb20vTUVyRXdTQkRZV212bnh1N3RQK3FzRVJqc0NuVmRIa0FQQmFGZUc5U1FuSkJZYXFrZEF1eDF2YQpMN0FKTytoNjladkJSTVZHR0NXS2FSZnVocFB1RXJaU2syRFRwK0hGSDgzS001bHVNTEpUOFZrUHFEcEVxTDRtCldILzc2OTFpZWJrU2M4b3VDM1RQQklMMzhQclprYUk4UzZvWmNzQk1IZHg5LzlBNGIwY3BvUjRwVkhiVG9HNmEKNlFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"producer-key-3ac3mSGOfisdMt2"}
- pathParams: {"producerKeychainId":"fc974e7f-27dd-4e7c-b4db-7c62c528f3b4"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 36. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

Il lookup con eserviceId nil produce HTTP 500 (008-9991), contro il 404 atteso dalla suite. Difetto di prodotto tracciato nel ticket associato per la risposta interna su un riferimento inesistente. Lo status 4xx preciso va allineato al contratto BFF.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11358

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /eserviceId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","correlationId":"5ceb5805-620c-4c01-a2b5-c98d59581a85","errors":[{"code":"008-9991","detail":"Unexpected error"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-WtpZUWjnhPVPIWw","description":"description-ZlqKvXkLrWJJzc0","isFreeOfCharge":true,"freeOfChargeReason":"reason-fWxwSsohrvDQfQv","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 37. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /riskAnalysisForm/answers/institutionalPurpose/0 viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11356.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /riskAnalysisForm/answers/institutionalPurpose/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11356

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /riskAnalysisForm/answers/institutionalPurpose/0
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"5b9b8b66-ba01-48f5-ad3e-878cd63fe792"}
- payload: {"eserviceId":"8aef1ec7-4c64-41fb-967a-926c60b541d7","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-gpBnpFGIOc2XFMN","description":"description-TeVImgeNpR0tcsT","isFreeOfCharge":true,"freeOfChargeReason":"reason-ZhAZHFvEjy5J7C8","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 38. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /freeOfChargeReason viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /freeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11359

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"26d380d6-8027-4ba0-a0dc-51c73beaa58d"}
- payload: {"eserviceId":"f99c0dbb-c878-424b-87d8-373d22280175","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-zttZV8DaHMGn8LZ","description":"description-ZjvujmdreRZY5BT","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 39. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11447.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11447

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"4623b7c4-1d92-40ed-8f5d-435e6224e8e5"}
- payload: {"eserviceId":"64938e8f-87f1-4e13-b9a1-4c3d13e28d5b","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-3GoJ6D02cexB1kH","description":"description-T48gItrPLsCqVXV","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 40. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio applicativo identifica correttamente l'assenza dell'e-service. L'operazione deve restituire HTTP 404. Il mapping a 400 è un difetto di prodotto tracciato in PIN-11376.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11376

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /eserviceId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"6d328d14-953a-4aad-b200-ded576410dea","errors":[{"code":"004-0002","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"166ab7d9-f2e4-4b60-ac40-e8bea6b14f73","title":"purpose-W57KPtjnGBCnlfN","description":"description-GJf7HJCo9iQybpy","isFreeOfCharge":true,"freeOfChargeReason":"reason-c8QFVrMwi91BkDa","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 41. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /riskAnalysisId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio applicativo identifica correttamente l'assenza dell'analisi del rischio. L'operazione deve restituire HTTP 404. Il mapping a 400 è un difetto di prodotto tracciato in PIN-11454.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /riskAnalysisId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11454

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /riskAnalysisId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Risk analysis not found","status":400,"detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService e826de79-8c53-467c-a825-4a68c2913fa9","correlationId":"8ac8ef51-6804-4b8d-a586-8ad7b1f23dae","errors":[{"code":"004-0019","detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService e826de79-8c53-467c-a825-4a68c2913fa9"}]}
- payload: {"eserviceId":"e826de79-8c53-467c-a825-4a68c2913fa9","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"00000000-0000-0000-0000-000000000000","title":"purpose-sOHJwVP67nu0Fxp","description":"description-Hmx10dNo7zLs6JO","isFreeOfCharge":true,"freeOfChargeReason":"reason-WwRjp9b9Xpjuavy","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 42. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11448.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11448

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"74391146-3c97-4824-a3d8-897ad6f4f82d"}
- payload: {"eserviceId":"1a062d33-e375-49c4-aea6-ecd96f316400","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"aeb89676-ee24-4718-986d-91dccf9184b3","title":"purpose-PUl4oQn9qkqXiFd","description":"description-NYrGFKVQLxPgD5e","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 43. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /freeOfChargeReason viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /freeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11359

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"665b5f00-0f88-498c-837a-28d568417b79"}
- payload: {"eserviceId":"70d6a6a3-2c4b-497a-baa2-3e21edbcd1a4","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"68f6576e-33a7-4720-8521-ce073f5b5b1f","title":"purpose-B2m9usyn8YAXygJ","description":"description-iQbn0BbGy4NvbXb","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool.helpJoin(ForkJoinPool.java:1883)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:440)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 44. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. Il template accetta e restituisce un valore suggerito composto da soli spazi. Il vincolo di lunghezza minima è soddisfatto, ma il suggerimento resta privo di contenuto visibile. Occorre rifiutare il valore con HTTP 400. Il difetto di prodotto è tracciato in PIN-11599.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11599

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"f2136c27-6a9b-47ae-8298-baf92e609fab"}
- payload: {"targetDescription":"target-description-qgWFiZYaWckyhRr37j5V","targetTenantKind":"PA","purposeTitle":"purpose-template-vq8ggQKEW8o09V3","purposeDescription":"purpose-description-sqQAMrWv5kIJyUt3FT90","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-QbPGYvUJnhcaenODby9U","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 45. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. Il template gratuito viene creato con una motivazione composta da soli spazi. La presenza della proprietà non soddisfa il requisito funzionale di fornire una motivazione effettiva. Occorre rifiutare il valore con HTTP 400. Il caso è coperto da PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeFreeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11359

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"a2ceab41-edbd-4eb5-9158-dcd92fb82a5b"}
- payload: {"targetDescription":"target-description-vffrPciRyrlINtK745kg","targetTenantKind":"PA","purposeTitle":"purpose-template-vyjAoMZs3JK8SZX","purposeDescription":"purpose-description-umpfQmZHN0QrADKvGyuF","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"   ","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 46. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. L'operazione accetta /purposeFreeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11410.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeFreeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11410

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"635e24b2-826b-4c27-86e3-a08d0d0a1c93"}
- payload: {"targetDescription":"target-description-e1rCkbLK3MScR2hMiAQG","targetTenantKind":"PA","purposeTitle":"purpose-template-du4Bphg2ZRHcXpj","purposeDescription":"purpose-description-dNqKrzsbrCbG87PR80T2","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 47. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /description
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"2d5ebdf3-3f4d-4e64-8d6f-a093368dfc39","kind":"VERIFIED","description":"   ","name":"name-qyXlnxa4IpkmRKFpI38hHxB4","creationTime":"2026-11-17T21:19:21.456Z"}
- payload: {"description":"   ","name":"name-qyXlnxa4IpkmRKFpI38hHxB4"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 48. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-10918

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /name
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name","status":409,"detail":"An attribute with name     already exists","correlationId":"eaeddbea-9e6a-4942-81fa-e38c29a0f83b","errors":[{"code":"003-0002","detail":"An attribute with name     already exists"}]}
- payload: {"description":"description-93K467JyFK6PRh757ToNzR5lhg0NxVPA","name":"   "}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 49. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11455

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_WRONG_TYPE_NUMBER
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: 123
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":123}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 50. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11455

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":"00000000-0000-0000-0000-000000000000"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 12 more

````


---

### 51. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11455

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_MALFORMED_UUID
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: not-a-valid-uuid-12345
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":"not-a-valid-uuid-12345"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 12 more

````


---

### 52. BffEServiceContractTest::queryEServicesCatalog::[payload] REPLACED_WITH_LONG_STRING @ /keyword

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /keyword di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11526.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffEServiceContractTest::queryEServicesCatalog su POST /catalog. Sostituire /keyword con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11526

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /keyword
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"results":[],"pagination":{"offset":0,"limit":200,"totalCount":0}}
- payload: {"offset":0,"limit":200,"keyword":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","sortBy":"NAME_ASC","onlyActiveEservices":false,"availableForRequester":false,"subscribedByRequester":false,"onlyTemplateInstances":false,"hasLinkedPurposeTemplates":false,"producerCategories":["COMUNI"],"asyncExchange":false,"mode":"DELIVER","onlySignalHubEnabled":false,"producersIds":[]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 53. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11605.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11605

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_EMPTY_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: 
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 404
- responseBody: {"type":"about:blank","title":"Delegation not found","status":404,"detail":"Delegation cacda8b6-0d84-4680-ab18-2cfbe9946dd8 of kind DelegatedConsumer not found","correlationId":"a7c8d5f3-f708-4e14-b62c-8858bdd11496","errors":[{"code":"010-0001","detail":"Delegation cacda8b6-0d84-4680-ab18-2cfbe9946dd8 of kind DelegatedConsumer not found"}]}
- payload: {"rejectionReason":""}
- pathParams: {"delegationId":"cacda8b6-0d84-4680-ab18-2cfbe9946dd8"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <404>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 54. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione composta da soli spazi non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11444

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 404
- responseBody: {"type":"about:blank","title":"Delegation not found","status":404,"detail":"Delegation 5115768f-7cf4-4a79-8214-fd52b1b49530 of kind DelegatedConsumer not found","correlationId":"26c6d684-47fb-436c-b200-e60c37730eed","errors":[{"code":"010-0001","detail":"Delegation 5115768f-7cf4-4a79-8214-fd52b1b49530 of kind DelegatedConsumer not found"}]}
- payload: {"rejectionReason":"   "}
- pathParams: {"delegationId":"5115768f-7cf4-4a79-8214-fd52b1b49530"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <404>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 55. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11604.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11604

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 404
- responseBody: {"type":"about:blank","title":"Delegation not found","status":404,"detail":"Delegation 2df3c1ec-60db-4ecf-9981-ced71c736a1f of kind DelegatedConsumer not found","correlationId":"835da511-7b7d-459a-871f-e708967b836f","errors":[{"code":"010-0001","detail":"Delegation 2df3c1ec-60db-4ecf-9981-ced71c736a1f of kind DelegatedConsumer not found"}]}
- payload: {"rejectionReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
- pathParams: {"delegationId":"2df3c1ec-60db-4ecf-9981-ced71c736a1f"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <404>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 56. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11444

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_EMPTY_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: 
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":""}
- pathParams: {"delegationId":"f65b825b-73a6-4c2f-9373-07b765ba3d90"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 57. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 404. La motivazione composta da soli spazi non fornisce un contenuto effettivo. La risposta di delega assente impedisce di osservare la validazione della motivazione. Occorre ripetere il caso con una delega rifiutabile. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11444

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 404
- responseBody: {"type":"about:blank","title":"Delegation not found","status":404,"detail":"Delegation 38025226-aba1-490d-affd-9477cb4d9b5e of kind DelegatedProducer not found","correlationId":"081ab1d2-dc4c-4434-968d-3a78abbc0506","errors":[{"code":"010-0001","detail":"Delegation 38025226-aba1-490d-affd-9477cb4d9b5e of kind DelegatedProducer not found"}]}
- payload: {"rejectionReason":"   "}
- pathParams: {"delegationId":"38025226-aba1-490d-affd-9477cb4d9b5e"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <404>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 58. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11603.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11603

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 404
- responseBody: {"type":"about:blank","title":"Delegation not found","status":404,"detail":"Delegation d2c86612-9b3c-4a9b-8a66-c7a320ade67a of kind DelegatedProducer not found","correlationId":"f313d2c8-9d17-44b1-b491-9a73e875783a","errors":[{"code":"010-0001","detail":"Delegation d2c86612-9b3c-4a9b-8a66-c7a320ade67a of kind DelegatedProducer not found"}]}
- payload: {"rejectionReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
- pathParams: {"delegationId":"d2c86612-9b3c-4a9b-8a66-c7a320ade67a"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <404>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 59. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11456

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_MALFORMED_UUID
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: not-a-valid-uuid-12345
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":"not-a-valid-uuid-12345"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 60. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11456

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_WRONG_TYPE_NUMBER
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: 123
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":123}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 61. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11456

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PATH_PARAMS
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /purposeTemplateId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 500
- responseBody: {"type":"about:blank","title":"Unexpected error","status":500,"detail":"Unexpected error","errors":[{"code":"000-9991","detail":"Unexpected error"}]}
- payload: null
- pathParams: {"purposeTemplateId":"00000000-0000-0000-0000-000000000000"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <500>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 12 more

````


---

### 62. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11449.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11449

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"47e37c24-ea26-4280-bf25-4e4edfa63d8a","versionId":"22f12de0-57b1-4ad4-81ce-ca4ec0c4c28d"}
- payload: {"title":"purpose-K7mZrR6bwHdAQi6","description":"description-Oxbd4WW0KTeuyzl","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"47e37c24-ea26-4280-bf25-4e4edfa63d8a"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 63. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /riskAnalysisForm/answers/institutionalPurpose/0 viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11356.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Sostituire /riskAnalysisForm/answers/institutionalPurpose/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11356

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /riskAnalysisForm/answers/institutionalPurpose/0
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"020b0d3c-2779-481e-98fb-e163756eda7b","versionId":"5cd5d72f-0318-401b-8b14-0e7ddc8ff813"}
- payload: {"title":"purpose-8RvzfPZcuKsRrUt","description":"description-Xk2RpTGmhFTZFzH","isFreeOfCharge":true,"freeOfChargeReason":"reason-I0gEoPLEcdClNZC","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"020b0d3c-2779-481e-98fb-e163756eda7b"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 64. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il template accetta e restituisce un valore suggerito composto da soli spazi. Il vincolo di lunghezza minima è soddisfatto, ma il suggerimento resta privo di contenuto visibile. Occorre rifiutare il valore con HTTP 400. Il difetto di prodotto è tracciato in PIN-11606.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Sostituire /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11606

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"0eb1829e-5076-4d29-a560-00d2754c8639","targetDescription":"target-description-oCnFLszgS0JwQoB1JceT","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:26:54.274Z","updatedAt":"2026-11-17T21:26:56.543Z","purposeTitle":"purpose-template-VCjtn7M0bJpx1yM","purposeDescription":"purpose-description-uSHl1b2zfihdEPhdm7pZ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"27f71410-3a97-47cf-bf57-43f7fd80770a","values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"id":"1d66aa3a-acbf-45fc-a908-04dcaf176616","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"0243674e-de7a-4acb-9e0e-3caa2b9519dd","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"1b127822-28c0-4093-8c9a-02993f68cc48","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"2e256c52-b88d-4798-9050-f9c91636a918","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-s7uQmBs9wRsiVXkioJ04","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-oCnFLszgS0JwQoB1JceT","targetTenantKind":"PA","purposeTitle":"purpose-template-VCjtn7M0bJpx1yM","purposeDescription":"purpose-description-uSHl1b2zfihdEPhdm7pZ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-s7uQmBs9wRsiVXkioJ04","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"0eb1829e-5076-4d29-a560-00d2754c8639"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 65. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /purposeFreeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11450.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Sostituire /purposeFreeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11450

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"f7bc5bf6-7ad5-4cd5-8271-fb45030744f7","targetDescription":"target-description-wPJxftv69obmFmpUG6bL","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:28:34.513Z","updatedAt":"2026-11-17T21:28:36.782Z","purposeTitle":"purpose-template-X7YzIcY3RGRGeVD","purposeDescription":"purpose-description-IFCW0X63JDdbpiFRBVrV","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"9890f2b9-66ce-4059-ad62-d0bf811cd9d9","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"f3f082e6-f210-43f3-b0be-a85ad666b483","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"eec1bebc-c754-450e-8e50-c57cefb24c67","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"6dcc88ec-a3e1-4c0b-89df-b820244f9c9c","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"33359bd5-b136-4077-bdf0-a08dc3aa1618","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-wPJxftv69obmFmpUG6bL","targetTenantKind":"PA","purposeTitle":"purpose-template-X7YzIcY3RGRGeVD","purposeDescription":"purpose-description-IFCW0X63JDdbpiFRBVrV","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"f7bc5bf6-7ad5-4cd5-8271-fb45030744f7"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 66. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11451.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11451

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_LONG_STRING
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"92991c5a-a4db-417a-885e-0430bd98e15d","versionId":"f99de7ab-c413-414c-840f-5f5ea98b2ff3"}
- payload: {"title":"purpose-uzmWqCzElXMGki4","description":"description-eytoicqwkj0fELi","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: {"purposeId":"92991c5a-a4db-417a-885e-0430bd98e15d"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool.helpJoin(ForkJoinPool.java:1883)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:440)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 67. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio segnala un e-service inesistente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11351.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11351

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /eserviceId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"120a20a9-f829-4322-a175-cca2d2baff52","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"12aad549-0959-4027-9144-fe368dcfbe70"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 68. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il descriptorId inesistente viene descritto come versione non più recente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11352.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /descriptorId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11352

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /descriptorId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"c786002b-6923-456d-924d-360fe4dd4aaa","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}]}
- payload: {"eserviceId":"dc6eaa1d-2bad-41cf-a348-818c2eaa2e38","descriptorId":"00000000-0000-0000-0000-000000000000"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 69. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio segnala una delega inesistente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11350.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /delegationId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11350

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /delegationId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"0525de47-7eb3-41e5-a4e9-f7f85aa79a4d","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"f8e7a873-53a5-4950-92c3-531d32acad40","descriptorId":"85afe9fe-6dab-478d-a22e-6f1757fa552a","delegationId":"00000000-0000-0000-0000-000000000000"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 70. WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: 11 sezioni ordinate, titoli esatti e paragrafi non vuoti

**RESULT**

La sezione Proprietà intellettuale viene visualizzata con il numero 8, mentre la sequenza attesa prevede il numero 9. Il difetto riguarda la numerazione dei termini di servizio ed è tracciato come prodotto in PIN-11378. Mantenere l'aspettativa coerente con la numerazione prevista.

**PROCEDURE**

Confrontare indice e intestazioni visibili dei termini di servizio con la revisione pubblicata e con le costanti usate da assertSections.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11378

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Titolo: Proprietà intellettuale] 
expected: "9. Proprietà intellettuale"
 but was: "8. Proprietà intellettuale"
	at it.pagopa.interop.suite.contract.WebTOSContractTest.assertSections(WebTOSContractTest.java:128)

````

</details>

## 113 - KO DOVUTI A BUG DI SUITE NOTI

<details>
<summary>Mostra/nascondi 113 KO</summary>

### 1. [PRIVATE_CATALOG_TEXT_SEARCH_6] Ricerca approssimata con un refuso [Given=BFF,When=BFF,Then=BFF]

**RESULT**

Il suffisso casuale aggiunto al nome dell'e-service altera la similarità con il termine cercato e rende il fixture inadatto alla verifica del refuso. Lo scenario è stato posto in ignore e non dovrebbe comparire nelle prossime run. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_TEXT_SEARCH_6 in debug e sospendere l'esecuzione prima che l'hook After archivi o elimini l'e-service. Dai log della ri-esecuzione, risalire dall'ID atteso di ES-1 al nome effettivamente pubblicato e verificare quale suffisso ha aggiunto la suite al nome definito nel Gherkin. Con l'e-service ancora disponibile, ripetere nel catalogo autenticato la ricerca dello scenario usando lo stesso termine con refuso e gli stessi eventuali filtri. Ripetere la ricerca con il nome effettivo completo, suffisso incluso, per verificare se ES-1 è reperibile. Confrontare entrambi gli esiti con il timeout registrato nei log.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.IllegalStateException: Timed out waiting for catalog presence across pages: expected IDs [e91d8c5e-8ef1-4758-a6fe-8c5017924196], missing IDs [e91d8c5e-8ef1-4758-a6fe-8c5017924196], last actual IDs [], last scanned offsets [0], last scan horizon 0, offset 0, limit 200, duration 30467ms
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceCatalogGateway.awaitPresence(BffEServiceCatalogGateway.java:92)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy160.awaitPresence(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase.awaitPresence(EServiceCatalogUseCase.java:39)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogSearchSteps.awaitPresence(EServiceCatalogSearchSteps.java:103)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEService(EServiceCatalogAssertionSteps.java:30)
	at ✽.l'e-service "ES-1" è presente tra i risultati della ricerca(classpath:feature-expanded/catalog/private-catalog-text-search.feature:109)
Caused by: java.lang.IllegalStateException: Polling in timeout dopo 30 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:80)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceCatalogGateway.awaitPresence(BffEServiceCatalogGateway.java:77)
	... 13 more
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.interop.common.eservice.application.EServiceCatalogTraversal@57d2c2db> within 30 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 15 more

````


---

### 2. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_NULL @ /answerData/annotation

**RESULT**

La suite legge @Nullable sul campo annotation della classe Java autogenerata e deduce che anche "annotation": null sia valido nel JSON. Per questo classifica la mutazione come INFERRED_VALID e attende HTTP 200. Nel modello Java, null può rappresentare un campo omesso. Il contratto OpenAPI consente di omettere annotation, ma se il campo è presente richiede un oggetto. Il BFF riceve un null esplicito e restituisce correttamente HTTP 400. Il test fallisce perché attende 200. Per questa mutazione, la suite deve ricavare l'aspettativa dallo schema OpenAPI e attendere HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Inviare un null JSON esplicito in /answerData/annotation, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /answerData/annotation
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d760ae60-cf74-463b-b974-cc5c8d4e4f14","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"answerData.annotation\""}]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":null,"suggestedValues":["answer-value-WgXiglDgMeUpcfQ"]}}
- pathParams: {"purposeTemplateId":"1c7cae51-0eb8-4276-84c3-33249ad75adb"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 3. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_SQL_INJECTION @ /answerData/annotation/text

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Consultare la richiesta e la risposta dello scenario su /answerData/annotation/text e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /answerData/annotation/text
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"2054b58c-6069-4fff-a8d1-ac460694626a","values":[],"editable":false,"annotation":{"id":"2bd575d7-80bf-4a8a-9cdf-53b15d01e7fe","text":"' OR '1'='1","docs":[]},"suggestedValues":["answer-value-pad2qljXBHDDWxX"]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"' OR '1'='1"},"suggestedValues":["answer-value-pad2qljXBHDDWxX"]}}
- pathParams: {"purposeTemplateId":"f948e673-62b5-4990-8bc8-e4b80d066ef1"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 4. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_SQL_INJECTION @ /answerData/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Consultare la richiesta e la risposta dello scenario su /answerData/suggestedValues/0 e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /answerData/suggestedValues/0
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"5aa47fce-5b86-4b83-a2ce-a147823b49c1","values":[],"editable":false,"annotation":{"id":"c6e175d5-e7fd-438b-bb3d-6f79979ab135","text":"annotation-FwYdME0tzgm07MgV7AoM","docs":[]},"suggestedValues":["' OR '1'='1"]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"annotation-FwYdME0tzgm07MgV7AoM"},"suggestedValues":["' OR '1'='1"]}}
- pathParams: {"purposeTemplateId":"a8148e57-3cd9-42a9-aff3-d6ed4ac665dd"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 5. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswerAnnotation::[payload] REPLACED_WITH_SQL_INJECTION @ /text

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswerAnnotation su PUT /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers/{answerId}/annotation. Consultare la richiesta e la risposta dello scenario su /text e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /text
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"509d3f1b-c532-4833-a420-8e2f8b1a8f8a","text":"' OR '1'='1","docs":[]}
- payload: {"text":"' OR '1'='1"}
- pathParams: {"answerId":"e1ebe640-8010-477e-88b2-094e1e987124","purposeTemplateId":"81020499-6088-4f7d-a41c-3a5100911fe3"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 6. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

OpenAPI consente di omettere delegationId. Se presente, il campo deve contenere una stringa in formato UUID. Il BFF riceve un null esplicito e restituisce HTTP 400, come previsto dal contratto. La suite classifica la mutazione come INFERRED_VALID e attende HTTP 200. Il test fallisce perché la suite tratta il null esplicito come valido per un campo opzionale. Per questa mutazione deve attendersi HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffAgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /delegationId
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"15be26e4-cdd8-4241-8368-dd8320bb95a2","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"41879073-67da-4649-835b-3e82f0d5f8ee","descriptorId":"98bb64aa-c095-449c-a298-a6c8568e329a","delegationId":null}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 7. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_NULL @ /description

**RESULT**

In ClientSeed, description è opzionale e, se presente, deve contenere una stringa. Il JSON inviato contiene un null esplicito. Il BFF restituisce HTTP 400 con l'errore "Expected string, received null at description", in linea con lo schema OpenAPI. La suite classifica la mutazione come INFERRED_VALID e si aspetta HTTP 200. L'asserzione fallisce perché l'aspettativa non distingue tra omissione del campo e invio di null. Per questa mutazione, la suite deve attendersi HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffClientContractTest::createApiClient su POST /clientsApi. Inviare un null JSON esplicito in /description, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8dd4fa49-dcbe-46df-bee9-58809830051c","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-EkLyucUCk8ETokXKGYMUsKdd","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 8. BffClientContractTest::createApiClient::[payload] REMOVED @ /members/0

**RESULT**

La mutazione lascia members presente con valore []. Lo schema ClientSeed richiede la presenza di members e consente un array vuoto perché non specifica minItems. Il BFF restituisce HTTP 200 con l'ID del client creato, in linea con il contratto. La suite classifica il payload come POLICY_INVALID e attende HTTP 400. L'asserzione fallisce per questa aspettativa. Per il caso REMOVED su /members/0, la suite deve attendersi HTTP 200.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffClientContractTest::createApiClient su POST /clientsApi. Rimuovere esclusivamente /members/0 dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19650

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /members/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_INVALID
- responseStatus: 200
- responseBody: {"id":"dd8177e4-eb73-40ba-9278-73baf29dc027"}
- payload: {"name":"client-itFa20gv70foneGiU1Z5sH5w","description":"description-zxrVTTRYkgEYEHSyyqW1p5GvceMSRgp9","members":[]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 9. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createApiClient su POST /clientsApi. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"df90fa05-807b-4263-a3f8-2b8eb26705bc"}
- payload: {"name":"' OR '1'='1","description":"description-Lz7X76bl7XNEldWuUROAvM3RifWdvUQD","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 10. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createApiClient su POST /clientsApi. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"1a83901b-e806-4b9a-a415-640f071faf42"}
- payload: {"name":"client-SL0NiIgvxa6YjLPCv7rtTihI","description":"' OR '1'='1","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 11. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"3ddb60b1-37d8-4a01-8313-d043f03c20b8","code":"88d223c1a5a44f32d6fcfc5727bc05a69fcf580da910080f08c946e9a502b890","kind":"CERTIFIED","description":"' OR '1'='1","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-NdlvWFxxqmoZZFeYLmOm6HL8","creationTime":"2026-11-17T21:20:01.310Z"}
- payload: {"description":"' OR '1'='1","name":"name-NdlvWFxxqmoZZFeYLmOm6HL8"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 12. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name ' OR '1'='1 or code eaec06bf3a1b96664dcf224b93180d1f53774de2b1839040fa83e7421f09ee08 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"b8939d68-7617-42b4-96cb-b4e550c4635a","errors":[{"code":"003-0002","detail":"An attribute with name ' OR '1'='1 or code eaec06bf3a1b96664dcf224b93180d1f53774de2b1839040fa83e7421f09ee08 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}]}
- payload: {"description":"description-PusOZJ1RA55bzRSlDIQTstdTtjj1w875","name":"' OR '1'='1"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 13. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"35c606eb-ff3e-43b9-a107-877ed0f9940e","code":"c1fed42efd25ac14324f813c7b5731aa17ff2e77527a6c0841ed94f52099509b","kind":"CERTIFIED_DISCRETE","description":"' OR '1'='1","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-U4tEbNFUWP7qFzDqgYf1W0xH","creationTime":"2026-11-17T21:25:40.111Z"}
- payload: {"description":"' OR '1'='1","name":"name-U4tEbNFUWP7qFzDqgYf1W0xH"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 14. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name ' OR '1'='1 or code eaec06bf3a1b96664dcf224b93180d1f53774de2b1839040fa83e7421f09ee08 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"cc954126-0784-43df-ab77-691267838685","errors":[{"code":"003-0002","detail":"An attribute with name ' OR '1'='1 or code eaec06bf3a1b96664dcf224b93180d1f53774de2b1839040fa83e7421f09ee08 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}]}
- payload: {"description":"description-i7JZCW3XNcsVT4aHjOxtGajjONIApRwb","name":"' OR '1'='1"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 15. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_NULL @ /description

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /description. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Inviare un null JSON esplicito in /description, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d8ca043d-77f8-465f-9112-fd012952b8c0","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-1eRxlSfnxbX4G4iX8WcbhdGf","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 16. BffClientContractTest::createConsumerClient::[payload] REMOVED @ /members/0

**RESULT**

La suite pretende 400 per l’assenza dell’elemento, ma il BFF accetta members: [] con HTTP 200. La policy deve distinguere la rimozione di un membro dall’omissione della proprietà. Il caso è analogo a createApiClient, già ricondotto a QA-19650.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Rimuovere esclusivamente /members/0 dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19650

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /members/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_INVALID
- responseStatus: 200
- responseBody: {"id":"6f9f30f6-af57-44a9-bc2c-bf89cfc46657"}
- payload: {"name":"client-1eRxlSfnxbX4G4iX8WcbhdGf","description":"description-UUbN203fUxc2AVieHAocuubHpfOiqF1s","members":[]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 17. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"e2c4930b-6672-4cdb-84c4-92410395e77b"}
- payload: {"name":"' OR '1'='1","description":"description-UUbN203fUxc2AVieHAocuubHpfOiqF1s","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 18. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"607f810d-9058-4ea0-957d-d7402a1ccc9f"}
- payload: {"name":"client-1eRxlSfnxbX4G4iX8WcbhdGf","description":"' OR '1'='1","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 19. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"081868cd-998b-4f7f-9bd5-5b8600551377","kind":"DECLARED","description":"' OR '1'='1","name":"name-VOHJjJRJkslRkWidBkI1Mxeq","creationTime":"2026-11-17T21:19:11.657Z"}
- payload: {"description":"' OR '1'='1","name":"name-VOHJjJRJkslRkWidBkI1Mxeq"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 20. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"789c0627-6f93-47f3-8386-92405c91f47d","kind":"DECLARED","description":"description-V9keWoWzpinbzaAeHZjiKgC1BeBlidu1","name":"' OR '1'='1","creationTime":"2026-11-17T21:19:14.620Z"}
- payload: {"description":"description-V9keWoWzpinbzaAeHZjiKgC1BeBlidu1","name":"' OR '1'='1"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 21. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::createEService su POST /eservices. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"EService template name conflict","status":409,"detail":"An EService template with name ' OR '1'='1 already exists","correlationId":"73d927d9-9f8e-4991-8586-579d4bffc99b","errors":[{"code":"001-0039","detail":"An EService template with name ' OR '1'='1 already exists"}]}
- payload: {"name":"' OR '1'='1","description":"description-bjWmFdn2SsgRA9O4R8ev","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 22. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::createEService su POST /eservices. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"70e2e713-ef9b-4498-94f0-f1cf3310452a","descriptorId":"489db0d2-3c02-4687-b83f-ae6cce5bf586"}
- payload: {"name":"eservice-aGR3x1vhgL0o0np","description":"' OR '1'='1","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 23. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"2175488f-46c4-49cc-8784-c277a29d34e5","versionId":"94cde101-f933-4c09-8ea5-70f658a5b9a2"}
- payload: {"name":"' OR '1'='1","intendedTarget":"target-acAzLP7z2g8Q2CPvrTk2GB3FsMFGDa","description":"description-takgQEize8cp6JZY8kGmJkG7F6EI8IDQ0Kgys1PHRZJqMwtYBU","technology":"REST","mode":"DELIVER","version":{"description":"version-description-ydVg2gZSKxHx1mhO1vzzJmraUTgrk65xkga","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 24. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /intendedTarget

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Consultare la richiesta e la risposta dello scenario su /intendedTarget e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /intendedTarget
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"93ef322b-45d3-4d4e-8488-943f082fc497","versionId":"e3adf2f1-dfab-423c-9f58-ed37a6ae9caa"}
- payload: {"name":"eservice-template-CmKetTohvEeK8UR","intendedTarget":"' OR '1'='1","description":"description-IdV1sbHFnCsEG6iMq9LXd0aKVshyuDTcoaSWBXjjBhovfPkdHu","technology":"REST","mode":"DELIVER","version":{"description":"version-description-wkg6SUwhyrHUrm4EINggi8FTHruZ48NrlPC","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 25. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"4f7ea9c5-192d-4c84-9bfe-93740fb5dec2","versionId":"19ce31ee-588d-4383-aaeb-fdef94b9fe7f"}
- payload: {"name":"eservice-template-2RlHH7UMiR9nr9A","intendedTarget":"target-HuMbY23HcgDVwdxGeVIxl4O0EAOAc1","description":"' OR '1'='1","technology":"REST","mode":"DELIVER","version":{"description":"version-description-An0bI2NUTVxCuMmSed3HWkRNxH4FUNkUNzO","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 26. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /version/description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Consultare la richiesta e la risposta dello scenario su /version/description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /version/description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"b69d031d-1f90-4bd6-a210-2a9e5c4f2668","versionId":"7879611c-3df2-4c76-bd8a-c2e9efd04620"}
- payload: {"name":"eservice-template-rXEOa0mmDmf0GEj","intendedTarget":"target-lOPCCdSKZcM06mvLJCdaHBQaeadZEl","description":"description-VqGXYRWEKR8wIa0pWOhqO4ke9XXlXlSTbN6cRvseESKy0qLRF4","technology":"REST","mode":"DELIVER","version":{"description":"' OR '1'='1","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 27. BffClientContractTest::createKey::[payload] REPLACED_WITH_SQL_INJECTION @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Consultare la richiesta e la risposta dello scenario su /alg e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18851

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists","correlationId":"b1342082-71f0-4c25-89f1-63486c5cdfce","errors":[{"code":"006-0017","detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists"}]}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtUzJNQ1NGdEJNbEd4eUsvUFNsNApQQ05XT1E2WW0yUndvNFVyYlJlenh1ZU5STHBYMzNxdGRSQmxRZG1ZbHNTSUQ4K0g4ZXJVMlQxcC9iYm9nSEFXCkNhOWlGZnRrd0FPbnVIcGxzYUhFeDVQUWNVNU1vNU5YWlFHNjRoQUQzTkY0ZE9Bc0dPK0V2NEZCd2pkb055T0sKczZpbUt4cklMNURUWXVBeExHdHN6RlAwTWpIYjNJQnBuWHNKQmUxeFpuTFlYeWRWZ25ucTQ5QzJ6MDhPblJKeApxUUxXa0Z4WUlCWVRYTjZVODZiZm94VFBPU3VyMjRlanlFTDYxR3hBRi9NN0FmaWRJMDFTV1J2cGkxWVB0MG85ClU5VStqV1RHOFpOZExTMmlrOEphVTlqb1I4VUUwdHR0bzBVcHovQk54YzFYRHkvU3JjbzdGY2loOU9iMU1HdmQKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"' OR '1'='1","name":"key-4PRbfymiEhdbKdX"}
- pathParams: {"clientId":"eb9ae18a-a6ed-4b12-b9a0-cc9177a4ba95"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 28. BffClientContractTest::createKey::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists","correlationId":"083a4e69-fe57-44dd-b0af-600896768d2f","errors":[{"code":"006-0017","detail":"Key with kid gPprYV0AU4qUmVeKwgVNH2h3utYQdHEVoMWDDA8-X38 already exists"}]}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtUzJNQ1NGdEJNbEd4eUsvUFNsNApQQ05XT1E2WW0yUndvNFVyYlJlenh1ZU5STHBYMzNxdGRSQmxRZG1ZbHNTSUQ4K0g4ZXJVMlQxcC9iYm9nSEFXCkNhOWlGZnRrd0FPbnVIcGxzYUhFeDVQUWNVNU1vNU5YWlFHNjRoQUQzTkY0ZE9Bc0dPK0V2NEZCd2pkb055T0sKczZpbUt4cklMNURUWXVBeExHdHN6RlAwTWpIYjNJQnBuWHNKQmUxeFpuTFlYeWRWZ25ucTQ5QzJ6MDhPblJKeApxUUxXa0Z4WUlCWVRYTjZVODZiZm94VFBPU3VyMjRlanlFTDYxR3hBRi9NN0FmaWRJMDFTV1J2cGkxWVB0MG85ClU5VStqV1RHOFpOZExTMmlrOEphVTlqb1I4VUUwdHR0bzBVcHovQk54YzFYRHkvU3JjbzdGY2loOU9iMU1HdmQKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"RS256","name":"' OR '1'='1"}
- pathParams: {"clientId":"cb07de91-e7f3-49e2-b242-bb45de42acae"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 29. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_SQL_INJECTION @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Consultare la richiesta e la risposta dello scenario su /alg e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF0RzI1YjY0eUV2N0haVnFCbkx0YQpvajV1cjhoMi9pTEpDeElXeHJIanJaY2JNRVM0aGNLUm1SOENRdW9vSUM5S2I4LzhscXBmUHluWlQ5NEVqSVIvCjdGRWkyN3Y4TTdIaG5JZTY4SjZMTXVDUWdQVE1laldpbk1NWDRWM1pxMmg5Q2ZVcXZUSUdHY01yZlgvYmlGZ0MKeHlNTlRYR2JVSVVLL3IweXpkbURDeUp3cGZxenIxYVdiWGZaRVVvVDZOU1BSZ0lEZHJPQldUUjZOTlIwR3dkSwoxVWt1dWJnVnRZcW9zNE1TN1V3T0Z4WnBWZWgrWVJGZW9UTm1sb1BNbW50aTcvbTlQMkFZcUVPUnRYeFRYUzBECldUTHBpK3lZbmhWRTFuNHg4dlE4RjBTUENGaWh5VlhLdmtlNy9LQlhGSGZLcHowL2FEMGNaUUlhWldibVRlV1cKdFFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"' OR '1'='1","name":"producer-key-EsUbZbT8ltBorm1"}
- pathParams: {"producerKeychainId":"30fdad93-16c4-44f8-9222-5541be6ec3a2"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 30. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzb05VK2pEWjRTWnZBOHRpVVR1cApwM0h0QzIrN3g5a0h2OUwzRW9zaE5FaDBPOXVGVUdvSGxmemdsOGxDb2I0WW0xb2lHWTU1ODJMMXpKSy9Ic0p2CmJxeGwvM2o4aXc0Yy9wZDBHWDlWTERFV09EREptN0hWMUpvSzJCQUJNazJKWk12Ty85MUFweWE1b1RibVJCVXgKTWJHNnNneXdoV3ZHK2s2cmJSVXZvUFV4L1I2NjF1MlY1WGFjcGtvajc4Sk1GNFV0bmhRcExwSWc2d3J3Q2ZHagphdk1qZE1FUzZHUFhtTXBUVEgxQjZ0aTNlSGJoUFFyT1QyU2ZsUktVS1pyZ24xRXppQTRTWENJc25pQk5ndlQzCnJ4TWtkRXhGUkxnNXhUbjhYY0l0NG9uM081SGZHMjdTWGdqSVFOWG5pQ0ppOWFJMVNRbWpSbzVjdmJSd2E3R1IKYXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"RS256","name":"' OR '1'='1"}
- pathParams: {"producerKeychainId":"18f73cb8-5369-43b7-83a8-d2b4f8a9c7bb"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 31. BffProducerKeychainContractTest::createProducerKeychain::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffProducerKeychainContractTest::createProducerKeychain su POST /producerKeychains. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"06bd009c-0239-4221-8edf-ffabf6ab1768"}
- payload: {"name":"' OR '1'='1","description":"description-1MmcxqqflD2XunjAtiQnfP6E0Xd883VQ","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 32. BffProducerKeychainContractTest::createProducerKeychain::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffProducerKeychainContractTest::createProducerKeychain su POST /producerKeychains. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"fdf5d9ea-c3cb-4e4a-b9ab-309dbbe47699"}
- payload: {"name":"producer-keychain-4vdpDmiFDzENQtIcGUlGcHLT","description":"' OR '1'='1","members":["5d717b97-5308-49a5-8682-187663278f24"]}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 33. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurpose su POST /purposes. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4a996b1c-6a2e-437d-9428-86f49d215f24","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"d6467a6d-cd58-400b-8150-acc51ed257cb","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-PficMgK1nKjPRtd","description":"description-T8nPwvMTsX4jP1c","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 34. BffPurposeContractTest::createPurpose::[payload] REMOVED @ /freeOfChargeReason

**RESULT**

La motivazione è omessa mentre l'indicatore di gratuità è true. Il BFF restituisce correttamente HTTP 400 per motivazione mancante. La suite attende 200 perché valuta soltanto l'opzionalità strutturale del campo. Occorre considerare la dipendenza fra gratuità e motivazione nell'aspettativa dello scenario. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeContractTest::createPurpose su POST /purposes. Rimuovere esclusivamente /freeOfChargeReason dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /freeOfChargeReason
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Missing free of charge reason","status":400,"detail":"Missing free of charge reason","correlationId":"a423856b-0182-46cc-af79-4366d1b03a91","errors":[{"code":"004-0013","detail":"Missing free of charge reason"}]}
- payload: {"eserviceId":"fc9c2a12-0f9b-4718-8e36-4fd338dc50fa","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-X7YrFjVKrDaI65Z","description":"description-lz9NobkK9PoZM3f","isFreeOfCharge":true,"dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 35. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /riskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurpose su POST /purposes. Inviare un null JSON esplicito in /riskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /riskAnalysisForm
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bb0ed1ea-844e-426b-85b7-7d00bb5f656e","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"eserviceId":"231e6dee-457c-4797-b840-e95be9d964d9","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":null,"title":"purpose-kCDVfpfJRd0khap","description":"description-088yeqFr6hVYdbg","isFreeOfCharge":true,"freeOfChargeReason":"reason-EDYVSk2u9RqElhl","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 36. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NIL_UUID @ /consumerId

**RESULT**

Il chiamante opera per il proprio ente, mentre consumerId è sostituito con l'UUID nil. Il BFF rifiuta la richiesta con HTTP 403 e codice 004-0008 perché il chiamante non è autorizzato a operare come fruitore per l'identificativo richiesto. Il controllo di autorizzazione precede il lookup. La suite deve attendere 403 per questo specifico scenario, invece di 404. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /consumerId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /consumerId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 403
- responseBody: {"type":"about:blank","title":"Tenant not allowed","status":403,"detail":"Tenant 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 is not allowed to perform the operation because is not operating as consumer","correlationId":"0be47fc0-b8bf-4509-b90e-933f409ec8ae","errors":[{"code":"004-0008","detail":"Tenant 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 is not allowed to perform the operation because is not operating as consumer"}]}
- payload: {"eserviceId":"03d360aa-6475-4a49-a0d5-f9d97ef609cb","consumerId":"00000000-0000-0000-0000-000000000000","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-53vDHXUixGg9lE2","description":"description-inkN6OlgXZqtT1I","isFreeOfCharge":true,"freeOfChargeReason":"reason-AFPNUCkB9HYfing","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <403>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 37. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurpose su POST /purposes. Consultare la richiesta e la risposta dello scenario su /riskAnalysisForm/answers/institutionalPurpose/0 e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /riskAnalysisForm/answers/institutionalPurpose/0
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"6f7ed4dc-0227-45b2-8f1d-fa0bcf961f20"}
- payload: {"eserviceId":"3765d6c2-8c14-4a12-b04d-712e8715aae0","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["' OR '1'='1"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-w97f3yfsNyOcO5w","description":"description-3s1whfSH9zGyPZN","isFreeOfCharge":true,"freeOfChargeReason":"reason-qCRMy7o4dI7YYwO","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 38. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /title

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurpose su POST /purposes. Consultare la richiesta e la risposta dello scenario su /title e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /title
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"8e7b5726-84d0-431b-8ae7-6644f947ac86"}
- payload: {"eserviceId":"11f2c3f8-e3f3-4264-a639-990caa21e7c0","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"' OR '1'='1","description":"description-WkaajtF9sLoaMtJ","isFreeOfCharge":true,"freeOfChargeReason":"reason-rsJskBZ8jBG1lM9","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 39. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurpose su POST /purposes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"aa7f0992-d545-470a-98fd-6c75732ff989"}
- payload: {"eserviceId":"2f314580-0306-4501-844a-aff1a7a669f2","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-QYqobj3Ksv0srAU","description":"' OR '1'='1","isFreeOfCharge":true,"freeOfChargeReason":"reason-p0mIHxdKcdeqAnh","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 40. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurpose su POST /purposes. Consultare la richiesta e la risposta dello scenario su /freeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"12bf085a-8212-49d4-85ce-fa58a16fe7c0"}
- payload: {"eserviceId":"1be7f098-1645-4fe2-a42e-f3a3f87c5466","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-AzVqJlWhuSj12U8","description":"description-HB6cpmrx5KMg9rc","isFreeOfCharge":true,"freeOfChargeReason":"' OR '1'='1","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 41. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"e3c3d4d2-2f78-4bf9-a43b-97d51871d6a7","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"9715539a-0bb9-479a-843d-a0f680068979","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"4f86d8e1-7e3b-4dd7-bb1c-b0632a05843b","title":"purpose-xM5f02NVEe2ZDab","description":"description-quAtI3JYGi0Jj3M","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 42. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REMOVED @ /freeOfChargeReason

**RESULT**

La motivazione è omessa mentre l'indicatore di gratuità è true. Il BFF restituisce correttamente HTTP 400 per motivazione mancante. La suite attende 200 perché valuta soltanto l'opzionalità strutturale del campo. Occorre considerare la dipendenza fra gratuità e motivazione nell'aspettativa dello scenario. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Rimuovere esclusivamente /freeOfChargeReason dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /freeOfChargeReason
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Missing free of charge reason","status":400,"detail":"Missing free of charge reason","correlationId":"30058058-37e9-42da-a326-66d2c3a0abee","errors":[{"code":"004-0013","detail":"Missing free of charge reason"}]}
- payload: {"eserviceId":"b8bc03cc-b22e-4645-a9e5-3764b929e2a9","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"deeb5c48-9733-4b48-ba59-a800f95629cb","title":"purpose-MkLJTW5iMl1etcs","description":"description-RolmBnsh08VLJwZ","isFreeOfCharge":true,"dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 43. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /consumerId

**RESULT**

Il chiamante opera per il proprio ente, mentre consumerId è sostituito con l'UUID nil. Il BFF rifiuta la richiesta con HTTP 403 e codice 004-0008 perché il chiamante non è autorizzato a operare come fruitore per l'identificativo richiesto. Il controllo di autorizzazione precede il lookup. La suite deve attendere 403 per questo specifico scenario, invece di 404. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /consumerId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NIL_UUID
- targetPath: /consumerId
- mutationKind: REPLACE
- mutationValue: 00000000-0000-0000-0000-000000000000
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 403
- responseBody: {"type":"about:blank","title":"Tenant not allowed","status":403,"detail":"Tenant 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 is not allowed to perform the operation because is not operating as consumer","correlationId":"45c70987-8ea3-48b1-b259-2d488d4dd570","errors":[{"code":"004-0008","detail":"Tenant 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 is not allowed to perform the operation because is not operating as consumer"}]}
- payload: {"eserviceId":"905b1a39-886b-4d78-a995-65ad6b80c89f","consumerId":"00000000-0000-0000-0000-000000000000","riskAnalysisId":"6ab06416-5a12-4f74-9d91-d61afb5c6f52","title":"purpose-VH1w5HAouwiNjxk","description":"description-r3sUwh1ordlFYi8","isFreeOfCharge":true,"freeOfChargeReason":"reason-MgCfcqlCZlEnxEw","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <404> but was <403>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:75)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 44. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_SQL_INJECTION @ /title

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Consultare la richiesta e la risposta dello scenario su /title e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /title
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"b69ad9a9-dfd1-46ab-95c6-32c3c72e2265"}
- payload: {"eserviceId":"a70f9e94-37dc-46f3-8421-dfa0d214b0a8","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"8e3a07e1-4693-485c-bd3b-6ea0c8e415b8","title":"' OR '1'='1","description":"description-YOBbxr2Ky8Aplfc","isFreeOfCharge":true,"freeOfChargeReason":"reason-2xGDohdyvY2Uuyc","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 45. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"9a716fbb-f409-4331-915f-2d225b3ff9a9"}
- payload: {"eserviceId":"e88ab420-be4a-4af7-a910-d2cb620e5bfe","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"106391d8-f8b8-4143-81de-26f5fb31b5b4","title":"purpose-gAdAM5UrGGMXAaH","description":"' OR '1'='1","isFreeOfCharge":true,"freeOfChargeReason":"reason-sUf2ZEmoiMRECtS","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 46. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_SQL_INJECTION @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Consultare la richiesta e la risposta dello scenario su /freeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"fd7e16df-ed71-4cff-aaae-9f4fd6713713"}
- payload: {"eserviceId":"503a2106-9507-487f-870a-16f4a610ef4f","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"286fa6ad-cc70-40a8-ba52-66e4fe09d22c","title":"purpose-mlBDVzyVOO348ar","description":"description-RfMeovMbi6MmRd6","isFreeOfCharge":true,"freeOfChargeReason":"' OR '1'='1","dailyCalls":1}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 47. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeRiskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Inviare un null JSON esplicito in /purposeRiskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /purposeRiskAnalysisForm
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"31017cf8-1a9a-4bc1-b249-0b228c861be8","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-zlg2caX01AqIjnQFHXIr","targetTenantKind":"PA","purposeTitle":"purpose-template-ZbOteZcHoGFs4Zc","purposeDescription":"purpose-description-vrq5Fa3IcOVacx4J77aY","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-pewdcXKr0YwRI9yebZ5r","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 48. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 201. La proprietà rimossa è facoltativa e la creazione del template riesce con 201 Created, come previsto dal contratto. Il KO deriva dal default 200 applicato dalla suite e va corretto nella policy di successo dell'endpoint.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18965

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 201
- responseBody: {"id":"b5276327-cbfd-423c-82d8-da47db5dbde5"}
- payload: {"targetDescription":"target-description-OlGq5UbHL1kFWcgAFrIX","targetTenantKind":"PA","purposeTitle":"purpose-template-UW8C0tcHHc09bW3","purposeDescription":"purpose-description-GVvuhg7kI20DHaojdRAc","purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-KA4SX5C3rRPPNVKaaehf","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 49. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"4192d1cc-3048-40c7-9b9a-eb005f53918b"}
- payload: {"targetDescription":"target-description-IhKVhbfkeCtyAucrfmDK","targetTenantKind":"PA","purposeTitle":"purpose-template-SWOPX19ABR3rPa4","purposeDescription":"purpose-description-aYGu75JDXUYkhgUjPgwy","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-W2XiQ0r6nfYI8WAMUoPR","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 50. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18900

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"27347923-0ef2-413a-9554-7cf70e50838d"}
- payload: {"targetDescription":"target-description-I8hEy8USxfhQrx47HU3Y","targetTenantKind":"PA","purposeTitle":"purpose-template-pxomD65aYA5rCPe","purposeDescription":"purpose-description-xYrYIZHAqJtFTEnkTZqw","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-woujWhELowceddBARU9w","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 51. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/purpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/purpose/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"b58f3163-b76e-4e6a-82d6-6b0461003d5a"}
- payload: {"targetDescription":"target-description-aW0k8D0DQ3fJEV1PozD6","targetTenantKind":"PA","purposeTitle":"purpose-template-pF3zvtRGSgOeFD4","purposeDescription":"purpose-description-76fcd3QFJzq6sq8KnGOC","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-eMGEIFewwptDSncyzUpL","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 52. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/usesPersonalData/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"39d2bc5b-e5fb-46f3-8655-a351b5fa9a4a"}
- payload: {"targetDescription":"target-description-EKvbTgbncYbA2dBhfi18","targetTenantKind":"PA","purposeTitle":"purpose-template-x45tLbIZ9DyzooA","purposeDescription":"purpose-description-qf4bjAiuqcfl0f5plYf9","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-srfzDRDB7G47ta3wN9S0","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 53. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"301d0a6d-553a-4cc3-97d0-a6c1b07385fc"}
- payload: {"targetDescription":"target-description-fbsK25nfC1ldZw1lK2jC","targetTenantKind":"PA","purposeTitle":"purpose-template-yujHv6aJ3AqbMBX","purposeDescription":"purpose-description-qYnlJlJ3i8k48ML79CF7","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-wWalG9vXERj7ZEzQ86RQ","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 54. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeDailyCalls. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Inviare un null JSON esplicito in /purposeDailyCalls, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /purposeDailyCalls
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"13a1cff4-5d97-4515-8354-d608eeeb1baa","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-x3reQb6JZbsEvNyeT2hc","targetTenantKind":"PA","purposeTitle":"purpose-template-n5ciluhSR8aeD0Q","purposeDescription":"purpose-description-iG66VYR9FQlQnVuSGwuX","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-iD5CkVq7ganqDLqYWFgM","purposeDailyCalls":null,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 55. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 201. La proprietà rimossa è facoltativa e la creazione del template riesce con 201 Created, come previsto dal contratto. Il KO deriva dal default 200 applicato dalla suite e va corretto nella policy di successo dell'endpoint.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeDailyCalls dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18965

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeDailyCalls
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 201
- responseBody: {"id":"0338d250-a8e7-49e6-97e6-74a0df180343"}
- payload: {"targetDescription":"target-description-SWW82H2FTehjHo8MaruM","targetTenantKind":"PA","purposeTitle":"purpose-template-TqJoLMbSiiK6Hg9","purposeDescription":"purpose-description-pToP7DuIm19XSw0ULpft","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-VtiSJ5JDOanc76FeGI9K","handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 56. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /targetDescription

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Consultare la richiesta e la risposta dello scenario su /targetDescription e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /targetDescription
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"f525e13a-041c-478d-8da5-6495621f0278"}
- payload: {"targetDescription":"' OR '1'='1","targetTenantKind":"PA","purposeTitle":"purpose-template-lkUWPIpASatBWjD","purposeDescription":"purpose-description-Iz7hddvJorrwbS1NXNco","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-TfSKF8cX7vA0jRjR2ioJ","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 57. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeTitle

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Consultare la richiesta e la risposta dello scenario su /purposeTitle e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeTitle
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Purpose Template title conflict","status":409,"detail":"Purpose Template title conflict for title ' OR '1'='1 and IDs 338e476d-8f19-4fda-93e1-67e8e8f9102d","correlationId":"70118aa7-0fb4-4479-a224-24583c5f14eb","errors":[{"code":"015-0002","detail":"Purpose Template title conflict for title ' OR '1'='1 and IDs 338e476d-8f19-4fda-93e1-67e8e8f9102d"}]}
- payload: {"targetDescription":"target-description-QBoXFH2psq1v70dEmknx","targetTenantKind":"PA","purposeTitle":"' OR '1'='1","purposeDescription":"purpose-description-Qg4SolHO0lS0yNtybr3o","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-dmZ3joqwquSwjnr4wzn3","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 58. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeDescription

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Consultare la richiesta e la risposta dello scenario su /purposeDescription e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeDescription
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"0353f542-cb85-4db5-b208-3ca51201c15c"}
- payload: {"targetDescription":"target-description-3zxXvATEuNs4wQu4Khqg","targetTenantKind":"PA","purposeTitle":"purpose-template-JspJIJNvLiWmuGi","purposeDescription":"' OR '1'='1","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-I128PWgzKHMYGPSFtNsM","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 59. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/version

**RESULT**

Il validatore rifiuta correttamente la versione del questionario composta da spazi con HTTP 400 e codice 015-0004. La suite attende 200 perché considera soltanto tipo e lunghezza della stringa. L'aspettativa deve includere la validità semantica della versione. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeRiskAnalysisForm/version con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /purposeRiskAnalysisForm/version
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Risk analysis template validation failed","status":400,"detail":"Risk analysis template validation failed. Reasons: Error: Unexpected ruleset version    ","correlationId":"3fd1d3b7-b3ed-4fec-9b72-6e9c0d186cd0","errors":[{"code":"015-0004","detail":"Risk analysis template validation failed. Reasons: Error: Unexpected ruleset version    "}]}
- payload: {"targetDescription":"target-description-CjEfESE4RFnvXc0xha5f","targetTenantKind":"PA","purposeTitle":"purpose-template-RSJREsw66Lt1QOP","purposeDescription":"purpose-description-32GXAX57sI94HpkUm7XV","purposeRiskAnalysisForm":{"version":"   ","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-eiZO40Zs0Dt1si7Bx40s","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 60. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Consultare la richiesta e la risposta dello scenario su /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"504f8180-bd17-4afb-ba85-223296882107"}
- payload: {"targetDescription":"target-description-q4PvSfmqWyLBUH14pKoj","targetTenantKind":"PA","purposeTitle":"purpose-template-bvrMVAHNHnB7eKv","purposeDescription":"purpose-description-1PSyKTlbnLYeZlwthNBm","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["' OR '1'='1"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-4VVla4oDBkeaV7MnBm6n","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 61. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Consultare la richiesta e la risposta dello scenario su /purposeFreeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 201
- responseBody: {"id":"3c029a94-fccd-4cf5-87e8-7d4c0630dfa4"}
- payload: {"targetDescription":"target-description-LSxqbcGEfrNfKIf6QCbv","targetTenantKind":"PA","purposeTitle":"purpose-template-FWBGo26fqsadzlo","purposeDescription":"purpose-description-oXGSxcw23YEK9BIXfcV2","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"' OR '1'='1","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <201>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 62. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"e4dfaf75-4075-46e6-bebb-cd3575a11c76","kind":"VERIFIED","description":"' OR '1'='1","name":"name-6DL9jNdFDL6Ja1aS7vHMjcbe","creationTime":"2026-11-17T21:19:22.275Z"}
- payload: {"description":"' OR '1'='1","name":"name-6DL9jNdFDL6Ja1aS7vHMjcbe"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 63. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicate attribute name","status":409,"detail":"An attribute with name ' OR '1'='1 already exists","correlationId":"4069f7be-3f62-4351-9638-b497de1eb13e","errors":[{"code":"003-0002","detail":"An attribute with name ' OR '1'='1 already exists"}]}
- payload: {"description":"description-RvT00XmB8Xe3kp1GXDr6LSZpVLylBjsU","name":"' OR '1'='1"}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 64. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_SQL_INJECTION @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 404. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Consultare la richiesta e la risposta dello scenario su /rejectionReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"' OR '1'='1"}
- pathParams: {"delegationId":"18763dfb-164f-4ac2-a54d-694abc15acbf"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 15 more

````


---

### 65. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_SQL_INJECTION @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 404. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Consultare la richiesta e la risposta dello scenario su /rejectionReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /rejectionReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"' OR '1'='1"}
- pathParams: {"delegationId":"8a7f4893-d1d3-40fe-937d-66b8054f6836"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 66. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isSignalHubEnabled
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bc090248-3ffc-4ecd-991f-10ff8f26e39a","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-ygEEiCSvqjhMoXq","description":"description-4xYHtGYxm83vrZGoo2ZP","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"eb12a619-b3e7-4cca-a539-9cf139d7b8e2"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 67. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isConsumerDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isConsumerDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isConsumerDelegable
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b57f00a3-647b-4af5-bc06-3775f2795609","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-AEw9b1okqZPLmyv","description":"description-bmnOvX4lKPrMSOzcli67","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"06c0553d-2963-4bec-b6ac-949e688443cb"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 68. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isClientAccessDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isClientAccessDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /isClientAccessDelegable
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ee6c6939-0054-43c2-be87-4049605b81f9","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-Qf8UGAYu3xgmkHh","description":"description-1VFgv09zis7vdL3RbbWx","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"2d99590d-2969-4338-b125-2c4b5ee7aea2"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 69. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /personalData
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"e208d4ba-65eb-452d-ad53-317ddc5dc1fe","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-enKL8DCrDkJXc8m","description":"description-ZALhovm0Htpn1cs20DKS","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
- pathParams: {"eServiceId":"822c962f-6e06-4b0f-a35b-bcb178842fa9"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 70. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /asyncExchange
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4985b522-ec95-4cae-ad9a-e011062d2c15","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-PBR2AFfLKrc7DGv","description":"description-odLgLBeEUgpwhZhWP6rh","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
- pathParams: {"eServiceId":"3a096758-7dca-4dc2-8418-946126cbbad5"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 71. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"EService template name conflict","status":409,"detail":"An EService template with name ' OR '1'='1 already exists","correlationId":"54c43a54-4d20-4348-b96f-8265b71e9311","errors":[{"code":"001-0039","detail":"An EService template with name ' OR '1'='1 already exists"}]}
- payload: {"name":"' OR '1'='1","description":"description-VZ731YE2nZkb8jpKm3rx","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"360069c0-f386-4b3e-b686-68ccefaff8e4"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 72. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"352a47e5-c4eb-497a-a1e6-ac604a0e994b"}
- payload: {"name":"eservice-KOlGJLlYSPr8MVP","description":"' OR '1'='1","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"352a47e5-c4eb-497a-a1e6-ac604a0e994b"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 73. BffEServiceContractTest::updateEServiceDescription::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::updateEServiceDescription su POST /eservices/{eServiceId}/description/update. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"d88f2896-23c6-4c48-954b-00bc3bedae07"}
- payload: {"description":"' OR '1'='1"}
- pathParams: {"eServiceId":"d88f2896-23c6-4c48-954b-00bc3bedae07"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 74. BffEServiceContractTest::updateEServiceName::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceContractTest::updateEServiceName su POST /eservices/{eServiceId}/name/update. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"EService template name conflict","status":409,"detail":"An EService template with name ' OR '1'='1 already exists","correlationId":"0bef660c-635e-402d-9ee3-2452de29090b","errors":[{"code":"001-0039","detail":"An EService template with name ' OR '1'='1 already exists"}]}
- payload: {"name":"' OR '1'='1"}
- pathParams: {"eServiceId":"1f02b8d7-f1c7-42ee-8595-d44897f3e60f"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 75. BffEServiceTemplateContractTest::updateEServiceTemplateDescription::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::updateEServiceTemplateDescription su POST /eservices/templates/{eServiceTemplateId}/description/update. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"description":"' OR '1'='1"}
- pathParams: {"eServiceTemplateId":"09004be8-3bbc-4dbb-b6f6-e650fcb2c316"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 76. BffEServiceTemplateContractTest::updateEServiceTemplateIntendedTarget::[payload] REPLACED_WITH_SQL_INJECTION @ /intendedTarget

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::updateEServiceTemplateIntendedTarget su POST /eservices/templates/{eServiceTemplateId}/intendedTarget/update. Consultare la richiesta e la risposta dello scenario su /intendedTarget e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /intendedTarget
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"intendedTarget":"' OR '1'='1"}
- pathParams: {"eServiceTemplateId":"7bb8de4c-b36c-41d0-8313-803124662a96"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 77. BffEServiceTemplateContractTest::updateEServiceTemplateName::[payload] REPLACED_WITH_SQL_INJECTION @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffEServiceTemplateContractTest::updateEServiceTemplateName su POST /eservices/templates/{eServiceTemplateId}/name/update. Consultare la richiesta e la risposta dello scenario su /name e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /name
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Duplicated service name","status":409,"detail":"An EService Template with name ' OR '1'='1 already exists","correlationId":"5c46a76b-9e93-4eba-b6e5-663e20b5e185","errors":[{"code":"011-0004","detail":"An EService Template with name ' OR '1'='1 already exists"}]}
- payload: {"name":"' OR '1'='1"}
- pathParams: {"eServiceTemplateId":"e523af8c-b9c8-4d70-8739-941dce19863f"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 78. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"5202974f-e15e-4839-91cf-9800c5c9c8ac","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-3aA9XiT1qhh8L71","description":"description-xoUzyJvHvfNVUov","isFreeOfCharge":true,"freeOfChargeReason":null,"riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"8d8047fd-e68f-4627-aa8b-55cb9753cb52"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 79. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /riskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Inviare un null JSON esplicito in /riskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /riskAnalysisForm
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6fd00845-42bf-47e1-8e31-057b4843920c","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"title":"purpose-pccqjf6ki2EFosh","description":"description-l7Q7tlLSKLE6d9R","isFreeOfCharge":true,"freeOfChargeReason":"reason-FhjvcPX4nYIL9ik","riskAnalysisForm":null,"dailyCalls":1}
- pathParams: {"purposeId":"379712f7-cdce-4380-81ef-e84d940c3f87"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 80. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/institutionalPurpose

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/institutionalPurpose in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/institutionalPurpose
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"86e88ff1-31bd-41e1-aafa-cb921240a751","versionId":"2f4780d8-afec-4da3-b5fd-b5992f0b5d8f"}
- payload: {"title":"purpose-wsfV0fgtxsZ75Q8","description":"description-5Rq2TaA0hkqrhFv","isFreeOfCharge":true,"freeOfChargeReason":"reason-vaOUFHTqWSsO1xA","riskAnalysisForm":{"version":"3.2","answers":{"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"86e88ff1-31bd-41e1-aafa-cb921240a751"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 81. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/institutionalPurpose/0 in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/institutionalPurpose/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"a57e60f9-1a9e-4388-827a-19890ac97187","versionId":"c29831b7-c6e6-45c9-9f8e-aa65490b6e86"}
- payload: {"title":"purpose-hhNybhbqd8y15MO","description":"description-qbZGTyJP7xSC8yZ","isFreeOfCharge":true,"freeOfChargeReason":"reason-gmgIugvGkt6VuIh","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":[],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"a57e60f9-1a9e-4388-827a-19890ac97187"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 82. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"c5c4a438-b1ac-48bc-9b77-2e2445e5aa20","versionId":"1e7d804e-fd1a-47eb-9da2-f92251dfe984"}
- payload: {"title":"purpose-K4XAL6ZMcVK4cSh","description":"description-Ph6KB2Ui7E0qECI","isFreeOfCharge":true,"freeOfChargeReason":"reason-YnBA1xi2Le38nZp","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"c5c4a438-b1ac-48bc-9b77-2e2445e5aa20"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 83. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/0

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/0 in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"671fe5fa-5343-423e-841c-829ece9ec9b2","versionId":"f26bc427-59c8-4256-8b2a-7d683e1a7536"}
- payload: {"title":"purpose-QrlxA3xBKTLgWn2","description":"description-OSG6dkJ0CQZKDIf","isFreeOfCharge":true,"freeOfChargeReason":"reason-si9KXflNqxcUNiP","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":[],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"671fe5fa-5343-423e-841c-829ece9ec9b2"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 84. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/usesThirdPartyPersonalData

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/usesThirdPartyPersonalData in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/usesThirdPartyPersonalData
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"0160b4cd-6e22-4e72-97e1-0561d58cbf6c","versionId":"a01d6075-b846-4625-b645-1677f6f42436"}
- payload: {"title":"purpose-UGPzLfPDZUx7DPA","description":"description-B80xP6aThV9ldaI","isFreeOfCharge":true,"freeOfChargeReason":"reason-XgBuIRrbgtzOgQc","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"0160b4cd-6e22-4e72-97e1-0561d58cbf6c"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 85. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/purpose

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/purpose in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/purpose
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"5afade2f-6a10-4c7e-9361-06f8a703591c","versionId":"e30a1f93-cb90-4026-8528-50a65780af93"}
- payload: {"title":"purpose-LDjEVEVw7XbxI85","description":"description-nRtMAyLeyE2yaTa","isFreeOfCharge":true,"freeOfChargeReason":"reason-TN0BBMFSSKg8GbR","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"5afade2f-6a10-4c7e-9361-06f8a703591c"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 86. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/usesThirdPartyPersonalData/0

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/usesThirdPartyPersonalData/0 in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/usesThirdPartyPersonalData/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"2f438261-5ebb-4ffe-9174-816a8039c33a","versionId":"0e0cb014-8ae2-472d-9f68-4ba13bb54fec"}
- payload: {"title":"purpose-lHWWglk73MgZZyr","description":"description-S0p685euE6appdP","isFreeOfCharge":true,"freeOfChargeReason":"reason-t9xZ1LE7F6LcGgG","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":[]}},"dailyCalls":1}
- pathParams: {"purposeId":"2f438261-5ebb-4ffe-9174-816a8039c33a"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 87. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/purpose/0

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/purpose/0 in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /riskAnalysisForm/answers/purpose/0
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"8f4fe047-28d7-47c8-a6ab-ccb120685e1c","versionId":"092c21b3-b148-4c5c-8fad-a7f7be5cc9b1"}
- payload: {"title":"purpose-IPevR4F3DoaYyzk","description":"description-53KaWniGoWkZLWR","isFreeOfCharge":true,"freeOfChargeReason":"reason-MGorjpcGfStDu4t","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":[],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"8f4fe047-28d7-47c8-a6ab-ccb120685e1c"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 88. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /title

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /title e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /title
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"c4ee258b-97da-4b9c-88de-986b3fdb49d3","versionId":"997c483e-57bc-4ef7-b6bd-9fdda603081b"}
- payload: {"title":"' OR '1'='1","description":"description-NAewle78C19d5Ia","isFreeOfCharge":true,"freeOfChargeReason":"reason-THycgqAGsialILP","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"c4ee258b-97da-4b9c-88de-986b3fdb49d3"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 89. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"af1c91f3-ff38-4d81-b68e-90df8e7c1df0","versionId":"c0d31346-57a3-457e-bf2d-c8a9639f141b"}
- payload: {"title":"purpose-ioQt8BpdXXAf2jM","description":"' OR '1'='1","isFreeOfCharge":true,"freeOfChargeReason":"reason-PLqmQbgOdn68VbL","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"af1c91f3-ff38-4d81-b68e-90df8e7c1df0"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 90. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /freeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"4d180212-1bd0-41c1-a3ec-fcb4d2fb1c35","versionId":"35b9e940-a4f1-435e-8ca4-71559ad729b6"}
- payload: {"title":"purpose-ad97x7HgSRP4jmc","description":"description-jifx5LVw6oPmqZP","isFreeOfCharge":true,"freeOfChargeReason":"' OR '1'='1","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"4d180212-1bd0-41c1-a3ec-fcb4d2fb1c35"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 91. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /riskAnalysisForm/answers/institutionalPurpose/0 e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /riskAnalysisForm/answers/institutionalPurpose/0
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"995044cf-b377-4652-b07e-5e9c6f087e15","versionId":"4b451047-d4d6-4cee-8e58-3520c9357e1c"}
- payload: {"title":"purpose-kycLhKFE46VVpHo","description":"description-E4SX5ZcffUUNTCU","isFreeOfCharge":true,"freeOfChargeReason":"reason-kDcqjXHFlPMIR6z","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["' OR '1'='1"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"995044cf-b377-4652-b07e-5e9c6f087e15"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 92. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeRiskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeRiskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /purposeRiskAnalysisForm
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"358b50bf-ff7e-43e9-8cf4-06b1ee75ea32","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-vB8eFoZF3ojmJTXP0JYx","targetTenantKind":"PA","purposeTitle":"purpose-template-byxi7z2odWaV7CQ","purposeDescription":"purpose-description-7raJ4fnoUZXMfCURUrKZ","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-ojPfEMHWMTlFQZEblevJ","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"d8e56d6a-0936-43e2-9a5e-fd6080c723eb"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 93. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"a27bb45b-1d12-497e-ac43-065eaaf903b5","targetDescription":"target-description-4kD78Fym3JirgtahpHCx","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:22:19.687Z","updatedAt":"2026-11-17T21:22:21.947Z","purposeTitle":"purpose-template-6x8VoBgcFB32DJ5","purposeDescription":"purpose-description-t18GeSjQwsJvimvSTLKP","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"7d9ab20a-eeb2-44bb-bca9-c536e89e559a","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"e8a00bea-9da9-49e5-b404-5c3c65d3d1b3","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"456ee923-0ee6-4232-ae5c-dd5d9a9f321c","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"21ce8ac3-10f3-4f0a-be40-250a8312eafd","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"f2852e87-49f4-4ed3-a6ad-cd0f8765fd18","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-dqMACLCVwz6Ts4Viacse","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-4kD78Fym3JirgtahpHCx","targetTenantKind":"PA","purposeTitle":"purpose-template-6x8VoBgcFB32DJ5","purposeDescription":"purpose-description-t18GeSjQwsJvimvSTLKP","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-dqMACLCVwz6Ts4Viacse","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"a27bb45b-1d12-497e-ac43-065eaaf903b5"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 94. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"8e417205-00c2-4741-b725-f726ac3a65a9","targetDescription":"target-description-hWeSPuip5S1GP4DBvMux","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:23:05.823Z","updatedAt":"2026-11-17T21:23:08.134Z","purposeTitle":"purpose-template-fLm01NzfRUpC7t3","purposeDescription":"purpose-description-FeLwW5LY3D47QkD17Ksb","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"c7761a6b-a021-4c97-ac65-70f46e3d5845","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"90fa563a-bfbd-4152-9282-bfd722c152b0","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"1a9ab04a-665e-4d66-bd0d-9684aaf0ba9a","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"ff7d1905-729d-45b6-87b8-52c102fd82dd","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"e473e7e0-684e-449d-a4fd-7e6b5b3bb0aa","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-C4mVIYY4VwPObvew8Wvu","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-hWeSPuip5S1GP4DBvMux","targetTenantKind":"PA","purposeTitle":"purpose-template-fLm01NzfRUpC7t3","purposeDescription":"purpose-description-FeLwW5LY3D47QkD17Ksb","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-C4mVIYY4VwPObvew8Wvu","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"8e417205-00c2-4741-b725-f726ac3a65a9"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 95. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/purpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/purpose/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"4ec0c00f-fd94-4103-ae97-4df96d2168cb","targetDescription":"target-description-HQE2uP2o42WOq6AUeLG5","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:23:50.801Z","updatedAt":"2026-11-17T21:23:51.735Z","purposeTitle":"purpose-template-yLO7do3Mi719BVu","purposeDescription":"purpose-description-TEFADvuG95VGJTNrj453","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"330e2959-20d8-425f-9c64-615e1ead3043","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"ac3da8f4-2e52-4599-ab24-7e3e49c22fde","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"49bcf928-48ae-4d97-9137-888b77d0b5d2","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"6efca63b-a98a-4d7d-8b88-ffc3a090064f","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"9b3fa668-f905-482f-a249-ebef608f577a","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-VVphTiXi935T44gAdv7k","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-HQE2uP2o42WOq6AUeLG5","targetTenantKind":"PA","purposeTitle":"purpose-template-yLO7do3Mi719BVu","purposeDescription":"purpose-description-TEFADvuG95VGJTNrj453","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-VVphTiXi935T44gAdv7k","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"4ec0c00f-fd94-4103-ae97-4df96d2168cb"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 96. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/usesPersonalData/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"7db32ca0-54ab-46ad-a066-6df23450ffc9","targetDescription":"target-description-3qmENd1csTFYqIQo4l7X","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:24:32.036Z","updatedAt":"2026-11-17T21:24:34.345Z","purposeTitle":"purpose-template-w5mjE4Ee92m9hGT","purposeDescription":"purpose-description-JjKglojb1NpI8NljA9JQ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"a962537f-7f5c-4165-8290-cec3e2b06cfc","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"69d21266-4777-417c-a2ed-3d2b45cefca0","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"8713b39b-4439-40e5-92b6-ab75e3989844","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"eff59203-3f2e-4f40-a4b4-07ba794fd133","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"01a65d45-e0b0-44af-8afa-0e5be04fefe6","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-J6eVicXWWlKtfKqG0hqS","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-3qmENd1csTFYqIQo4l7X","targetTenantKind":"PA","purposeTitle":"purpose-template-w5mjE4Ee92m9hGT","purposeDescription":"purpose-description-JjKglojb1NpI8NljA9JQ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-J6eVicXWWlKtfKqG0hqS","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"7db32ca0-54ab-46ad-a066-6df23450ffc9"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 97. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18966

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"4709bcbe-68eb-4c34-b44e-905eceeb67f8","targetDescription":"target-description-AaYnbSYYYIjSDsv8qLHI","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:25:13.899Z","updatedAt":"2026-11-17T21:25:16.225Z","purposeTitle":"purpose-template-fca58i0adhWN9n5","purposeDescription":"purpose-description-TEfP8Op5bcOsDJvvMUxK","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"0b59f0ad-acd0-412e-8253-76c29c702665","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"ec1421b4-383c-4c6d-8d85-7e96ba6d38df","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"7a551881-03a6-45ee-b141-4cfc1a52c0b6","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"f009ef2c-8f93-4d0e-af53-86c4482795ec","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"b182b079-9f3a-40a0-8d2e-d6efbc5e5b9c","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-1q6XqNZc7Fj5XzwQ19Lr","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-AaYnbSYYYIjSDsv8qLHI","targetTenantKind":"PA","purposeTitle":"purpose-template-fca58i0adhWN9n5","purposeDescription":"purpose-description-TEfP8Op5bcOsDJvvMUxK","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-1q6XqNZc7Fj5XzwQ19Lr","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"4709bcbe-68eb-4c34-b44e-905eceeb67f8"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 98. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeFreeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeFreeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"cb31762e-a953-46d1-947a-b1f6e9312bc1","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"purposeFreeOfChargeReason\""}]}
- payload: {"targetDescription":"target-description-eLUKUP325PSAx5w3BCAE","targetTenantKind":"PA","purposeTitle":"purpose-template-DUm0wMi6YkBatCu","purposeDescription":"purpose-description-vRjgvfwMVjKfVfTJDFG2","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":null,"purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"2954f646-869b-4352-aa89-01eb08699b11"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 99. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeFreeOfChargeReason

**RESULT**

La motivazione è omessa mentre l'indicatore di gratuità è true. Il BFF restituisce correttamente HTTP 400 per motivazione mancante. La suite attende 200 perché valuta soltanto l'opzionalità strutturale del campo. Occorre considerare la dipendenza fra gratuità e motivazione nell'aspettativa dello scenario. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeFreeOfChargeReason dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REMOVED
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REMOVE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Missing free of charge reason","status":400,"detail":"Missing free of charge reason","correlationId":"21386495-f409-4318-a65e-ed379f0d55f5","errors":[{"code":"015-0001","detail":"Missing free of charge reason"}]}
- payload: {"targetDescription":"target-description-KZ46pRpSChaUbo6JG6OT","targetTenantKind":"PA","purposeTitle":"purpose-template-lscyNT3FjiU4M5V","purposeDescription":"purpose-description-DJlhDcnSpze3GR7bpBR5","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"42d700e2-61bb-43e6-a65b-643d19ceb7e5"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 100. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeDailyCalls. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeDailyCalls, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /purposeDailyCalls
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c95dec06-78fa-4036-a96b-b0ad97922414","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-R2ydF6UUh6NBH2ymRTGu","targetTenantKind":"PA","purposeTitle":"purpose-template-WVlSiPTQm34ZWlz","purposeDescription":"purpose-description-9NuB3VL2ddllXA4gTx1l","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-Rb4VQIM2VmWJTlxElxo6","purposeDailyCalls":null,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"53e446b2-2cfe-4659-b4d5-82843a73f8b4"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 101. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /targetDescription

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Consultare la richiesta e la risposta dello scenario su /targetDescription e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /targetDescription
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"c64018f9-c206-4fcf-84d6-05863945323d","targetDescription":"' OR '1'='1","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:25:56.655Z","updatedAt":"2026-11-17T21:25:58.997Z","purposeTitle":"purpose-template-NCP3rO3IUPKIokd","purposeDescription":"purpose-description-PHBpf536s5Ld8mlRJOdw","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"39f8eca9-c04f-4236-9264-aec3beb93618","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"fb4407dd-e28f-46e4-8e5a-34b6b1775797","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"d9b72006-aeb4-40e7-91eb-c7978f29b115","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"4ab888ea-6b5a-4c51-b5b8-01cb68058c44","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"7ce33b1d-7eaa-42a0-9d9c-9d1efcc580a4","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-lcD04geTvXe2SseLKzOp","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"' OR '1'='1","targetTenantKind":"PA","purposeTitle":"purpose-template-NCP3rO3IUPKIokd","purposeDescription":"purpose-description-PHBpf536s5Ld8mlRJOdw","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-lcD04geTvXe2SseLKzOp","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"c64018f9-c206-4fcf-84d6-05863945323d"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 102. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeTitle

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Consultare la richiesta e la risposta dello scenario su /purposeTitle e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeTitle
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 409
- responseBody: {"type":"about:blank","title":"Purpose Template title conflict","status":409,"detail":"Purpose Template title conflict for title ' OR '1'='1 and IDs 338e476d-8f19-4fda-93e1-67e8e8f9102d","correlationId":"bf0151f1-45d9-45ad-9284-4691a3554519","errors":[{"code":"015-0002","detail":"Purpose Template title conflict for title ' OR '1'='1 and IDs 338e476d-8f19-4fda-93e1-67e8e8f9102d"}]}
- payload: {"targetDescription":"target-description-UqTCLnaCjpt83r9ZOK6A","targetTenantKind":"PA","purposeTitle":"' OR '1'='1","purposeDescription":"purpose-description-DKms6vtymJJxTj09eYOA","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-DjmPhTAaYoqWWqGalGSW","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"7f67241f-3112-4906-b88c-38149afa6e43"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <409>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 103. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeDescription

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Consultare la richiesta e la risposta dello scenario su /purposeDescription e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeDescription
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"983b0029-e606-44c1-95ec-59dcfbd20c6f","targetDescription":"target-description-4Wj3i40zD84YOSfNRWqC","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:26:26.838Z","updatedAt":"2026-11-17T21:26:29.102Z","purposeTitle":"purpose-template-atl7WgcZvJC3DoA","purposeDescription":"' OR '1'='1","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"2cb61bee-631f-434c-992c-1127154095b5","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"5cde79db-af81-4e39-b9a8-a0251c8c5e23","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"4c92c30d-73a7-4373-90f4-e4c4045b23e6","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"4c7dabb2-df7b-4706-93c2-6cc682b4ef41","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"5f5ad3bd-0ac8-4ed5-812b-f6875cb06b3b","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-XvCEaCM6aFuamXo6ypFD","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-4Wj3i40zD84YOSfNRWqC","targetTenantKind":"PA","purposeTitle":"purpose-template-atl7WgcZvJC3DoA","purposeDescription":"' OR '1'='1","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-XvCEaCM6aFuamXo6ypFD","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"983b0029-e606-44c1-95ec-59dcfbd20c6f"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 104. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/version

**RESULT**

Il validatore rifiuta correttamente la versione del questionario composta da spazi con HTTP 400 e codice 015-0004. La suite attende 200 perché considera soltanto tipo e lunghezza della stringa. L'aspettativa deve includere la validità semantica della versione. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Sostituire /purposeRiskAnalysisForm/version con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_BLANK_STRING
- targetPath: /purposeRiskAnalysisForm/version
- mutationKind: REPLACE
- mutationValue:    
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Risk analysis template validation failed","status":400,"detail":"Risk analysis template validation failed. Reasons: Error: Unexpected ruleset version    ","correlationId":"bedf51b3-66cd-4078-9611-691eacb4271d","errors":[{"code":"015-0004","detail":"Risk analysis template validation failed. Reasons: Error: Unexpected ruleset version    "}]}
- payload: {"targetDescription":"target-description-MjRQlczobFuJYXVd7DU8","targetTenantKind":"PA","purposeTitle":"purpose-template-LfHP0Eq1gjaTpWS","purposeDescription":"purpose-description-LeSVRvZNbzHozvCPE1d3","purposeRiskAnalysisForm":{"version":"   ","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-gH2gxOTL8UBfk9J9VpIk","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"3dc609f6-7e5a-4f6b-8761-808cf3e8d498"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 105. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Consultare la richiesta e la risposta dello scenario su /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"4da2a697-f024-4a8c-8209-ea29e51f9e6d","targetDescription":"target-description-CMcU3djmzAiJ4CBNfQuj","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:26:58.343Z","updatedAt":"2026-11-17T21:27:00.648Z","purposeTitle":"purpose-template-m9AJHTVyCVXoKTG","purposeDescription":"purpose-description-bPWPn8TiwzefQQ9M5Prg","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"cbc68009-8af6-4d6b-ab29-7714d030fd06","values":[],"editable":false,"suggestedValues":["' OR '1'='1"]},"isRequestOnBehalfOfThirdParties":{"id":"fa8da315-6064-46c3-bb00-e5d32a043d4c","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"77046dce-7d59-43e5-8a58-5ca1f697b5de","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"57e34ea8-a3fe-496a-856f-55095001096e","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"d2ccb9a7-a27a-4819-a3f2-936e188a5e0b","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-yXzcDXohf2NO0i0JjkcE","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-CMcU3djmzAiJ4CBNfQuj","targetTenantKind":"PA","purposeTitle":"purpose-template-m9AJHTVyCVXoKTG","purposeDescription":"purpose-description-bPWPn8TiwzefQQ9M5Prg","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["' OR '1'='1"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-yXzcDXohf2NO0i0JjkcE","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"4da2a697-f024-4a8c-8209-ea29e51f9e6d"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 106. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_SQL_INJECTION @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Consultare la richiesta e la risposta dello scenario su /purposeFreeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /purposeFreeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"id":"f6d4f2a6-cb3e-4960-b54b-1a6f101e9731","targetDescription":"target-description-2XbLaXRECKXdsqIeLddc","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-17T21:28:23.291Z","updatedAt":"2026-11-17T21:28:25.580Z","purposeTitle":"purpose-template-Axaum67z4Yf9Hoo","purposeDescription":"purpose-description-lH8XTgvTNx1E9mtlJp8A","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"ff6095d5-7911-4eb3-97be-bc030dc312e9","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"97682f73-f3d7-4a61-ba6d-7de96a1458a9","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"c8dc59e2-b412-4b22-a769-d23fbbbe8d87","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"e1a0ea71-1fc6-4bd4-8f07-045b682cc8e9","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"b5b0265c-7f0c-4859-9069-ac56d9d9e274","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"' OR '1'='1","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-2XbLaXRECKXdsqIeLddc","targetTenantKind":"PA","purposeTitle":"purpose-template-Axaum67z4Yf9Hoo","purposeDescription":"purpose-description-lH8XTgvTNx1E9mtlJp8A","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"' OR '1'='1","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"f6d4f2a6-cb3e-4960-b54b-1a6f101e9731"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 107. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f7501ea7-a345-4371-944e-63ff65b817ab","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-MSE4111TGPCgJU8","description":"description-PdylogDypVoKZLm","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: {"purposeId":"e5b5fcad-1b64-455e-91e8-fdf66ad16de1"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 108. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /title

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /title e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /title
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"83d08562-c443-4a3d-b373-055155f7adb4","versionId":"98c89216-5ef4-47cd-95b0-a47473f99bf1"}
- payload: {"title":"' OR '1'='1","description":"description-QWfjwMKp2mo6Bhj","isFreeOfCharge":true,"freeOfChargeReason":"reason-HKaC7PU28qNZDVW","dailyCalls":1}
- pathParams: {"purposeId":"83d08562-c443-4a3d-b373-055155f7adb4"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 109. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /description e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /description
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"84dde35f-b356-496c-8f77-ecdca5123f92","versionId":"a20f7b7d-6727-49bd-ae0e-6201146f9509"}
- payload: {"title":"purpose-ZujhRlsAkoUZMN2","description":"' OR '1'='1","isFreeOfCharge":true,"freeOfChargeReason":"reason-MVlLiZDpHcuKCmr","dailyCalls":1}
- pathParams: {"purposeId":"84dde35f-b356-496c-8f77-ecdca5123f92"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool.helpJoin(ForkJoinPool.java:1883)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:440)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 110. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_SQL_INJECTION @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. La policy di ammissione di questo input è in attesa di decisione da parte di Interop. Lo scenario è stato commentato e non dovrebbe essere eseguito nelle prossime run. La sospensione non è classificata come bug di suite e non richiede un ticket dedicato.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_SQL_INJECTION di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Consultare la richiesta e la risposta dello scenario su /freeOfChargeReason e verificare l'aspettativa definita dalla policy. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19653

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_SQL_INJECTION
- targetPath: /freeOfChargeReason
- mutationKind: REPLACE
- mutationValue: ' OR '1'='1
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 200
- responseBody: {"purposeId":"34acca01-611b-473e-8820-22c9681767dc","versionId":"b1fc9f1b-277f-4988-9298-c28bb9aa91e6"}
- payload: {"title":"purpose-G8Cm5YvUkc76STu","description":"description-2BxGtpYQ8BGoypm","isFreeOfCharge":true,"freeOfChargeReason":"' OR '1'='1","dailyCalls":1}
- pathParams: {"purposeId":"34acca01-611b-473e-8820-22c9681767dc"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 111. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

La suite attende HTTP 201; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /delegationId. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19649

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_NULL
- targetPath: /delegationId
- mutationKind: REPLACE
- mutationValue: null
- expectationOrigin: INFERRED_VALID
- responseStatus: 400
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"2fac3a7a-059d-432c-be00-c42bd6b7c759","errors":[{"code":"017-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"1ee16a6c-0777-4d3f-89f0-a5084aadd047","descriptorId":"83514eb9-cc76-44a5-b15c-54a9b3f9e70a","delegationId":null}
- pathParams: null
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <201> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:50)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 112. WebEServiceDetailsCatalogPageContractTest::checkCustomThresholdsOnCatalog7::Precondition failed

**RESULT**

Il selettore 1 recupera il primo attributo accumulato nello store del thread, anziché quello creato dal setup corrente. Il requisito e l'assegnazione usano quindi un attributo precedente, già assegnato al tenant. Il BFF risponde 400 con codice 005-0014; il polling ripete la chiamata fino al timeout. Occorre usare i riferimenti espliciti degli attributi appena creati per requisiti e assegnazioni. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire il setup delle soglie personalizzate con lo store del thread già popolato. Conservare i riferimenti degli attributi appena creati e confrontarli con quelli usati nei requisiti del descriptor e nelle assegnazioni. Verificare che ogni soglia sia associata all'attributo del setup corrente e che il test raggiunga le asserzioni Web.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
java.lang.reflect.UndeclaredThrowableException
	at jdk.proxy2/jdk.proxy2.$Proxy172.assignCertifiedAttribute(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.prepareCustomThresholdsForOtherTenant(WebEServiceDetailsCatalogPageContractTest.java:421)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.checkCustomThresholdsOnCatalog7(WebEServiceDetailsCatalogPageContractTest.java:207)
Caused by: java.lang.reflect.InvocationTargetException
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:24)
	... 3 more
Caused by: java.lang.IllegalStateException: Polling in timeout dopo 10 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.tenant.infrastructure.BffTenantGateway.executeWithPolling(BffTenantGateway.java:163)
	at it.pagopa.interop.bff.tenant.infrastructure.BffTenantGateway.assignCertifiedAttribute(BffTenantGateway.java:107)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy156.assignCertifiedAttribute(Unknown Source)
	at it.pagopa.interop.common.tenant.application.TenantUseCase.assignCertifiedAttribute(TenantUseCase.java:24)
	at it.pagopa.interop.common.journey.infrastructure.TenantJourneyImpl.assignCertifiedAttribute(TenantJourneyImpl.java:63)
	... 8 more
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@b4cdc29> within 10 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 24 more

````


---

### 113. WebAgreementRequestPaginationContractTest::shouldShowPaginationNavbarRegardlessOfOffset::offset elevato mantiene la navbar visibile a fine pagina

**RESULT**

La verifica della barra viene eseguita subito dopo la navigazione con offset elevato, mentre il caricamento della lista non è ancora confermato. Il precedente controllo del titolo riguarda la navigazione iniziale. Occorre attendere il caricamento successivo e ripetere la verifica della presenza della paginazione prima di controllarne l'usabilità. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Aprire la lista delle richieste, quindi navigare nuovamente con offset superiore ai risultati disponibili. Attendere il completamento del caricamento della lista e verificare, tramite polling, che la barra di paginazione sia presente. Controllarne quindi l'usabilità e la possibilità di tornare a una pagina valida.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Pagination navbar is present] 
Expecting value to be true but was false
	at it.pagopa.interop.suite.contract.WebAgreementRequestPaginationContractTest.lambda$highOffsetScenario$3(WebAgreementRequestPaginationContractTest.java:100)

````

</details>

## 4 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 4 KO</summary>

### 1. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #1 | line: 51]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale su QA PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [33d39525-23cc-4848-90a4-db1bad3298c8,
    5a2ad596-5992-4ca3-9d91-0d19c2fff386,
    c4d956a9-ca7d-405b-9e55-04e4b5da9cf2]
to contain exactly (and in same order):
  [5a2ad596-5992-4ca3-9d91-0d19c2fff386,
    33d39525-23cc-4848-90a4-db1bad3298c8,
    c4d956a9-ca7d-405b-9e55-04e4b5da9cf2]
but there were differences at these indexes:
  - element at index 0: expected "5a2ad596-5992-4ca3-9d91-0d19c2fff386" but was "33d39525-23cc-4848-90a4-db1bad3298c8"
  - element at index 1: expected "33d39525-23cc-4848-90a4-db1bad3298c8" but was "5a2ad596-5992-4ca3-9d91-0d19c2fff386"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 2. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #3 | line: 53]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale su PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [3d95af34-22f1-4be5-bc93-67d058b7139b,
    8c22d224-6531-467f-883f-5a22eca0e00a,
    3a824e31-8d4b-4b90-ac77-2625ab849847]
to contain exactly (and in same order):
  [3d95af34-22f1-4be5-bc93-67d058b7139b,
    3a824e31-8d4b-4b90-ac77-2625ab849847,
    8c22d224-6531-467f-883f-5a22eca0e00a]
but there were differences at these indexes:
  - element at index 1: expected "3a824e31-8d4b-4b90-ac77-2625ab849847" but was "8c22d224-6531-467f-883f-5a22eca0e00a"
  - element at index 2: expected "8c22d224-6531-467f-883f-5a22eca0e00a" but was "3a824e31-8d4b-4b90-ac77-2625ab849847"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 3. BffClientContractTest::createKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. Viene impostato dunque un algoritmo vuoto, che funzionalmente è inammissibile.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Sostituire /alg con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11354

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-18851

**ERROR LOG**

````text
java.lang.AssertionError: HTTP contract expectation failed
- scope: PAYLOAD
- scenario: REPLACED_WITH_EMPTY_STRING
- targetPath: /alg
- mutationKind: REPLACE
- mutationValue: 
- expectationOrigin: POLICY_UNKNOWN
- responseStatus: 204
- responseBody: 
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtUzJNQ1NGdEJNbEd4eUsvUFNsNApQQ05XT1E2WW0yUndvNFVyYlJlenh1ZU5STHBYMzNxdGRSQmxRZG1ZbHNTSUQ4K0g4ZXJVMlQxcC9iYm9nSEFXCkNhOWlGZnRrd0FPbnVIcGxzYUhFeDVQUWNVNU1vNU5YWlFHNjRoQUQzTkY0ZE9Bc0dPK0V2NEZCd2pkb055T0sKczZpbUt4cklMNURUWXVBeExHdHN6RlAwTWpIYjNJQnBuWHNKQmUxeFpuTFlYeWRWZ25ucTQ5QzJ6MDhPblJKeApxUUxXa0Z4WUlCWVRYTjZVODZiZm94VFBPU3VyMjRlanlFTDYxR3hBRi9NN0FmaWRJMDFTV1J2cGkxWVB0MG85ClU5VStqV1RHOFpOZExTMmlrOEphVTlqb1I4VUUwdHR0bzBVcHovQk54YzFYRHkvU3JjbzdGY2loOU9iMU1HdmQKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"key-4PRbfymiEhdbKdX"}
- pathParams: {"clientId":"cec7a8a2-d016-4fa9-84ed-943d69821d86"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:670)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)
Caused by: java.lang.AssertionError: 1 expectation failed.
Expected status code <400> but was <204>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 4. WebPurposeTemplateContractTest::createPurposeTemplate::creating purpose template with personal data treatment enabled

**RESULT**

Il caricamento della pagina di creazione del template supera il timeout e il titolo rimane vuoto. Il test non raggiunge la verifica del trattamento dei dati personali. Il caso è classificato come flaky, la fix è stata apportata in data odierna per mezzo di sviluppi collaterali.

**PROCEDURE**

Seguire la navigazione alla pagina di creazione del template con trattamento dati personali disabilitato. Controllare URL, titolo, ruoli e risposte di caricamento fino allo scadere del polling.

**ERROR LOG**

````text
java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.
Purpose Template Creation Page is not loaded yet. Page title is blank
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCreationPage.assertLoaded(PurposeTemplateCreationPage.java:34)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy302.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebPurposeTemplateContractTest.assertLandedOnCreatedTemplate(WebPurposeTemplateContractTest.java:70)
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of "I miei template di finalità agevolata" within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 7 more

````

</details>
