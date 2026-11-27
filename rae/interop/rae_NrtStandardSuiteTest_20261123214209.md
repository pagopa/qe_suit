# 20261123 Report Analisi Esiti (RAE) NrtStandardSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release |  |
| Scope | NIGHTLY |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19740 |
| Data e ora | 2026-11-23T21:09:28Z |
| Durata | 00:32:06 |
| Numero KO | 113 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/45266472772/attempts/1 |
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

## 0 - KO DOVUTI A BUG DI PRODOTTO NOTI

<details>
<summary>Mostra/nascondi 0 KO</summary>

Nessun fallimento.

</details>

## 0 - KO DOVUTI A BUG DI SUITE NOTI

<details>
<summary>Mostra/nascondi 0 KO</summary>

Nessun fallimento.

</details>

## 113 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 113 KO</summary>

### 1. [CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per il fruitore [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il test CONSUMER_AGREEMENT_DOWNLOAD_1 è fallito durante la verifica iniziale del pulsante "Scarica richiesta di fruizione" nella pagina di dettaglio del fruitore.

Dopo le azioni "Attiva" e "Conferma" eseguite dall’erogatore, il selettore non ha individuato il pulsante nella sezione "Informazioni generali", neanche dopo circa 20 secondi di tentativi.

Il test non ha raggiunto la successiva verifica di abilitazione del pulsante.

I log non confermano però lo stato ACTIVE né lo stato del documento al momento del KO.

**PROCEDURE**

Ricostruire la sequenza fino al fallimento, verificando le azioni di approvazione dell’erogatore e la successiva navigazione al dettaglio della richiesta come fruitore. Esaminare quindi il selettore cercato, i tentativi effettuati e lo stack trace, per stabilire se il KO avviene sulla presenza del pulsante o sulla sua abilitazione dopo l’attesa.

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
	at jdk.proxy2/jdk.proxy2.$Proxy274.isDisabled(Unknown Source)
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
	at jdk.proxy2/jdk.proxy2.$Proxy152.verifyConsumerAgreementDownloadAvailability(Unknown Source)
	at it.pagopa.interop.common.agreement.application.AgreementUseCase.verifyConsumerAgreementDownloadAvailability(AgreementUseCase.java:30)
	at it.pagopa.interop.common.agreement.infrastructure.cucumber.AgreementSteps.verifyAgreementDownloadAvailabilityForConsumer(AgreementSteps.java:61)
	at ✽.il fruitore può scaricare il documento della richiesta di fruizione solo quanto è pronto(classpath:feature-expanded/agreement/download-agreement.feature:10)

````


---

### 2. [PRODUCER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per l'erogatore [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il test PRODUCER_AGREEMENT_DOWNLOAD_1 è fallito durante la verifica iniziale del pulsante "Scarica richiesta di fruizione" nella pagina di dettaglio dell'erogatore.

Dopo le azioni "Attiva" e "Conferma", il selettore non ha individuato il pulsante nella sezione "Informazioni generali", neanche dopo circa 20 secondi di tentativi.

Il test non ha raggiunto la successiva verifica di abilitazione.

I log non confermano lo stato ACTIVE né lo stato del documento al momento del KO.

**PROCEDURE**

Ricostruire la sequenza fino al fallimento, verificando le azioni di approvazione dell’erogatore e la successiva navigazione al dettaglio della richiesta come fruitore. Esaminare quindi il selettore cercato, i tentativi effettuati e lo stack trace, per stabilire se il KO avviene sulla presenza del pulsante o sulla sua abilitazione dopo l’attesa.

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
	at jdk.proxy2/jdk.proxy2.$Proxy274.isDisabled(Unknown Source)
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
	at jdk.proxy2/jdk.proxy2.$Proxy152.verifyProducerAgreementDownloadAvailability(Unknown Source)
	at it.pagopa.interop.common.agreement.application.AgreementUseCase.verifyProducerAgreementDownloadAvailability(AgreementUseCase.java:34)
	at it.pagopa.interop.common.agreement.infrastructure.cucumber.AgreementSteps.verifyAgreementDownloadAvailabilityForProducer(AgreementSteps.java:55)
	at ✽.l'erogatore può scaricare il documento della richiesta di fruizione solo quanto è pronto(classpath:feature-expanded/agreement/download-agreement.feature:17)

````


---

### 3. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #2 | line: 52]

**RESULT**

Il test PRODUCER_AGREEMENT_DOWNLOAD_1 è fallito durante la verifica iniziale del pulsante "Scarica richiesta di fruizione" nella pagina di dettaglio dell'erogatore.

Dopo le azioni "Attiva" e "Conferma", il selettore non ha individuato il pulsante nella sezione "Informazioni generali", neanche dopo circa 20 secondi di tentativi.

Il test non ha raggiunto la successiva verifica di abilitazione.

I log non confermano lo stato ACTIVE né lo stato del documento al momento del KO.

**PROCEDURE**

Ricostruire la sequenza fino al fallimento, verificando le azioni di approvazione dell’erogatore e la successiva navigazione al dettaglio della richiesta come fruitore. Esaminare quindi il selettore cercato, i tentativi effettuati e lo stack trace, per stabilire se il KO avviene sulla presenza del pulsante o sulla sua abilitazione dopo l’attesa.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11528

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [1971db14-ea23-4a33-b9e7-8bc1183c5ceb,
    7f16ce61-12be-4f00-a8f0-8ea727fed02c,
    c64c39a4-8a6d-46c7-bc9f-c9f6ee95a470]
to contain exactly (and in same order):
  [c64c39a4-8a6d-46c7-bc9f-c9f6ee95a470,
    1971db14-ea23-4a33-b9e7-8bc1183c5ceb,
    7f16ce61-12be-4f00-a8f0-8ea727fed02c]
but there were differences at these indexes:
  - element at index 0: expected "c64c39a4-8a6d-46c7-bc9f-c9f6ee95a470" but was "1971db14-ea23-4a33-b9e7-8bc1183c5ceb"
  - element at index 1: expected "1971db14-ea23-4a33-b9e7-8bc1183c5ceb" but was "7f16ce61-12be-4f00-a8f0-8ea727fed02c"
  - element at index 2: expected "7f16ce61-12be-4f00-a8f0-8ea727fed02c" but was "c64c39a4-8a6d-46c7-bc9f-c9f6ee95a470"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 4. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #3 | line: 53]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [3b61a915-f6ec-428b-a1a8-3ba646cd8761,
    443cdc38-95b9-4e65-b7f3-ef2b3a9dd124,
    38bd73cb-6f6a-4bf4-b2cd-71a2d9316806]
to contain exactly (and in same order):
  [3b61a915-f6ec-428b-a1a8-3ba646cd8761,
    38bd73cb-6f6a-4bf4-b2cd-71a2d9316806,
    443cdc38-95b9-4e65-b7f3-ef2b3a9dd124]
but there were differences at these indexes:
  - element at index 1: expected "38bd73cb-6f6a-4bf4-b2cd-71a2d9316806" but was "443cdc38-95b9-4e65-b7f3-ef2b3a9dd124"
  - element at index 2: expected "443cdc38-95b9-4e65-b7f3-ef2b3a9dd124" but was "38bd73cb-6f6a-4bf4-b2cd-71a2d9316806"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 5. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #4 | line: 54]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [08cb7bbc-a64b-4829-a769-b251f5e9abf8,
    2e97a96e-5eb9-42ef-a0bb-50da6261ea2e,
    def52c89-c1c9-4c13-8965-b5f1e5375525]
to contain exactly (and in same order):
  [08cb7bbc-a64b-4829-a769-b251f5e9abf8,
    def52c89-c1c9-4c13-8965-b5f1e5375525,
    2e97a96e-5eb9-42ef-a0bb-50da6261ea2e]
but there were differences at these indexes:
  - element at index 1: expected "def52c89-c1c9-4c13-8965-b5f1e5375525" but was "2e97a96e-5eb9-42ef-a0bb-50da6261ea2e"
  - element at index 2: expected "2e97a96e-5eb9-42ef-a0bb-50da6261ea2e" but was "def52c89-c1c9-4c13-8965-b5f1e5375525"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 6. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.1] - Impossibilità di archiviare un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il rifiuto dell'archiviazione è corretto. L'e-service non può essere archiviato con 60 giorni di preavviso perché una sua versione è già in fase di archiviazione con 120 giorni di preavviso. Atteso da Figma: la snackbar mostra solo il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» Osservato nella UI: la snackbar mostra il messaggio atteso, seguito dalle istruzioni per contattare l'assistenza e da un identificativo copiabile. Il test confronta il testo rilevato nella snackbar con il messaggio atteso tramite un'asserzione di uguaglianza. Il testo aggiuntivo rende le due stringhe diverse e fa fallire il test.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.1. Consultare lo stack trace e confrontare il testo atteso con quello rilevato da WebEServiceGateway.assertArchivingRejection per il caso ARCHIVING_BEFORE_DESCRIPTOR. Durante la riesecuzione, verificare che l'archiviazione venga rifiutata e confrontare il contenuto della snackbar mostrata a video con il design Figma di riferimento. Individuare le parti del messaggio visualizzato che differiscono dal design.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11178

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.
  Se il problema non si risolve, contatta l’assistenza e indica il codice qui sotto."
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
	at jdk.proxy2/jdk.proxy2.$Proxy159.shouldFailToArchiveEService(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToArchiveEService(EServiceUseCase.java:43)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchiving(EServiceSteps.java:62)
	at ✽.il sistema rifiuta l'archiviazione dell'e-service da parte dell'erogatore con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:20)

````


---

### 7. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il sistema rifiuta correttamente la richiesta del delegato con 60 giorni di preavviso, perché l'archiviazione dell'e-service precederebbe quella di una versione già pianificata con 120 giorni di preavviso. Da Figma ci si aspetta il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» La snackbar mostra invece «Non è stato possibile richiedere l’archiviazione di questo e-service. Riprova più tardi.», seguito dalle istruzioni per contattare l'assistenza e da un identificativo. Al delegato viene quindi suggerito di riprovare più tardi, mentre per procedere deve selezionare un preavviso più lungo. Il test confronta il testo rilevato nella snackbar con quello atteso tramite un'asserzione di uguaglianza. Il confronto fallisce già sul messaggio principale, che è diverso da quello previsto.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.2 e consultare lo stack trace. Confrontare il testo atteso per ARCHIVING_BEFORE_DESCRIPTOR con quello rilevato da WebEServiceGateway.assertArchivingRejection. Durante la riesecuzione, verificare che la richiesta di archiviazione del delegato venga rifiutata. Confrontare la snackbar mostrata a video con il design Figma e verificare se il messaggio indica il motivo del rifiuto e l'azione da intraprendere.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11179

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.
  Se il problema non si risolve, contatta l’assistenza e indica il codice qui sotto."
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
	at jdk.proxy2/jdk.proxy2.$Proxy159.shouldFailToRequestEServiceArchiving(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToRequestEServiceArchiving(EServiceUseCase.java:47)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchivingRequest(EServiceSteps.java:69)
	at ✽.il sistema rifiuta la richiesta di archiviazione dell'e-service da parte del delegato con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:36)

````


---

### 8. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_NULL @ /answerData/annotation

**RESULT**

La suite legge @Nullable sul campo annotation della classe Java autogenerata e deduce che anche "annotation": null sia valido nel JSON. Per questo classifica la mutazione come INFERRED_VALID e attende HTTP 200. Nel modello Java, null può rappresentare un campo omesso. Il contratto OpenAPI consente di omettere annotation, ma se il campo è presente richiede un oggetto. Il BFF riceve un null esplicito e restituisce correttamente HTTP 400. Il test fallisce perché attende 200. Per questa mutazione, la suite deve ricavare l'aspettativa dallo schema OpenAPI e attendere HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Inviare un null JSON esplicito in /answerData/annotation, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d5093b6e-be4e-4c3f-a7e9-775b31b6eecd","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"answerData.annotation\""}]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":null,"suggestedValues":["answer-value-7skyo2g9tZ2FNYf"]}}
- pathParams: {"purposeTemplateId":"4bbdb541-c8f4-4cd9-8896-b7a53cc99b69"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 9. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_BLANK_STRING @ /answerData/suggestedValues/0

**RESULT**

La suite attende HTTP 400. Il BFF restituisce HTTP 200 e riporta il valore suggerito con tutti e tre gli spazi. L'asserzione fallisce per la differenza tra lo status atteso e quello effettivo. Il 200 è coerente con il contratto OpenAPI attuale, che impone una lunghezza da 1 a 250 caratteri per valore suggerito. Il problema di prodotto è che si può configurare un suggerimento privo di contenuto utile. L'atteso proposto è rifiutare con HTTP 400 i valori composti solo da spazi e aggiornare il contratto OpenAPI. La resa nel portale resta da verificare.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer su POST /purposeTemplates/{purposeTemplateId}/riskAnalysis/answers. Sostituire /answerData/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"03343ac9-f58a-49bc-9d3e-f1350b0ce94a","values":[],"editable":false,"annotation":{"id":"56a466aa-c97c-4f19-befe-372984c43e95","text":"annotation-gPqfspUR6CqwKezt7mMR","docs":[]},"suggestedValues":["   "]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"annotation-gPqfspUR6CqwKezt7mMR"},"suggestedValues":["   "]}}
- pathParams: {"purposeTemplateId":"1a44f660-7114-4344-9555-2bdf46ab2b06"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 10. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

OpenAPI consente di omettere delegationId. Se presente, il campo deve contenere una stringa in formato UUID. Il BFF riceve un null esplicito e restituisce HTTP 400, come previsto dal contratto. La suite classifica la mutazione come INFERRED_VALID e attende HTTP 200. Il test fallisce perché la suite tratta il null esplicito come valido per un campo opzionale. Per questa mutazione deve attendersi HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffAgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"227d0cab-403e-4d08-ae02-9e4da7ea7492","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"22d3ac11-bf25-487a-8049-260c2020cfca","descriptorId":"3f113549-8e45-40f7-aeb2-015f5f243462","delegationId":null}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 11. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

Il BFF restituisce HTTP 400 con codice 002-0005 e dettaglio "EService not found". La suite attende HTTP 404 e l'asserzione fallisce per la differenza di status.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"41156030-59f2-4ff4-b8a7-15715562eba3","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"75ad685b-84de-4a6f-ac44-b30a32ac5aa9"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 12. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

**RESULT**

Per il descriptorId inesistente, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0021 e il messaggio "Descriptor provided is not the latest descriptor". Il messaggio presenta il descrittore come una versione non aggiornata, mentre l'ID inviato non corrisponde a un descrittore esistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /descriptorId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"647602d4-7bc3-4781-84f9-4cfea945d73d","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}],"userMessage":"Non puoi richiedere la fruizione di questa versione perché non è la più recente. Aggiorna la pagina e, se disponibile, crea una richiesta per l'ultima versione."}
- payload: {"eserviceId":"2a87bf58-110c-4eb4-a951-855b4cb0f567","descriptorId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 13. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

**RESULT**

Per la delega inesistente indicata nel body, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0026 e messaggio "Delegation not found". Il BFF identifica correttamente la delega come inesistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /delegationId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"506b92d1-e3df-4cf2-bee5-a274241ac74b","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"2a00c05b-4ed3-4dc7-9dbb-21f83074a759","descriptorId":"e7695716-1116-4ab2-92e4-34af0bf5ba6e","delegationId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 14. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_NULL @ /description

**RESULT**

In ClientSeed, description è opzionale e, se presente, deve contenere una stringa. Il JSON inviato contiene un null esplicito. Il BFF restituisce HTTP 400 con l'errore "Expected string, received null at description", in linea con lo schema OpenAPI. La suite classifica la mutazione come INFERRED_VALID e si aspetta HTTP 200. L'asserzione fallisce perché l'aspettativa non distingue tra omissione del campo e invio di null. Per questa mutazione, la suite deve attendersi HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffClientContractTest::createApiClient su POST /clientsApi. Inviare un null JSON esplicito in /description, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"681e24c0-1a6c-4ece-a589-ebdf923ef048","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-QCYYivUsRyvH19Lzsatmqxkg","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 15. BffClientContractTest::createApiClient::[payload] REMOVED @ /members/0

**RESULT**

La mutazione lascia members presente con valore []. Lo schema ClientSeed richiede la presenza di members e consente un array vuoto perché non specifica minItems. Il BFF restituisce HTTP 200 con l'ID del client creato, in linea con il contratto. La suite classifica il payload come POLICY_INVALID e attende HTTP 400. L'asserzione fallisce per questa aspettativa. Per il caso REMOVED su /members/0, la suite deve attendersi HTTP 200.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffClientContractTest::createApiClient su POST /clientsApi. Rimuovere esclusivamente /members/0 dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"f02089f2-9fb4-4b7a-bc71-cec542383aca"}
- payload: {"name":"client-lJeu8I4iE8D8rHng3ge21VyZ","description":"description-akEkGI3ILqLiiO5rXo28VUGe1STrvaE1","members":[]}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 16. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

Il BFF accetta una descrizione composta da tre spazi, restituisce HTTP 200 e riporta la descrizione invariata nell'attributo creato. Il test fallisce perché la suite attende HTTP 400. Il 200 è coerente con CertifiedAttributeSeed: description è obbligatoria e lo schema attuale ammette una stringa di soli spazi. Il problema da segnalare è la possibilità di creare un attributo certificato con una descrizione priva di contenuto visibile. L'atteso da confermare con Interop è il rifiuto con HTTP 400 e l'allineamento dello schema OpenAPI.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"bed8841c-2e67-4d5b-8e10-c4236e2bd854","code":"96a68cb631a5813bb5375fb8f93768bfeeea2544c7a9a360a03409b2dda96969","kind":"CERTIFIED","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-PY79XYfms8JPzgHimIxAkmBb","creationTime":"2026-11-23T21:22:31.800Z"}
- payload: {"description":"   ","name":"name-PY79XYfms8JPzgHimIxAkmBb"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 17. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

Il BFF consente la creazione di un attributo certificato con un nome composto solo da spazi e restituisce HTTP 200 in assenza di conflitti. Nella verifica restituisce HTTP 409 con codice 003-0002 ("Duplicate attribute name or code") perché esiste già, per la stessa origine, un attributo con lo stesso nome o codice. La suite attende HTTP 400 per il nome privo di contenuto visibile. Il KO osservato è dovuto al 409 per duplicato. Il difetto di prodotto consiste nella possibilità di creare l'attributo con un nome di soli spazi, consentita dallo schema OpenAPI attuale.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedAttributeContractTest::createCertifiedAttribute su POST /certifiedAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"99e20a77-a780-45fb-bc52-0d2399daa5eb","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-waJ5zR7HOen7sPFr1hMHNTgeNfq6ILxG","name":"   "}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 18. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"9f707a8c-8ec8-4e86-80f2-7059189884fe","code":"189b8b5828cb57e089850b7debeef2f222e16c13499cbd066cadcc9ed77128ce","kind":"CERTIFIED_DISCRETE","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-F87fnExkTc6mUUvi6hGPXCZw","creationTime":"2026-11-23T21:31:20.564Z"}
- payload: {"description":"   ","name":"name-F87fnExkTc6mUUvi6hGPXCZw"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 19. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute su POST /certifiedDiscreteAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"66d568ae-d8ce-46a6-8dc9-dd2d9b4acbac","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-igbNH6uu5zWMVIQPamgGRuQgpyNk7prE","name":"   "}
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
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 20. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_NULL @ /description

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /description. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Inviare un null JSON esplicito in /description, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"7457b166-aab6-4700-95bd-55505c05c8fc","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-WSfLPnAGGRMhT3w3a3NXez0V","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 21. BffClientContractTest::createConsumerClient::[payload] REMOVED @ /members/0

**RESULT**

La suite pretende 400 per l’assenza dell’elemento, ma il BFF accetta members: [] con HTTP 200. La policy deve distinguere la rimozione di un membro dall’omissione della proprietà. Il caso è analogo a createApiClient, già ricondotto a QA-19650.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffClientContractTest::createConsumerClient su POST /clientsConsumer. Rimuovere esclusivamente /members/0 dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"26ca2066-9058-4ff4-b569-865277d2e4bc"}
- payload: {"name":"client-WSfLPnAGGRMhT3w3a3NXez0V","description":"description-MyT9LtKGBnr7HydmVRNktDTs0Jwb46sC","members":[]}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 22. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"6c99fdeb-c377-41f0-90e5-0da88b070bd7","kind":"DECLARED","description":"   ","name":"name-z9kki2KLLy4Segv9FvbxGjeX","creationTime":"2026-11-23T21:20:03.983Z"}
- payload: {"description":"   ","name":"name-z9kki2KLLy4Segv9FvbxGjeX"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 23. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDeclaredAttributeContractTest::createDeclaredAttribute su POST /declaredAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"03dd5ff1-1bb1-4515-bbd9-3c02d3d4b5d8","kind":"DECLARED","description":"description-UGDIvP4cx5APF6B2Xai8CsT7mopHNtlW","name":"   ","creationTime":"2026-11-23T21:20:06.433Z"}
- payload: {"description":"description-UGDIvP4cx5APF6B2Xai8CsT7mopHNtlW","name":"   "}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 24. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6493139e-a27e-4766-bef9-a4c4f7924999","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-aJREEZajJ4Xy1lI","description":"description-CHneeVibDQEFDuWCrLEx","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 25. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isConsumerDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isConsumerDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d6e1dca8-853c-4edf-993e-641fbc54dda8","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-btk7d3TLrVKX6Zh","description":"description-rChtHnoPqbOMN4e4wFKc","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 26. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isClientAccessDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /isClientAccessDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"1fbea10d-13d1-4c89-b96b-a58b14931407","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-TpQliKagP4ywCFt","description":"description-EYIhOFOue6l9NO2qQiNe","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 27. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b941f4fe-dd48-4636-94ce-4953e288829c","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-nbCxSppmigZmPIP","description":"description-iFXpLrNHNKlyzUMvufRW","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 28. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::createEService su POST /eservices. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bd61ee4b-fc54-4186-bad1-f22e86258c19","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-ah10TwIH1HvZc2i","description":"description-VrUhOP88SV231975v7pU","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 29. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6c6b4cd9-5487-4178-ae26-5ad5f30040fd","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"version\""}]}
- payload: {"name":"eservice-template-6ssvIuc9CjLd5tp","intendedTarget":"target-ENloSRLLdpMvNTU27xHcu5LIfVaLq7","description":"description-TCQjNrXhmACiDWbbse89CLOD3r1VHJUMIf8p4o1aYUakHi9XwJ","technology":"REST","mode":"DELIVER","version":null,"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 30. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsPerConsumer

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/dailyCallsPerConsumer. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/dailyCallsPerConsumer, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"1c525de6-38c4-4def-9e0a-a74d2a3d41c8","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsPerConsumer\""}]}
- payload: {"name":"eservice-template-uPRI1qtkZSg3e0X","intendedTarget":"target-pzFzsGze7Yqz4xphJ9SNC6zoO1uNEh","description":"description-Jr7YEBkZJB24CGdql1CzLlkfiilFREGzKpWjFnxG7IxlWPiltg","technology":"REST","mode":"DELIVER","version":{"description":"version-description-SLB4h1f6KLktHwjF8ZDGF38LvEMr0donLbz","voucherLifespan":60,"dailyCallsPerConsumer":null,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 31. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsTotal

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/dailyCallsTotal. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/dailyCallsTotal, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bcfdf603-8bae-4756-8951-cb6bea653d99","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsTotal\""}]}
- payload: {"name":"eservice-template-AB1GRFOJs0lw1Ss","intendedTarget":"target-y64mfOEQ1CsUAweK40OmRy8KV4zRff","description":"description-cJwBHaPgvtowXLXuvbcs98Jo8EGkU5WpNac6cQlVqO5Cbg5IeG","technology":"REST","mode":"DELIVER","version":{"description":"version-description-no3IdkSo6uDlumhZPvRpR2x9Kv5O5rOY8OS","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":null,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 32. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/agreementApprovalPolicy

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /version/agreementApprovalPolicy. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /version/agreementApprovalPolicy, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8b489165-3126-46c3-b560-9917b1368e7c","errors":[{"code":"008-9999","detail":"Validation error: Expected 'AUTOMATIC' | 'MANUAL', received null at \"version.agreementApprovalPolicy\""}]}
- payload: {"name":"eservice-template-BN3EN81kGUyBLyN","intendedTarget":"target-ZOAWnl3mS3rjmIctJTY2ARTeIwu39J","description":"description-t0Ay7d38zlk53IWjMwIlDEJ85GHso4Pe9aVbyZighQkTOZ1ZJ9","technology":"REST","mode":"DELIVER","version":{"description":"version-description-YURyhvjVQsj2N8Gf1zJr5GET5rH9OY3VbE9","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":null},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 33. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3024be14-3094-4519-808a-b19009628444","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-template-PzT3Y7XxvJ1WTpg","intendedTarget":"target-oXHfHUO1fu3Y2f2Rrxuj3rnZyflx2n","description":"description-xood8Q74Op0gWJzPYfgi7PvbLRootkIHdA0yvPMTeTVijx34fr","technology":"REST","mode":"DELIVER","version":{"description":"version-description-UeZN27L1Jjdj8UbZd2hoNPDDKvh610zFQMf","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":null,"personalData":false,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 34. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c068ad3c-684d-4bee-9c95-b4111c4a0869","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-template-6clirbu7KYMZaZa","intendedTarget":"target-XvW7yADUeRhc0pxNQGvvEa1aEnxy9V","description":"description-f6o6cyiQDw69KPiWYMmoOVFtoVlyvvClf49SwYATvm7gjgG81j","technology":"REST","mode":"DELIVER","version":{"description":"version-description-gAWqAYrkHOjIZKB1hJxrhbHT2qv3ksDl5Q0","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":null,"asyncExchange":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 35. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceTemplateContractTest::createEServiceTemplate su POST /eservices/templates. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"515bd431-11a4-44ee-95e0-7c6362888941","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-template-LNIk699NyiaI8f6","intendedTarget":"target-qv4qjh1HpkQ8pDl7XWBOJH4soDJiHJ","description":"description-JLVgqX9qSYhC3inWZPyKmLhcVSVOVhTkfblHPlrBLsFf7x8miQ","technology":"REST","mode":"DELIVER","version":{"description":"version-description-c4hEXsaujRapETHsYGPeBxwFAmfMCTCat2I","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":null}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 36. BffClientContractTest::createKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. Viene impostato dunque un algoritmo vuoto, che funzionalmente è inammissibile.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Sostituire /alg con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1enZHTWNsalBJbkFzd3JJOEIxVwpJOElYaVRnYllzd2lBUVF4a0t1T081K05WR1MyNW9waVF3bVFrMkd6UE05K2FyeTlpVXhhMk52KzVRUUhKbnEwClJDZkF2RGJzdkhlZ05VYU9SSkxMWVRVYWVHZnlGUlFQOTY5RnpNakxiMlVjVEFIQjlwT0hXZWhuazE5disxSmMKUUVIc2d1MUJsbjZSS05BKzBqTzc2ZnpqWVlMN3R5YytmOEpBcUx2TlU2OXdSeTZocFNLU1h0QTFmWDVZSGFVWgo2dHpRak1ySzVaQmJtaitEMjRrdWdVSnV2RHc2UjJiS0JjRnhvQWlCcXd3dmZPT0QvVTNCYjNwVHA0OUlMUHR5ClNtQlJCZ2ZGenZnYWhac3czMXEvL2xaRnQ1a0wxSmNuYWg4Qm5udUk0OUp3N1h0dHdkTENoWlhxWFk3NlBsc0UKa1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"key-kTqMjrRLDzpvi70"}
- pathParams: {"clientId":"157da539-2e7b-42c0-91c0-01ba83e6feb7"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 37. BffClientContractTest::createKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

**RESULT**

Il problema è legato al fatto che per un atto di creazione della chiave, questa risulta già essere stata creata prima, facendo scaturire un errore 409 di CONFLICT per "Key with kid X already exists"

**PROCEDURE**

Log analizzato. Test ripetuto in locale e il problema si riscontra anche in locale nella medesima forma ottenendo un responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid vKsqTFUc3iYp033uYUYMLDJW8fecdV5ZGZUvYVsCxXI already exists","correlationId":"30f14697-2a8b-4b93-940d-1f4af85c42fb","errors":[{"code":"006-0017","detail":"Key with kid vKsqTFUc3iYp033uYUYMLDJW8fecdV5ZGZUvYVsCxXI already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19681

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid LRQWR8Qel2KsNc-0vPcglneeoo9LynHuL9r3LpY5WzE already exists","correlationId":"3621327c-c510-4a1c-b8ed-a4c45cf9d381","errors":[{"code":"006-0017","detail":"Key with kid LRQWR8Qel2KsNc-0vPcglneeoo9LynHuL9r3LpY5WzE already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1enZHTWNsalBJbkFzd3JJOEIxVwpJOElYaVRnYllzd2lBUVF4a0t1T081K05WR1MyNW9waVF3bVFrMkd6UE05K2FyeTlpVXhhMk52KzVRUUhKbnEwClJDZkF2RGJzdkhlZ05VYU9SSkxMWVRVYWVHZnlGUlFQOTY5RnpNakxiMlVjVEFIQjlwT0hXZWhuazE5disxSmMKUUVIc2d1MUJsbjZSS05BKzBqTzc2ZnpqWVlMN3R5YytmOEpBcUx2TlU2OXdSeTZocFNLU1h0QTFmWDVZSGFVWgo2dHpRak1ySzVaQmJtaitEMjRrdWdVSnV2RHc2UjJiS0JjRnhvQWlCcXd3dmZPT0QvVTNCYjNwVHA0OUlMUHR5ClNtQlJCZ2ZGenZnYWhac3czMXEvL2xaRnQ1a0wxSmNuYWg4Qm5udUk0OUp3N1h0dHdkTENoWlhxWFk3NlBsc0UKa1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"key-kTqMjrRLDzpvi70"}
- pathParams: {"clientId":"d4f4aeea-ab47-4fac-ae53-0121a6448d01"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 38. BffClientContractTest::createKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il valore di alg di 5.000 caratteri non rappresenta un identificatore di algoritmo utilizzabile. Occorre validare i valori ammessi e la loro lunghezza. Il conflitto per kid già presente maschera il controllo della lunghezza; la verifica richiede una chiave nuova e un'esecuzione isolata. Il difetto è tracciato in PIN-11053.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffClientContractTest::createKey su POST /clients/{clientId}/keys. Sostituire /alg con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid LRQWR8Qel2KsNc-0vPcglneeoo9LynHuL9r3LpY5WzE already exists","correlationId":"d968f94e-f59b-4469-b95e-327010b95cdd","errors":[{"code":"006-0017","detail":"Key with kid LRQWR8Qel2KsNc-0vPcglneeoo9LynHuL9r3LpY5WzE already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1enZHTWNsalBJbkFzd3JJOEIxVwpJOElYaVRnYllzd2lBUVF4a0t1T081K05WR1MyNW9waVF3bVFrMkd6UE05K2FyeTlpVXhhMk52KzVRUUhKbnEwClJDZkF2RGJzdkhlZ05VYU9SSkxMWVRVYWVHZnlGUlFQOTY5RnpNakxiMlVjVEFIQjlwT0hXZWhuazE5disxSmMKUUVIc2d1MUJsbjZSS05BKzBqTzc2ZnpqWVlMN3R5YytmOEpBcUx2TlU2OXdSeTZocFNLU1h0QTFmWDVZSGFVWgo2dHpRak1ySzVaQmJtaitEMjRrdWdVSnV2RHc2UjJiS0JjRnhvQWlCcXd3dmZPT0QvVTNCYjNwVHA0OUlMUHR5ClNtQlJCZ2ZGenZnYWhac3czMXEvL2xaRnQ1a0wxSmNuYWg4Qm5udUk0OUp3N1h0dHdkTENoWlhxWFk3NlBsc0UKa1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"key-kTqMjrRLDzpvi70"}
- pathParams: {"clientId":"a4ce3738-e5c5-4d6a-bbfb-ca1f99de4dd2"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 39. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204.La chiave viene accettata con alg vuoto o composto da soli spazi. Il valore non identifica un algoritmo e deve essere rifiutato. Il difetto di prodotto è tracciato in PIN-11354.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF2Wlp1WGNKTjFqb0IzZnBXTFN6aAp0OHhzSmdaQ2RBWXFxZU5Sb2VJZU1JRTVzb1U2VXBhdmdXMk5BaENiTkNnbHp5eE53cy9oRitJOTZYeHkydW9sCmY3Q1BoZGVvNVlwV091OFhIeCtCUHhyalNNRjR0bTlVbTRPMUxsclUrVmFXZVR6dDhOeVpJaERzOW56T2V6VHgKNTAwWXhOUkdRODRnaFZGYWwzWVJ4aDlBYlNBUWhrSVFGd3dmSDJSQWJ2V3hCRVJjbnFRL2JBVmRiRFJNSDljKwpmUUdXVVhkdncxVUFqWUdUMGNlOEV0UFhyWEE4bnFUSGlGcGs5Z0pVY1RSUlBjdkx2WFQ0YXI3TkZUeVI3dm02CnZacitVM2JNWFZEUWVUUnE5RUkrU3NYNjZxUlhFU1NxRXN0Z241SWpsdm41ZFUyc1l1Sk5DY2E5VjhQMy9tdGcKV1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"producer-key-hC7aaGvWq8adAPQ"}
- pathParams: {"producerKeychainId":"f3158f8a-3913-4988-8764-a9a7406747cc"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 40. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204.La chiave viene accettata con alg vuoto o composto da soli spazi. Il valore non identifica un algoritmo e deve essere rifiutato. Il difetto di prodotto è tracciato in PIN-11354.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEydExKckl1aklEMWgyNnliQmZpNgpSeURVa1MxNkE0VVJCYytLMnlVdUswMjJwZ3BpcHdZRzVRdzV4RGIxYTR5V1NwSW1iZUl6MkRhQVZaYXkrVC9LClBHcFFsVzBUQVl4NlhZUGhvNGQ5ZStDNDZlOVBYNXA5TGI2bVplWUNhdzhmcUtvSWlvMTk2dnlranJJb3FlMGoKcXFvcVBiajRKQkNYa05COXRySE1SN3dIWGJubjNrT0svSTJudVU2NjZxV0ROZ3pqVW1WNlNWWTlabWltWWIyTApDcXMwQkFzSXVOYW5PZjVFd1RTNFVnTElIUDNtNEJnSUlTU0RyWTNBY2dmUzJHSlhJdjZJWlpEYkRYUGEwZVJ0CmJYM0VYV09ob1dMYTFzNGZDNU5tU0liYVZYMWVEM3pJQ2dnZEJ2NEE2Y2NrVmRmRDBOSGhIOFQwUEprRmlCekEKc3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"producer-key-u23BFbKYM30S9af"}
- pathParams: {"producerKeychainId":"b793f054-e44a-4786-b929-6b4c225ea91f"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 41. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. Il valore di alg di 5.000 caratteri non rappresenta un identificatore di algoritmo utilizzabile. Occorre validare i valori ammessi e la loro lunghezza. La registrazione della chiave viene accettata con HTTP 204. Il difetto è tracciato in PIN-11602.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffProducerKeychainContractTest::createProducerKey su POST /producerKeychains/{producerKeychainId}/keys. Sostituire /alg con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzeDJCUnMzeVZEdGMyb0tmUjZZcgpaVlBTcFZTZGJpZ1hJYVAwakVXRUdvT1lsdEVaSGd2V0pTWWtUVlV0YWR5ZFdxVlJMc3hZdkNPQUNxQ1Z3OXVCCm4wNStCU0VNeVFGeENTMXZUcngvTHpsYnhHTmZoVGwza2ZpOVJPdTZxcmFweHJYS2V0cERDdUpBM1FpNVZFaUMKUFYwUkNxanRhc01HRlNiL002eStYNEx4SkF6U3pocmR3V2pUUG0yTEZlQXVkblhUOFVOeFFPeXgzV0VRSGNIcQozQlNoOFZrT0xFVDNBR2NSeEgwZjUvd0o2SktHWFR5ZW8zOFFKNCtpdytvWUF1SUM5ak1GZFV6cXVuTFN6T2phCld4L0VPaHBCdTNhVWErb1FLTEpUTXdCeXJSbDF3enovVUxZNnB0amRXM0N0ZEh4a3pobzNxVG4xZjNUenJNZ04KNndJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"producer-key-hoVfUsI1yATC7BE"}
- pathParams: {"producerKeychainId":"7c80bb6b-75e2-476c-9005-b0548ca41a83"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 42. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /riskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurpose su POST /purposes. Inviare un null JSON esplicito in /riskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4f074a3d-daad-4dd0-ae68-48abcf542ae0","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"eserviceId":"ce43a4a3-6912-42c3-9627-218991218e2c","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":null,"title":"purpose-94m4wXU3mIXeh0l","description":"description-b2REBXVTPzl6Fxf","isFreeOfCharge":true,"freeOfChargeReason":"reason-HqS1YY6eW7lnnel","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 43. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurpose su POST /purposes. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b6ee82a0-2754-44e0-b85e-abe369231c47","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"a20e8790-f4a7-457f-9e3f-aca3c302bad7","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-BTtPScic2ktRUq5","description":"description-jraCuWK7x61ZETM","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 44. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /riskAnalysisForm/answers/institutionalPurpose/0 viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11356.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /riskAnalysisForm/answers/institutionalPurpose/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"1e960dea-2b7d-4513-a742-911920f34093"}
- payload: {"eserviceId":"77d1507e-708e-4a3c-94ec-f03732a23b01","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-w8TnLlXJihgQ5Tx","description":"description-x6Fozkh5GPET0aw","isFreeOfCharge":true,"freeOfChargeReason":"reason-s8t5ZoyPSXFRRI0","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 45. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /freeOfChargeReason viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /freeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"b85622a4-0ad3-4af4-8d29-bdfb0380bf79"}
- payload: {"eserviceId":"ff9bc309-735c-4449-afc1-e0ff45e7dbf9","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-jpk26tlumZEkVkV","description":"description-QexV5mKpao8ppqD","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 46. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11447.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::createPurpose su POST /purposes. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"4aac6c9e-3167-453b-812e-348f1d134f5b"}
- payload: {"eserviceId":"797c02a4-3df7-4263-8356-e5b30eb24aa3","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-oLAX4e2gvG6V1Lj","description":"description-23wJzsT0pybwBMU","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 47. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"eba55974-0f1d-4da4-a699-a3584ea07037","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"4799b4a2-fa65-4c13-95d6-b8a7a1a5229c","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"bdf33b12-615e-4a15-9b8a-d2e0d4b46a0b","title":"purpose-2NlsZyFfhui9WD9","description":"description-zg56jNj9gsxaDLL","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 48. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio applicativo identifica correttamente l'assenza dell'e-service. L'operazione deve restituire HTTP 404. Il mapping a 400 è un difetto di prodotto tracciato in PIN-11376.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"9d233986-4e0f-42d3-af62-2508c49a73b3","errors":[{"code":"004-0002","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"3aae8b50-ddbd-402e-8b76-0c34de4f8967","title":"purpose-2hgs6tLK95Cxklo","description":"description-J6ZKekakrdSuh3C","isFreeOfCharge":true,"freeOfChargeReason":"reason-Bc9pYGvtdLWeOQr","dailyCalls":1}
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
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 49. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /riskAnalysisId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio applicativo identifica correttamente l'assenza dell'analisi del rischio. L'operazione deve restituire HTTP 404. Il mapping a 400 è un difetto di prodotto tracciato in PIN-11454.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /riskAnalysisId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Risk analysis not found","status":400,"detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService ef597fc3-ee2e-44ca-9355-57b7e4f29884","correlationId":"dea8b5d9-3db3-440a-b679-cdefea057206","errors":[{"code":"004-0019","detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService ef597fc3-ee2e-44ca-9355-57b7e4f29884"}]}
- payload: {"eserviceId":"ef597fc3-ee2e-44ca-9355-57b7e4f29884","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"00000000-0000-0000-0000-000000000000","title":"purpose-wi2DRNdkL4HA9gT","description":"description-6mC4VXTYn2q5guJ","isFreeOfCharge":true,"freeOfChargeReason":"reason-D75abXCrElDDgJt","dailyCalls":1}
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
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 50. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11448.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"45caece3-0f57-4b1a-a75e-55f8b3a6b26d"}
- payload: {"eserviceId":"5afe7e85-5a24-4f18-b07a-aa0aa11cce93","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"7053f7eb-4902-4b16-8586-4aa5760da00b","title":"purpose-2W1DOvPZD93yL66","description":"description-UCou1OL7UQOqcJt","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 51. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /freeOfChargeReason viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::createPurposeForReceiveEservice su POST /reverse/purposes. Sostituire /freeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"2c19ddaf-907a-4c49-a66b-41a657592f73"}
- payload: {"eserviceId":"e28da607-d118-46fe-8457-25e07852a41d","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"a4598444-5932-4f67-a3e2-8f8a82e8e979","title":"purpose-eQreoZrx21pWu9D","description":"description-0dDx8dQ5KM03KWR","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 52. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeRiskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Inviare un null JSON esplicito in /purposeRiskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"0ff5321d-4073-482a-8b59-a5f7a302162d","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-eDoxzhIZxoV7WbT5D6l9","targetTenantKind":"PA","purposeTitle":"purpose-template-sNcx3e7Gv7H8cTp","purposeDescription":"purpose-description-B6YbqamwrShomix3bvr8","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-5yOkDevq5hO6ak2icGJq","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 53. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 201. La proprietà rimossa è facoltativa e la creazione del template riesce con 201 Created, come previsto dal contratto. Il KO deriva dal default 200 applicato dalla suite e va corretto nella policy di successo dell'endpoint.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"3abf5b25-3fc2-46b4-b9e6-79caa9029082"}
- payload: {"targetDescription":"target-description-reVjKkPCndueWt0GR2AS","targetTenantKind":"PA","purposeTitle":"purpose-template-Bu78OS5s0FOVwyY","purposeDescription":"purpose-description-Y8GZiWhPf2RzLgf02pXA","purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-jZGxyXdfwzR5l5qKxJz1","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 54. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"891a8018-d1a5-4201-89c2-dad1e3aca5fb"}
- payload: {"targetDescription":"target-description-PuJfvECooyZu9tAs1ZpV","targetTenantKind":"PA","purposeTitle":"purpose-template-nIOe6xOtP5MjoE7","purposeDescription":"purpose-description-2luQBHjHwTZXxmHUnoau","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-FB9sX01qnu4JqsA9RmVO","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 55. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"ef90b435-a2d8-499c-87fb-fd4935cec5f2"}
- payload: {"targetDescription":"target-description-3MzOlsnqUBU6lwRUT8BQ","targetTenantKind":"PA","purposeTitle":"purpose-template-MON9OVTYGmkUJRF","purposeDescription":"purpose-description-9rFL6f8Qr8tUnhAZTHjF","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-9gr7t2Utxn039b2XCJCF","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 56. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/purpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"71503cae-40d8-4ea5-8cb4-a116351e0489"}
- payload: {"targetDescription":"target-description-mmB4rP3RCgBJ81rPNZOO","targetTenantKind":"PA","purposeTitle":"purpose-template-N276eU40NWUIlOO","purposeDescription":"purpose-description-0uHsAjAHO0CYNzHLpdJi","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-IEdfB0Nj5I1nRIy87ABw","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 57. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"6a1e66c3-00be-45da-b19c-6d2a282aaf4f"}
- payload: {"targetDescription":"target-description-4kozX7FEUEN7fAD24Jwy","targetTenantKind":"PA","purposeTitle":"purpose-template-qHDeDnszCN6QWsM","purposeDescription":"purpose-description-Nz7b5n0c5kCqgJlbDjrc","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-vYqrML9PbriFj5Sn06CF","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 58. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"4a26a1cd-e185-4ad2-9e72-59542e7e85af"}
- payload: {"targetDescription":"target-description-6sOTKXNRuGJrrqU6jv23","targetTenantKind":"PA","purposeTitle":"purpose-template-Yxmgm4RBJrAbVWz","purposeDescription":"purpose-description-pDkC33uWtrnyews7PTIQ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-ZaiAb97O1ok4BAORNo1j","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 59. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeDailyCalls. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Inviare un null JSON esplicito in /purposeDailyCalls, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a3062e59-1b37-4ec3-b31d-6c724e3235c8","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-YIRic4CEebN7zixgwNSM","targetTenantKind":"PA","purposeTitle":"purpose-template-2uNZ9ovpdx77DjT","purposeDescription":"purpose-description-W7ou0OUC7UCWKTyCb1Eg","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-aogWt0ifv5lEZJAknbTs","purposeDailyCalls":null,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 60. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 201. La proprietà rimossa è facoltativa e la creazione del template riesce con 201 Created, come previsto dal contratto. Il KO deriva dal default 200 applicato dalla suite e va corretto nella policy di successo dell'endpoint.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Rimuovere esclusivamente /purposeDailyCalls dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"0c97c9f5-8076-4813-911c-f811cd8fa154"}
- payload: {"targetDescription":"target-description-LMeFoDD6gSGDk3JKEsoY","targetTenantKind":"PA","purposeTitle":"purpose-template-mDyfLlaZce5l2SO","purposeDescription":"purpose-description-GYXhALnCEe0HJnD0CQX9","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-OzZWMNVZNPr6METAiztG","handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 61. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. Il template accetta e restituisce un valore suggerito composto da soli spazi. Il vincolo di lunghezza minima è soddisfatto, ma il suggerimento resta privo di contenuto visibile. Occorre rifiutare il valore con HTTP 400. Il difetto di prodotto è tracciato in PIN-11599.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"a4c95242-ac50-461a-a575-e7c6da17c511"}
- payload: {"targetDescription":"target-description-wdIGA2XHHSirXsES1FcB","targetTenantKind":"PA","purposeTitle":"purpose-template-Z4ur2stau8vLyIj","purposeDescription":"purpose-description-YqKEDBZhrzFS438z96dZ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-GReOQLWelnA6tkRH8DLT","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 62. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. Il template gratuito viene creato con una motivazione composta da soli spazi. La presenza della proprietà non soddisfa il requisito funzionale di fornire una motivazione effettiva. Occorre rifiutare il valore con HTTP 400. Il caso è coperto da PIN-11359.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeFreeOfChargeReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"51486a2d-5938-4489-933f-6dee070025e1"}
- payload: {"targetDescription":"target-description-5N5qdGyZLXIt1Ze1sx0L","targetTenantKind":"PA","purposeTitle":"purpose-template-oQCRcDMNsb9EqDR","purposeDescription":"purpose-description-X02ZnKx6iglKtBpBVlVT","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"   ","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 63. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 201. L'operazione accetta /purposeFreeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11410.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeTemplateContractTest::createPurposeTemplate su POST /purposeTemplates. Sostituire /purposeFreeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"06cbbe88-b9a5-4688-af66-306a84ac51a4"}
- payload: {"targetDescription":"target-description-Z72vOMSOIgFVp7PjVXyd","targetTenantKind":"PA","purposeTitle":"purpose-template-AySrRYhneHKB6du","purposeDescription":"purpose-description-Y0eGeFueWOPZHhTqj0tJ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 64. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200.Il campo composto da soli spazi viene accettato e restituito nella risorsa creata. Il contenuto è privo di testo visibile. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Sostituire /description con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"69f6b8e2-f348-46d6-a3ed-9aec6894cd51","kind":"VERIFIED","description":"   ","name":"name-J2kjaDRmtwQvc89K5nio6m25","creationTime":"2026-11-23T21:20:11.385Z"}
- payload: {"description":"   ","name":"name-J2kjaDRmtwQvc89K5nio6m25"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 65. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 409. Il conflitto di unicità impedisce di verificare un nuovo inserimento con il nome di soli spazi. Ripetere il caso su dati privi di collisioni. PIN-10918 raccoglie la segnalazione storica ed è indicato come Rejected nell'analisi disponibile. Il requisito di rifiuto deve essere confermato prima di attribuire il KO a una nuova regressione.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffVerifiedAttributeContractTest::createVerifiedAttribute su POST /verifiedAttributes. Sostituire /name con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name","status":409,"detail":"An attribute with name     already exists","correlationId":"d493f7e2-c11a-4302-980b-bc1141555287","errors":[{"code":"003-0002","detail":"An attribute with name     already exists"}],"userMessage":"Esiste già un attributo con questo nome. Scegli un altro nome."}
- payload: {"description":"description-hxWrd4RSd4FZ3bN2pTWfO6rvQ6dzvcOh","name":"   "}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 66. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
- payload: null
- pathParams: {"purposeTemplateId":"00000000-0000-0000-0000-000000000000"}
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
Expected status code <404> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 67. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
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
Expected status code <400> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 68. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
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
Expected status code <400> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 12 more

````


---

### 69. BffEServiceContractTest::queryEServicesCatalog::[payload] REPLACED_WITH_LONG_STRING @ /keyword

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /keyword di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11526.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffEServiceContractTest::queryEServicesCatalog su POST /catalog. Sostituire /keyword con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 70. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11605.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":""}
- pathParams: {"delegationId":"7543c138-2f1e-41e1-8559-9aa76972da19"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 71. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione composta da soli spazi non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"   "}
- pathParams: {"delegationId":"78d4c70e-c283-4932-9307-3cbf1c602af7"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 72. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11604.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
- pathParams: {"delegationId":"73e0f14d-30c4-4870-bbaa-2feb96e2270c"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 73. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- pathParams: {"delegationId":"adab26fb-fad4-4abe-b567-27b5be0e7231"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 74. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 404. La motivazione composta da soli spazi non fornisce un contenuto effettivo. La risposta di delega assente impedisce di osservare la validazione della motivazione. Occorre ripetere il caso con una delega rifiutabile. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"   "}
- pathParams: {"delegationId":"d09098bb-e944-43b8-bedb-099f4f8a2f8f"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 75. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11603.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 204
- responseBody: 
- payload: {"rejectionReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
- pathParams: {"delegationId":"dbadd6db-d588-4b0a-bbe6-95e5d1528d44"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 76. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
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
Expected status code <400> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 77. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
- payload: null
- pathParams: {"purposeTemplateId":"00000000-0000-0000-0000-000000000000"}
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
Expected status code <404> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 78. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseStatus: 415
- responseBody: {"type":"about:blank","title":"Unsupported media type","status":415,"detail":"Unsupported request body charset or encoding","errors":[{"code":"000-10035","detail":"Unsupported request body charset or encoding"}]}
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
Expected status code <400> but was <415>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 79. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isSignalHubEnabled. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isSignalHubEnabled, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"e3e057c5-85f0-456c-a721-49a737f34f5d","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-lLDdO465FBuHGAz","description":"description-KBKsgrnQwFBQ1UhKRkMA","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"7548e8c5-ce98-424a-a486-a10e37fc4067"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 80. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isConsumerDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isConsumerDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f720cde6-d8f1-443c-8760-7bc1c0c16b24","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-qsdoLwEINxr7Ppt","description":"description-j2Y5HXNqrZvHm001MGMy","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"c6a7ba43-b42d-465c-a774-76b896c04bdf"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 81. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /isClientAccessDelegable. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /isClientAccessDelegable, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b1ebf3f8-4168-49ba-bc58-78512f96bc2f","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-N2tGzifV4ixaCyV","description":"description-RHbGGv3YoNgnbHx8JX3Z","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"53f3de77-efa5-4130-8b31-1b28820537da"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 82. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /personalData

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /personalData. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /personalData, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d79990c0-2b0a-4f13-bd66-e52ada17ef51","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-TXOKGOE8NQDBU7s","description":"description-1gJvnVSzjb6QE7slth0Z","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
- pathParams: {"eServiceId":"58727a20-020d-47a9-b092-a4d4a258fa51"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 83. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /asyncExchange

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /asyncExchange. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffEServiceContractTest::updateEServiceById su PUT /eservices/{eServiceId}. Inviare un null JSON esplicito in /asyncExchange, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b9a2e0db-038e-4295-992d-3621b10c47ff","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-l7NqCGAuhJ5jOYu","description":"description-Logs7MV4LB1KxDInY2wO","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
- pathParams: {"eServiceId":"67f0f6dc-828a-4263-b110-252ccf16aa8c"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 84. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4d74d070-7a96-4bcc-9a6e-2401e0c7fca9","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-2bwnYjvcPfNNZJ9","description":"description-C999RCVYnTbE4Ck","isFreeOfCharge":true,"freeOfChargeReason":null,"riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"5ea91335-07dd-47e9-b406-62840ce0d922"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 85. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /riskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Inviare un null JSON esplicito in /riskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"05523c2b-5be3-4041-a6ff-691b36dafa72","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"title":"purpose-sWIeP2aMXRLUVO8","description":"description-GQfNU4wSo7tHIts","isFreeOfCharge":true,"freeOfChargeReason":"reason-wdUq8HqzN2rRuQE","riskAnalysisForm":null,"dailyCalls":1}
- pathParams: {"purposeId":"7157eb7c-3f06-4135-a9fe-89a1d8977328"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 86. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/institutionalPurpose

**RESULT**

Il salvataggio della bozza restituisce HTTP 200, compatibile con lo schema della mappa answers e con l'assenza di minItems negli array. L'attivazione viene bloccata con HTTP 400 e codice 004-0014 per risposta mancante o vuota. La validazione funzionale interviene all'attivazione. Il KO deriva dal 400 atteso dalla suite durante l'aggiornamento e richiede un'attesa di 200 per il salvataggio. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Eseguire REMOVED su /riskAnalysisForm/answers/institutionalPurpose in una finalità in bozza con questionario inizialmente completo. Rileggere la finalità per verificare la risposta rimossa o l'array svuotato. Tentare l'attivazione mantenendo valide le altre precondizioni e leggere il motivo del rifiuto.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"purposeId":"c9764814-cc5d-48c2-b196-b6ef0130f3dc","versionId":"f58eea3b-b681-4a45-a221-5b78b12cbe20"}
- payload: {"title":"purpose-iwzIf49TkONHpKn","description":"description-OIjfD7Pcpyehv6f","isFreeOfCharge":true,"freeOfChargeReason":"reason-6XNO4ybULJZKibk","riskAnalysisForm":{"version":"3.2","answers":{"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"c9764814-cc5d-48c2-b196-b6ef0130f3dc"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 87. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11449.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"purposeId":"1f6f703e-57ca-4661-84f3-b9f9002205f6","versionId":"de0120f3-5e25-4099-b971-52179bf578f6"}
- payload: {"title":"purpose-Pnga1c0T0G7PZsM","description":"description-BDWbzY41TZdaQR2","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"1f6f703e-57ca-4661-84f3-b9f9002205f6"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 88. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il valore di soli spazi in /riskAnalysisForm/answers/institutionalPurpose/0 viene accettato, ma non fornisce il contenuto richiesto dalla domanda o dalla motivazione di gratuità. Il difetto di prodotto è tracciato in PIN-11356.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeContractTest::updatePurpose su POST /purposes/{purposeId}. Sostituire /riskAnalysisForm/answers/institutionalPurpose/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"purposeId":"0b67024a-03ab-4471-9da8-f60db7298a66","versionId":"f39a610f-63e8-4868-a9ff-7d4b7facc013"}
- payload: {"title":"purpose-Hje5Iz5ZKf80iSL","description":"description-GFJI00r6kE7sonH","isFreeOfCharge":true,"freeOfChargeReason":"reason-JsBQWpfUJrfr1Gl","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"0b67024a-03ab-4471-9da8-f60db7298a66"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 89. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeRiskAnalysisForm. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeRiskAnalysisForm, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"93cff6ac-52d7-4579-a74c-b1d474d70441","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-7Qnyl5ngn0XcAJRh9JYJ","targetTenantKind":"PA","purposeTitle":"purpose-template-mvhHNBmFTUTcPEz","purposeDescription":"purpose-description-a39MipnANoWBtphqueFG","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-U7yH3rXqlgEYBerqvaFb","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"74052c0b-6352-4739-8c37-b0d8697505ae"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 90. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"ee4c002e-8357-4dc9-9c76-e42aa6886c49","targetDescription":"target-description-CMXDPpUZDXNku72GAKDY","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:24:05.163Z","updatedAt":"2026-11-23T21:24:07.700Z","purposeTitle":"purpose-template-r5pAaqtVuLdciYt","purposeDescription":"purpose-description-PL0Z4uSKkC0EBATmE70H","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"fbb4f76b-dbf7-446d-8034-8480f0b4a8c5","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"e444843c-cca0-43a9-8aab-b2b061b7717f","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"0798b36b-eb9d-49ad-99db-9d63bdb47978","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"5ca1884b-a542-447b-9ab1-52ffc87126dd","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"e9f38fa7-7280-4d00-a98b-2dc74a2ddb89","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-55DLSTvLbALAajPe5imw","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-CMXDPpUZDXNku72GAKDY","targetTenantKind":"PA","purposeTitle":"purpose-template-r5pAaqtVuLdciYt","purposeDescription":"purpose-description-PL0Z4uSKkC0EBATmE70H","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-55DLSTvLbALAajPe5imw","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"ee4c002e-8357-4dc9-9c76-e42aa6886c49"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 91. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"24d39574-56ed-4ab0-a3a6-c3a1fa82d5fc","targetDescription":"target-description-aCE5t6QOHmdJdBde7hKj","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:24:53.937Z","updatedAt":"2026-11-23T21:24:56.231Z","purposeTitle":"purpose-template-4isJYFa34h8MxJS","purposeDescription":"purpose-description-h3BcEQz3k8R01SpdElTe","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"b248109f-2174-4352-a125-1fa3fed7d0ef","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"a361a1f4-c34d-4577-ae4f-7118ab70dc62","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"31c1d7cd-e3cb-40ba-ba45-dfe2f57d4da7","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"f9725718-a90b-4c8c-85f5-28072b793014","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"6f40ed2d-3a2a-4fdc-839f-73d7e6c9866e","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-3BXwyPZobF689AnKnCBr","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-aCE5t6QOHmdJdBde7hKj","targetTenantKind":"PA","purposeTitle":"purpose-template-4isJYFa34h8MxJS","purposeDescription":"purpose-description-h3BcEQz3k8R01SpdElTe","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-3BXwyPZobF689AnKnCBr","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"24d39574-56ed-4ab0-a3a6-c3a1fa82d5fc"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 92. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/purpose/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"aba1b41c-1c54-46c1-a74c-818f3ffdcd49","targetDescription":"target-description-Q6Efn4EcEAiSOnw2QDLX","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:25:33.074Z","updatedAt":"2026-11-23T21:25:35.362Z","purposeTitle":"purpose-template-41RiwbSdkIR9gup","purposeDescription":"purpose-description-D1N1XbVp6pnYrk7ckuFu","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"8bec3598-ad0b-420f-8c52-7a3d00130a32","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"ad9247b6-f7c5-4379-a2dd-e2d2eab1398e","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"a1791da8-c411-4285-ba70-aa304f342ffe","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"8bec78bb-c207-49b3-8755-ec8f7226167c","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"423617de-7155-49ee-8103-1ec8e378d46d","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-cd4fHTqE8gkG3gOQAEnJ","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-Q6Efn4EcEAiSOnw2QDLX","targetTenantKind":"PA","purposeTitle":"purpose-template-41RiwbSdkIR9gup","purposeDescription":"purpose-description-D1N1XbVp6pnYrk7ckuFu","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-cd4fHTqE8gkG3gOQAEnJ","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"aba1b41c-1c54-46c1-a74c-818f3ffdcd49"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 93. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"2129cb10-89f6-4665-9a64-3cb153e89d79","targetDescription":"target-description-Fk1aFOj4td7UYQrzAduK","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:26:14.178Z","updatedAt":"2026-11-23T21:26:16.448Z","purposeTitle":"purpose-template-WlLtICKtaJGHGLW","purposeDescription":"purpose-description-jtyLlyTDniAGo5wp15QE","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"75a4a28b-1436-4947-b2de-57172bca3f3d","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"4d589885-8686-4e88-8b1d-87c70b5c65c3","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"90e39cde-2b9b-43e0-b33b-bd271784aad3","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"ee9ce253-2b78-473f-ab4b-d39abd926177","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"63619429-1fe8-4325-8dfd-de319eeabff8","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-KtHjl2hgXctZg0S0H3fi","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-Fk1aFOj4td7UYQrzAduK","targetTenantKind":"PA","purposeTitle":"purpose-template-WlLtICKtaJGHGLW","purposeDescription":"purpose-description-jtyLlyTDniAGo5wp15QE","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-KtHjl2hgXctZg0S0H3fi","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"2129cb10-89f6-4665-9a64-3cb153e89d79"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 94. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. annotation è una proprietà facoltativa della risposta nel questionario. La suite non risolve correttamente lo schema del valore all'interno della mappa e applica POLICY_UNKNOWN, imponendo 400. Il salvataggio è compatibile con il contratto. Occorre correggere la risoluzione dello schema e l'aspettativa del test.

**PROCEDURE**

Eseguire il solo scenario REMOVED di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Rimuovere esclusivamente /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation dal payload di partenza. Se il percorso identifica un elemento, verificare il contenuto del contenitore dopo la rimozione. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"43bdecd9-c025-46e8-b1d7-e8bfbd6f0160","targetDescription":"target-description-oI88DyrgUJrAQDJvzHR4","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:26:56.956Z","updatedAt":"2026-11-23T21:26:57.821Z","purposeTitle":"purpose-template-mpI4kUY6WPcSqhS","purposeDescription":"purpose-description-CQjM4ll4WT08EyHgoZTS","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"c142b462-3071-4d37-af21-deae2e2dd5a3","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"a28888c8-2ccb-491b-9ab7-51b80b7fa65e","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"c08f0909-3917-4ec6-bdd3-6bdaf819e1dc","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"484464d3-9cb6-49f2-95dd-a0f3c2b7a0e4","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"ba7ff4ef-5c36-4144-ba33-697efd560e6f","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-EVAyqjlMqmOUdhYTnWCN","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-oI88DyrgUJrAQDJvzHR4","targetTenantKind":"PA","purposeTitle":"purpose-template-mpI4kUY6WPcSqhS","purposeDescription":"purpose-description-CQjM4ll4WT08EyHgoZTS","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-EVAyqjlMqmOUdhYTnWCN","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"43bdecd9-c025-46e8-b1d7-e8bfbd6f0160"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 95. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeFreeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeFreeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a4d629ad-5eb6-4afe-8e6b-7ba5c4dfb484","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"purposeFreeOfChargeReason\""}]}
- payload: {"targetDescription":"target-description-EdiW5rUGhotkdNwPHcmV","targetTenantKind":"PA","purposeTitle":"purpose-template-n81DbTB8U1hx8fT","purposeDescription":"purpose-description-tQ1b5QcN6qk8DjgiR9Yi","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":null,"purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"2471aee2-8107-4908-b76a-bf7a6f7aaff8"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 96. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /purposeDailyCalls. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Inviare un null JSON esplicito in /purposeDailyCalls, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ebd6a3de-4642-4929-bfc1-60113497eb75","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-VVgHuxktdicMwlya8T9Y","targetTenantKind":"PA","purposeTitle":"purpose-template-RZaQ72qGyAYjC2a","purposeDescription":"purpose-description-UTZQG3n7RLdC6CU2Bi0P","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-3Z7IlJD5hQpkVfdDXQV3","purposeDailyCalls":null,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"d0760565-036b-456e-a92d-a35bb15da618"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 97. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. Il template accetta e restituisce un valore suggerito composto da soli spazi. Il vincolo di lunghezza minima è soddisfatto, ma il suggerimento resta privo di contenuto visibile. Occorre rifiutare il valore con HTTP 400. Il difetto di prodotto è tracciato in PIN-11606.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Sostituire /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0 con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"cd95f8c6-d4bc-4aec-b895-91231ba7cdc8","targetDescription":"target-description-zZiSiXkGtHrmalhqI6os","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:28:39.408Z","updatedAt":"2026-11-23T21:28:41.677Z","purposeTitle":"purpose-template-43IId9Xt4OSo9dq","purposeDescription":"purpose-description-zE1romYQHSeIlM0d46XX","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"2f32d78b-6c3d-477e-b097-7e6f048fdd8d","values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"id":"ea17a06d-e56c-49e2-9c2a-621eff57ad66","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"c2369550-bc74-4622-b16d-360827b32f75","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"0b798c0d-7933-49f5-bef0-1c47dd7b497d","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"31f90d30-937b-4a02-b78d-68872e1fea13","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-C7Gwmk45R4vAu7tRcbPH","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-zZiSiXkGtHrmalhqI6os","targetTenantKind":"PA","purposeTitle":"purpose-template-43IId9Xt4OSo9dq","purposeDescription":"purpose-description-zE1romYQHSeIlM0d46XX","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-C7Gwmk45R4vAu7tRcbPH","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"cd95f8c6-d4bc-4aec-b895-91231ba7cdc8"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 98. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /purposeFreeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11450.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeTemplateContractTest::updatePurposeTemplate su PUT /purposeTemplates/{purposeTemplateId}. Sostituire /purposeFreeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"id":"e9c58371-3f8d-4211-8652-07dadf849122","targetDescription":"target-description-p4RCWcucBIBRJ2YKqtm1","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-23T21:30:03.435Z","updatedAt":"2026-11-23T21:30:05.693Z","purposeTitle":"purpose-template-rJR6LyAnM7fBbGO","purposeDescription":"purpose-description-Zh2viDkb9GsWWIi8BMAp","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"f44fddc7-7718-4035-a57c-e76d3fdea8d8","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"acb741c6-faaa-4c7f-bad9-78f30082cd1d","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"9ce9ae97-f76a-4cc9-8536-0a8016fdbbbc","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"cbdefa4d-04e3-43f2-92d1-14ba8e774e61","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"5b79ac88-5d76-4f76-96a0-f6f7891c495e","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-p4RCWcucBIBRJ2YKqtm1","targetTenantKind":"PA","purposeTitle":"purpose-template-rJR6LyAnM7fBbGO","purposeDescription":"purpose-description-Zh2viDkb9GsWWIi8BMAp","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"e9c58371-3f8d-4211-8652-07dadf849122"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 99. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 200; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /freeOfChargeReason. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Inviare un null JSON esplicito in /freeOfChargeReason, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"96fb8fe8-4b58-46ca-9d93-9ca76a70098d","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-KrF2OP7wR27Cf8A","description":"description-cr407nfqArTk41b","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: {"purposeId":"828f9760-8370-4b29-93f1-493b9f227d99"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 100. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /freeOfChargeReason di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11451.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffPurposeContractTest::updateReversePurpose su POST /reverse/purposes/{purposeId}. Sostituire /freeOfChargeReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"purposeId":"18943dd6-31c0-4e4c-973b-99c32a1d2090","versionId":"2d6c12d2-a663-424e-82a2-5f9a7d6d0795"}
- payload: {"title":"purpose-Ui6drFNUB18cRZp","description":"description-WsH0KxGh0jpdahX","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: {"purposeId":"18943dd6-31c0-4e4c-973b-99c32a1d2090"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 101. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

La suite attende HTTP 201; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /delegationId. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8bf8a3be-bcae-4a73-830e-8e9f99cd8600","errors":[{"code":"017-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"d4e4c1e3-ead1-4678-9d37-5b7cdfa8d8f6","descriptorId":"842a066e-4660-4ec1-a740-16d2c562d5e0","delegationId":null}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 102. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio segnala un e-service inesistente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11351.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"e974d52d-82fe-4139-8894-caac9450dd39","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"29afeeb9-43bc-47ee-9e31-f9fb71e65f97"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 103. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il descriptorId inesistente viene descritto come versione non più recente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11352.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /descriptorId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"ac758cf2-d333-4238-856b-fa28e4896364","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}]}
- payload: {"eserviceId":"ad5a772c-67a6-4de6-95cd-56f762a7dd61","descriptorId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 104. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio segnala una delega inesistente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11350.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /delegationId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"563702f1-6e9b-4af1-9a14-d93f7b817cfd","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"213460c4-4028-43f1-8b07-91497d003bb1","descriptorId":"51064fc8-053f-465d-b4f8-c8ddb34e2c42","delegationId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 105. WebConsumerPurposeDetailsPageContractTest::consumerPurposeDetailsPageContract::Consumer purpose details page shows the expected content and filters

**RESULT**

Errore restituito dopo aggiornamento del FE che ha portato alla rimozione della description della purpose che era originariamente riportata in due luoghi distinti prima della modifica più recente.

**PROCEDURE**

Log analizzato. Test ripetuto in locale e il problema si riscontra anche in locale org.assertj.core.error.AssertJMultipleFailuresError: Multiple Failures (1 failure) -- failure 1 -- [Purpose description] expected: "description-PCqzYWfN6Hn7oTs" but was: null at WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$1(WebConsumerPurposeDetailsPageContractTest.java:81) Dopo confronto con tester che ha lavorato sul test in questione, è emerso che la struttura della pagina è cambiata. Nella versione precedente la descrizione era riportata in due parti : una vicina al titolo (h1) della pagina ed una ulteriore riportata tra i dettagli della purpose. Analizzando la versione più recente del FE si riscontra che la descrizione è presente solo tra i dettagli della purpose, ma non più vicina al titolo della pagina.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19689

**ERROR LOG**

````text
org.assertj.core.error.AssertJMultipleFailuresError: 
Multiple Failures (1 failure)
-- failure 1 --
[Purpose description] 
expected: "description-s8qpwCZvOSGDBCX"
 but was: null
at WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$1(WebConsumerPurposeDetailsPageContractTest.java:81)
	at it.pagopa.interop.suite.contract.WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$2(WebConsumerPurposeDetailsPageContractTest.java:71)

````


---

### 106. WebProducerEServiceTemplateDetailsPageContractTest::producerEServiceTemplateDetailsPageContract::Producer e-service template details page shows expected content in Details tab

**RESULT**

Un elemento Link non viene trovato nella pagina al suo caricamento dal test. XPath correntemente usato risulta corretto facendo il check manuale sul HTML della pagina. Si desume sia un problema di flakiness che non dovrebbe capitare dando più tempo per caricare gli elementi.

**PROCEDURE**

Log analizzato. Test ripetuto con run in locale e il problema si riscontra anche in locale. Effettuato anche check manuale sulla pagina controllando se XPath usato sia corretto e la risorsa URL (tag &lt;a&gt;) viene correttamente trovata sulla pagina in ambiente dev e in ambiente qa. Si desume che possa essere un problema di mancato caricamento entro i 20 secondi di polling.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19690

**ERROR LOG**

````text
java.util.NoSuchElementException: Impossibile ottenere l'attributo href del link
	at it.pagopa.infrastructure.suit.component.Link.lambda$getHref$1(Link.java:14)
	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
	at it.pagopa.infrastructure.suit.component.Link.getHref(Link.java:14)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy325.getHref(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerEServiceTemplateDetailsPageContractTest.lambda$contractScenarios$2(WebProducerEServiceTemplateDetailsPageContractTest.java:216)
	at org.assertj.core.api.SoftAssertionsProvider.assertSoftly(SoftAssertionsProvider.java:122)
	at org.assertj.core.api.SoftAssertions.assertSoftly(SoftAssertions.java:165)
	at it.pagopa.interop.suite.contract.WebProducerEServiceTemplateDetailsPageContractTest.lambda$contractScenarios$3(WebProducerEServiceTemplateDetailsPageContractTest.java:81)

````


---

### 107. WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: 11 sezioni ordinate, titoli esatti e paragrafi non vuoti

**RESULT**

La sezione Proprietà intellettuale viene visualizzata con il numero 8, mentre la sequenza attesa prevede il numero 9. Il difetto riguarda la numerazione dei termini di servizio ed è tracciato come prodotto in PIN-11378. Mantenere l'aspettativa coerente con la numerazione prevista.

**PROCEDURE**

Confrontare indice e intestazioni visibili dei termini di servizio con la revisione pubblicata e con le costanti usate da assertSections.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11378

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Titolo: Proprietà intellettuale] 
expected: "9. Proprietà intellettuale"
 but was: "8. Proprietà intellettuale"
	at it.pagopa.interop.suite.contract.WebTOSContractTest.assertSections(WebTOSContractTest.java:130)

````


---

### 108. WebProducerAgreementContractTest::shouldShowAgreementDetailToProducer::richiesta di fruizione ricevuta visibile all'utente API del Comune di Milano

**RESULT**

Il risultato desunto dai test manuali è che gli utenti API non possono accedere alla pagina di dettaglio di richiesta fruizione con errore 403 Forbidden { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "834b1173-ea4e-45e3-b25d-9a458f0d5e76", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi dei log ed esecuzione in locale del test. Oltre a questi esecuzione manuale del test sia in ambiente DEV, sia QA con ottenimento di mancato caricamento delle pagine richieste inerenti ambito "erogazione/richieste" per gli utenti API.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11633

**ERROR LOG**

````text
java.lang.AssertionError: Expected Gestisci richiesta di fruizione found null
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy259.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementDetailPage.assertLoaded(ProducerAgreementDetailPage.java:31)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy257.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:45)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
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

````


---

### 109. WebEServiceContractTest::shouldShowEServiceDetailToProducer::nome dell'e-service creato visibile all'utente API del Comune di Milano

**RESULT**

Dato un esito positivo nella esecuzione in locale il problema viene imputato ad un mancato caricamento tempestivo dei componenti che non sono stati trovati

**PROCEDURE**

Analisi del log e riesecuzione in locale del test. Il problema non viene riscontrato con la esecuzione in locale. Classificato come problema di flakiness

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19705

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Breadcrumbs] 
Expecting actual:
  []
to contain exactly (and in same order):
  ["Erogazione", "I miei e-service", "Visualizza e-service"]
but could not find the following elements:
  ["Erogazione", "I miei e-service", "Visualizza e-service"]

	at it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage.assertLoaded(ProducerEServiceDetailPage.java:83)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy265.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceContractTest.lambda$shouldShowEServiceDetailToProducer$5(WebEServiceContractTest.java:96)

````


---

### 110. WebAgreementRequestPaginationContractTest::shouldShowPaginationNavbarRegardlessOfOffset::offset elevato mantiene la navbar visibile a fine pagina

**RESULT**

La verifica della barra viene eseguita subito dopo la navigazione con offset elevato, mentre il caricamento della lista non è ancora confermato. Il precedente controllo del titolo riguarda la navigazione iniziale. Occorre attendere il caricamento successivo e ripetere la verifica della presenza della paginazione prima di controllarne l'usabilità. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Aprire la lista delle richieste, quindi navigare nuovamente con offset superiore ai risultati disponibili. Attendere il completamento del caricamento della lista e verificare, tramite polling, che la barra di paginazione sia presente. Controllarne quindi l'usabilità e la possibilità di tornare a una pagina valida.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Pagination navbar is present] 
Expecting value to be true but was false
	at it.pagopa.interop.suite.contract.WebAgreementRequestPaginationContractTest.lambda$highOffsetScenario$3(WebAgreementRequestPaginationContractTest.java:105)

````


---

### 111. WebProducerPurposeContractTest::shouldShowPurposeDetailToProducer::finalità ricevuta visibile all'utente API del Comune di Milano

**RESULT**

Problema di accesso per il ruolo API alla pagina suddetta con errore di 403 forbidden. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "381dac17-a676-47d2-b197-2597a9cb4941", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi dei log e riesecuzione in locale. Il problema viene riscontrato anche con una esecuzione in locale solo per utenti API. Facendo test manuali in DEV e QA si riscontrano problemi di accesso per il ruolo API alla pagina suddetta con errore di 403 forbidden.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11634

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Breadcrumbs] 
Expecting actual:
  []
to contain exactly (and in same order):
  ["Erogazione", "Finalità ricevute", "Visualizza finalità"]
but could not find the following elements:
  ["Erogazione", "Finalità ricevute", "Visualizza finalità"]

	at it.pagopa.interop.web.purpose.infrastructure.page.ProducerPurposeDetailPage.assertLoaded(ProducerPurposeDetailPage.java:22)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy353.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerPurposeContractTest.lambda$shouldShowPurposeDetailToProducer$4(WebProducerPurposeContractTest.java:86)

````


---

### 112. WebProducerAgreementContractTest::shouldShowReceivedAgreementListToProducer::richieste di fruizione ricevute accessibili all'utente API del Comune di Milano

**RESULT**

Problema di accesso per gli operatori API a questa pagina. Di seguito si riporta errore riscontrato da browser. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "0f952e03-84f8-422a-aba1-20ad4c90f54c", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi del log e riesecuzione in locale con fallimento. Eseguendo il test manualmente sia in DEV, sia in QA, si manifesta un problema di accesso (403 Forbidden) per gli operatori API a questa pagina.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11635

**ERROR LOG**

````text
java.lang.AssertionError: Expected Richieste di fruizione ricevute found null
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy259.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementListPage.assertLoaded(ProducerAgreementListPage.java:20)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy302.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:48)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
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

````


---

### 113. WebProducerPurposeContractTest::shouldShowReceivedPurposeListToProducer::finalità ricevute visibili all'utente API del Comune di Milano

**RESULT**

Problema di accesso per gli operatori API a questa pagina. Di seguito si riporta errore riscontrato da browser. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "ffa80c07-b1f2-4ddd-85cf-b839216dddd6", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi del log e riesecuzione in locale con fallimento. Eseguendo il test manualmente sia in DEV, sia in QA, si manifesta un problema di accesso (403 Forbidden) per gli operatori API a questa pagina.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11636

**ERROR LOG**

````text
java.lang.AssertionError: Expected Finalità ricevute found null
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy259.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.purpose.infrastructure.page.ProducerPurposeListPage.assertLoaded(ProducerPurposeListPage.java:29)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy352.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerPurposeContractTest.lambda$shouldShowReceivedPurposeListToProducer$1(WebProducerPurposeContractTest.java:54)

````

</details>
