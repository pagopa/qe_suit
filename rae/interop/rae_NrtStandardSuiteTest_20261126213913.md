# 20261126 Report Analisi Esiti (RAE) NrtStandardSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release |  |
| Scope | NIGHTLY |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19834 |
| Data e ora | 2026-11-26T21:09:17Z |
| Durata | 00:29:09 |
| Numero KO | 120 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/45895960310/attempts/1 |
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

## 22 - KO DOVUTI A BUG DI PRODOTTO NOTI

<details>
<summary>Mostra/nascondi 22 KO</summary>

### 1. [CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per il fruitore [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il test CONSUMER_AGREEMENT_DOWNLOAD_1 è fallito durante la verifica iniziale del pulsante "Scarica richiesta di fruizione" nella pagina di dettaglio del fruitore.

Dopo le azioni "Attiva" e "Conferma" eseguite dall’erogatore, il selettore non ha individuato il pulsante nella sezione "Informazioni generali", neanche dopo circa 20 secondi di tentativi.

Il test non ha raggiunto la successiva verifica di abilitazione del pulsante.

I log non confermano però lo stato ACTIVE né lo stato del documento al momento del KO.

**PROCEDURE**

Ricostruire la sequenza fino al fallimento, verificando le azioni di approvazione dell’erogatore e la successiva navigazione al dettaglio della richiesta come fruitore. Esaminare quindi il selettore cercato, i tentativi effettuati e lo stack trace, per stabilire se il KO avviene sulla presenza del pulsante o sulla sua abilitazione dopo l’attesa.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy275.isDisabled(Unknown Source)
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
	at jdk.proxy2/jdk.proxy2.$Proxy154.verifyConsumerAgreementDownloadAvailability(Unknown Source)
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

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy275.isDisabled(Unknown Source)
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
	at jdk.proxy2/jdk.proxy2.$Proxy154.verifyProducerAgreementDownloadAvailability(Unknown Source)
	at it.pagopa.interop.common.agreement.application.AgreementUseCase.verifyProducerAgreementDownloadAvailability(AgreementUseCase.java:34)
	at it.pagopa.interop.common.agreement.infrastructure.cucumber.AgreementSteps.verifyAgreementDownloadAvailabilityForProducer(AgreementSteps.java:55)
	at ✽.l'erogatore può scaricare il documento della richiesta di fruizione solo quanto è pronto(classpath:feature-expanded/agreement/download-agreement.feature:17)

````


---

### 3. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.1] - Impossibilità di archiviare un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il rifiuto dell'archiviazione è corretto. L'e-service non può essere archiviato con 60 giorni di preavviso perché una sua versione è già in fase di archiviazione con 120 giorni di preavviso. Atteso da Figma: la snackbar mostra solo il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» Osservato nella UI: la snackbar mostra il messaggio atteso, seguito dalle istruzioni per contattare l'assistenza e da un identificativo copiabile. Il test confronta il testo rilevato nella snackbar con il messaggio atteso tramite un'asserzione di uguaglianza. Il testo aggiuntivo rende le due stringhe diverse e fa fallire il test.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.1. Consultare lo stack trace e confrontare il testo atteso con quello rilevato da WebEServiceGateway.assertArchivingRejection per il caso ARCHIVING_BEFORE_DESCRIPTOR. Durante la riesecuzione, verificare che l'archiviazione venga rifiutata e confrontare il contenuto della snackbar mostrata a video con il design Figma di riferimento. Individuare le parti del messaggio visualizzato che differiscono dal design.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11178

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.
  Se il problema non si risolve, contatta l’assistenza e indica il codice qui sotto."
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.assertArchivingRejection(WebEServiceGateway.java:156)
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.shouldFailToArchiveEService(WebEServiceGateway.java:100)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy161.shouldFailToArchiveEService(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToArchiveEService(EServiceUseCase.java:61)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchiving(EServiceSteps.java:62)
	at ✽.il sistema rifiuta l'archiviazione dell'e-service da parte dell'erogatore con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:20)

````


---

### 4. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

**RESULT**

Il sistema rifiuta correttamente la richiesta del delegato con 60 giorni di preavviso, perché l'archiviazione dell'e-service precederebbe quella di una versione già pianificata con 120 giorni di preavviso. Da Figma ci si aspetta il messaggio «Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.» La snackbar mostra invece «Non è stato possibile richiedere l’archiviazione di questo e-service. Riprova più tardi.», seguito dalle istruzioni per contattare l'assistenza e da un identificativo. Al delegato viene quindi suggerito di riprovare più tardi, mentre per procedere deve selezionare un preavviso più lungo. Il test confronta il testo rilevato nella snackbar con quello atteso tramite un'asserzione di uguaglianza. Il confronto fallisce già sul messaggio principale, che è diverso da quello previsto.

**PROCEDURE**

Rieseguire in locale ESERVICE_ARCHIVING_GRACE_PERIOD_1.2 e consultare lo stack trace. Confrontare il testo atteso per ARCHIVING_BEFORE_DESCRIPTOR con quello rilevato da WebEServiceGateway.assertArchivingRejection. Durante la riesecuzione, verificare che la richiesta di archiviazione del delegato venga rifiutata. Confrontare la snackbar mostrata a video con il design Figma e verificare se il messaggio indica il motivo del rifiuto e l'azione da intraprendere.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11179

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [EService archiving rejection: ARCHIVING_BEFORE_DESCRIPTOR] 
expected: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
 but was: 
  "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga.
  Se il problema non si risolve, contatta l’assistenza e indica il codice qui sotto."
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.assertArchivingRejection(WebEServiceGateway.java:156)
	at it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway.shouldFailToRequestEServiceArchiving(WebEServiceGateway.java:112)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy161.shouldFailToRequestEServiceArchiving(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceUseCase.shouldFailToRequestEServiceArchiving(EServiceUseCase.java:65)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceSteps.rejectEServiceArchivingRequest(EServiceSteps.java:69)
	at ✽.il sistema rifiuta la richiesta di archiviazione dell'e-service da parte del delegato con un periodo di preavviso di 60 giorni e una motivazione valida(classpath:feature-expanded/eservice/archive-eservice.feature:36)

````


---

### 5. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

Il BFF restituisce HTTP 400 con codice 002-0005 e dettaglio "EService not found". La suite attende HTTP 404 e l'asserzione fallisce per la differenza di status.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"ac3fdb6c-4001-4306-b13f-a1deb59e64c2","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"1ea0f8e9-3c66-492b-aede-350978147b7d"}
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

### 6. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

**RESULT**

Per il descriptorId inesistente, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0021 e il messaggio "Descriptor provided is not the latest descriptor". Il messaggio presenta il descrittore come una versione non aggiornata, mentre l'ID inviato non corrisponde a un descrittore esistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /descriptorId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"02c0edf7-fe91-48db-8f6b-ae2b9aa7e169","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}],"userMessage":"Non puoi richiedere la fruizione di questa versione perché non è la più recente. Aggiorna la pagina e, se disponibile, crea una richiesta per l'ultima versione."}
- payload: {"eserviceId":"886da013-0f57-48a6-ac68-d8a3a010ecdf","descriptorId":"00000000-0000-0000-0000-000000000000"}
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

### 7. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

**RESULT**

Per la delega inesistente indicata nel body, la suite attende HTTP 404. Il BFF restituisce HTTP 400 con codice 002-0026 e messaggio "Delegation not found". Il BFF identifica correttamente la delega come inesistente. L'asserzione fallisce perché la suite attende 404 e riceve 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffAgreementContractTest::createAgreement su POST /agreements. Sostituire /delegationId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"fd26a654-bff8-4e35-9a25-0de1895e8119","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"3418f30a-feb9-4708-8437-cb96a3425692","descriptorId":"2ade5b31-c19e-4fb9-a7ba-b7421b5c6f4e","delegationId":"00000000-0000-0000-0000-000000000000"}
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

### 8. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	... 12 more

````


---

### 9. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 10. BffEServiceContractTest::queryEServicesCatalog::[payload] REPLACED_WITH_LONG_STRING @ /keyword

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 200. L'operazione accetta /keyword di 5.000 caratteri. Il campo non dispone di un limite massimo nello schema fornito. Il difetto di prodotto consiste nella mancata limitazione della lunghezza; occorre definire il limite funzionale e applicarlo nel contratto e nella validazione. Il caso è tracciato in PIN-11526.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffEServiceContractTest::queryEServicesCatalog su POST /catalog. Sostituire /keyword con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 11. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- pathParams: {"delegationId":"9c703e18-052d-4628-8938-07b1426da5d6"}
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

### 12. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 404. La motivazione composta da soli spazi non fornisce un contenuto effettivo. La risposta di delega assente impedisce di osservare la validazione della motivazione. Occorre ripetere il caso con una delega rifiutabile. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- pathParams: {"delegationId":"36f24191-ca88-41b0-8757-a4fbc3400334"}
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

### 13. BffDelegationContractTest::rejectProducerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11603.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectProducerDelegation su POST /producers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- pathParams: {"delegationId":"ecf9f895-36f9-4e54-afad-8ad9db45d678"}
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

### 14. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. L'UUID nil richiede una risposta 404 per il template assente. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 15. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 16. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11456.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_MALFORMED_UUID di BffPurposeTemplateContractTest::unsuspendPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/unsuspend. Sostituire /purposeTemplateId con un valore che non rispetta il formato UUID. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 17. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

**RESULT**

La suite attende HTTP 404; il BFF restituisce HTTP 400. Il dettaglio segnala un e-service inesistente. Il caso richiede la gestione della risorsa assente con HTTP 404 ed è tracciato in PIN-11351.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NIL_UUID di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Sostituire /eserviceId con l'UUID nil e mantenere validi gli altri riferimenti. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"819d6e60-128c-4427-a684-61ba3d235351","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"8cb37920-9b1b-47fe-9b78-92a8579a37df"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 18. WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: 11 sezioni ordinate, titoli esatti e paragrafi non vuoti

**RESULT**

La sezione Proprietà intellettuale viene visualizzata con il numero 8, mentre la sequenza attesa prevede il numero 9. Il difetto riguarda la numerazione dei termini di servizio ed è tracciato come prodotto in PIN-11378. Mantenere l'aspettativa coerente con la numerazione prevista.

**PROCEDURE**

Confrontare indice e intestazioni visibili dei termini di servizio con la revisione pubblicata e con le costanti usate da assertSections.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11378

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Titolo: Proprietà intellettuale] 
expected: "9. Proprietà intellettuale"
 but was: "8. Proprietà intellettuale"
	at it.pagopa.interop.suite.contract.WebTOSContractTest.assertSections(WebTOSContractTest.java:130)

````


---

### 19. WebProducerAgreementContractTest::shouldShowAgreementDetailToProducer::richiesta di fruizione ricevuta visibile all'utente API del Comune di Milano

**RESULT**

Il risultato desunto dai test manuali è che gli utenti API non possono accedere alla pagina di dettaglio di richiesta fruizione con errore 403 Forbidden { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "834b1173-ea4e-45e3-b25d-9a458f0d5e76", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi dei log ed esecuzione in locale del test. Oltre a questi esecuzione manuale del test sia in ambiente DEV, sia QA con ottenimento di mancato caricamento delle pagine richieste inerenti ambito "erogazione/richieste" per gli utenti API.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy262.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementDetailPage.assertLoaded(ProducerAgreementDetailPage.java:31)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy260.assertLoaded(Unknown Source)
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

### 20. WebProducerPurposeContractTest::shouldShowPurposeDetailToProducer::finalità ricevuta visibile all'utente API del Comune di Milano

**RESULT**

Problema di accesso per il ruolo API alla pagina suddetta con errore di 403 forbidden. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "381dac17-a676-47d2-b197-2597a9cb4941", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi dei log e riesecuzione in locale. Il problema viene riscontrato anche con una esecuzione in locale solo per utenti API. Facendo test manuali in DEV e QA si riscontrano problemi di accesso per il ruolo API alla pagina suddetta con errore di 403 forbidden.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy354.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerPurposeContractTest.lambda$shouldShowPurposeDetailToProducer$4(WebProducerPurposeContractTest.java:86)

````


---

### 21. WebProducerAgreementContractTest::shouldShowReceivedAgreementListToProducer::richieste di fruizione ricevute accessibili all'utente API del Comune di Milano

**RESULT**

Problema di accesso per gli operatori API a questa pagina. Di seguito si riporta errore riscontrato da browser. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "0f952e03-84f8-422a-aba1-20ad4c90f54c", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi del log e riesecuzione in locale con fallimento. Eseguendo il test manualmente sia in DEV, sia in QA, si manifesta un problema di accesso (403 Forbidden) per gli operatori API a questa pagina.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy262.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementListPage.assertLoaded(ProducerAgreementListPage.java:20)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy307.assertLoaded(Unknown Source)
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

### 22. WebProducerPurposeContractTest::shouldShowReceivedPurposeListToProducer::finalità ricevute visibili all'utente API del Comune di Milano

**RESULT**

Problema di accesso per gli operatori API a questa pagina. Di seguito si riporta errore riscontrato da browser. { "type": "about:blank", "title": "Unauthorized", "status": 403, "detail": "Invalid roles [\"api\"] for this operation", "correlationId": "ffa80c07-b1f2-4ddd-85cf-b839216dddd6", "errors": [ { "code": "008-9993", "detail": "Invalid roles [\"api\"] for this operation" } ] }

**PROCEDURE**

Analisi del log e riesecuzione in locale con fallimento. Eseguendo il test manualmente sia in DEV, sia in QA, si manifesta un problema di accesso (403 Forbidden) per gli operatori API a questa pagina.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy262.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.purpose.infrastructure.page.ProducerPurposeListPage.assertLoaded(ProducerPurposeListPage.java:29)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy346.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerPurposeContractTest.lambda$shouldShowReceivedPurposeListToProducer$1(WebProducerPurposeContractTest.java:54)

````

</details>

## 5 - KO DOVUTI A BUG DI SUITE NOTI

<details>
<summary>Mostra/nascondi 5 KO</summary>

### 1. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

OpenAPI consente di omettere delegationId. Se presente, il campo deve contenere una stringa in formato UUID. Il BFF riceve un null esplicito e restituisce HTTP 400, come previsto dal contratto. La suite classifica la mutazione come INFERRED_VALID e attende HTTP 200. Il test fallisce perché la suite tratta il null esplicito come valido per un campo opzionale. Per questa mutazione deve attendersi HTTP 400.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di BffAgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"fc341609-9d72-49a7-8b2b-160818e9e33b","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"155bcf77-fd9c-4d7a-b6b6-5bfe50219fc6","descriptorId":"988a86c5-32b0-4891-b2ce-8234294e824a","delegationId":null}
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

### 2. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NULL @ /delegationId

**RESULT**

La suite attende HTTP 201; il BFF restituisce HTTP 400. La risposta rifiuta il null esplicito in /delegationId. La proprietà può essere omessa, ma lo schema non ammette il tipo null. La suite deduce invece INFERRED_VALID dall'opzionalità del modello. Il difetto riguarda l'inferenza dell'aspettativa e richiede di distinguere omissione e null esplicito.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_NULL di M2Mv3AgreementContractTest::createAgreement su POST /agreements. Inviare un null JSON esplicito in /delegationId, mantenendo presenti e validi gli altri campi. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f2bbc337-101a-4198-97d5-343dc2da8f10","errors":[{"code":"017-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"c2ce7447-e046-401b-9369-ca12c04147fa","descriptorId":"14724110-fe4d-4edb-acec-578196ced211","delegationId":null}
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

### 3. WebProducerEServiceTemplateDetailsPageContractTest::producerEServiceTemplateDetailsPageContract::Producer e-service template details page shows expected content in Details tab

**RESULT**

Un elemento Link non viene trovato nella pagina al suo caricamento dal test. XPath correntemente usato risulta corretto facendo il check manuale sul HTML della pagina. Si desume sia un problema di flakiness che non dovrebbe capitare dando più tempo per caricare gli elementi.

**PROCEDURE**

Log analizzato. Test ripetuto con run in locale e il problema si riscontra anche in locale. Effettuato anche check manuale sulla pagina controllando se XPath usato sia corretto e la risorsa URL (tag &lt;a&gt;) viene correttamente trovata sulla pagina in ambiente dev e in ambiente qa. Si desume che possa essere un problema di mancato caricamento entro i 20 secondi di polling.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy317.getHref(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerEServiceTemplateDetailsPageContractTest.lambda$contractScenarios$2(WebProducerEServiceTemplateDetailsPageContractTest.java:216)
	at org.assertj.core.api.SoftAssertionsProvider.assertSoftly(SoftAssertionsProvider.java:122)
	at org.assertj.core.api.SoftAssertions.assertSoftly(SoftAssertions.java:165)
	at it.pagopa.interop.suite.contract.WebProducerEServiceTemplateDetailsPageContractTest.lambda$contractScenarios$3(WebProducerEServiceTemplateDetailsPageContractTest.java:81)

````


---

### 4. WebEServiceContractTest::shouldShowEServiceDetailToProducer::nome dell'e-service creato visibile all'utente API del Comune di Milano

**RESULT**

Dato un esito positivo nella esecuzione in locale il problema viene imputato ad un mancato caricamento tempestivo dei componenti che non sono stati trovati

**PROCEDURE**

Analisi del log e riesecuzione in locale del test. Il problema non viene riscontrato con la esecuzione in locale. Classificato come problema di flakiness

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
	at jdk.proxy2/jdk.proxy2.$Proxy302.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceContractTest.lambda$shouldShowEServiceDetailToProducer$5(WebEServiceContractTest.java:96)

````


---

### 5. WebAgreementRequestPaginationContractTest::shouldShowPaginationNavbarRegardlessOfOffset::offset elevato mantiene la navbar visibile a fine pagina

**RESULT**

La verifica della barra viene eseguita subito dopo la navigazione con offset elevato, mentre il caricamento della lista non è ancora confermato. Il precedente controllo del titolo riguarda la navigazione iniziale. Occorre attendere il caricamento successivo e ripetere la verifica della presenza della paginazione prima di controllarne l'usabilità. Il difetto di suite è in corso di analisi sulla codebase.

**PROCEDURE**

Aprire la lista delle richieste, quindi navigare nuovamente con offset superiore ai risultati disponibili. Attendere il completamento del caricamento della lista e verificare, tramite polling, che la barra di paginazione sia presente. Controllarne quindi l'usabilità e la possibilità di tornare a una pagina valida.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19652

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Pagination navbar is present] 
Expecting value to be true but was false
	at it.pagopa.interop.suite.contract.WebAgreementRequestPaginationContractTest.lambda$highOffsetScenario$3(WebAgreementRequestPaginationContractTest.java:105)

````

</details>

## 93 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 93 KO</summary>

### 1. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #1 | line: 51]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale su QA PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [1b139e9f-3ca0-4653-9cf9-9dc28faae3f2,
    4b1eb67f-b428-4461-a416-af8934da7e70,
    19f6b380-a58e-46c3-b67b-53a9973b4946]
to contain exactly (and in same order):
  [4b1eb67f-b428-4461-a416-af8934da7e70,
    1b139e9f-3ca0-4653-9cf9-9dc28faae3f2,
    19f6b380-a58e-46c3-b67b-53a9973b4946]
but there were differences at these indexes:
  - element at index 0: expected "4b1eb67f-b428-4461-a416-af8934da7e70" but was "1b139e9f-3ca0-4653-9cf9-9dc28faae3f2"
  - element at index 1: expected "1b139e9f-3ca0-4653-9cf9-9dc28faae3f2" but was "4b1eb67f-b428-4461-a416-af8934da7e70"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 2. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #2 | line: 52]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [f0c231d6-b793-4b62-9a2e-4a715b8cf9b8,
    08054162-9252-4a7b-81dd-8c8e2507a580,
    4fd0ecd7-32b8-4a77-831e-fb3afdf8abf9]
to contain exactly (and in same order):
  [4fd0ecd7-32b8-4a77-831e-fb3afdf8abf9,
    f0c231d6-b793-4b62-9a2e-4a715b8cf9b8,
    08054162-9252-4a7b-81dd-8c8e2507a580]
but there were differences at these indexes:
  - element at index 0: expected "4fd0ecd7-32b8-4a77-831e-fb3afdf8abf9" but was "f0c231d6-b793-4b62-9a2e-4a715b8cf9b8"
  - element at index 1: expected "f0c231d6-b793-4b62-9a2e-4a715b8cf9b8" but was "08054162-9252-4a7b-81dd-8c8e2507a580"
  - element at index 2: expected "08054162-9252-4a7b-81dd-8c8e2507a580" but was "4fd0ecd7-32b8-4a77-831e-fb3afdf8abf9"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 3. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #3 | line: 53]

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [c6361ffa-ad13-485e-8264-32964cd5e8a7,
    c6a8ebbd-3288-44ed-bf3b-7d61cb8d5e56,
    bb7cb1c6-6109-4b8b-93f3-fac93f8f6dfa]
to contain exactly (and in same order):
  [c6361ffa-ad13-485e-8264-32964cd5e8a7,
    bb7cb1c6-6109-4b8b-93f3-fac93f8f6dfa,
    c6a8ebbd-3288-44ed-bf3b-7d61cb8d5e56]
but there were differences at these indexes:
  - element at index 1: expected "bb7cb1c6-6109-4b8b-93f3-fac93f8f6dfa" but was "c6a8ebbd-3288-44ed-bf3b-7d61cb8d5e56"
  - element at index 2: expected "c6a8ebbd-3288-44ed-bf3b-7d61cb8d5e56" but was "bb7cb1c6-6109-4b8b-93f3-fac93f8f6dfa"

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

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

> **ℹ️ Validazione finale prima dell’automazione**
>
> L’analisi di questo KO è stata precompilata automaticamente sulla base di un fallimento precedentemente analizzato e confermata dall’analista QA nell’ambito della presente RAE.
>
> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: **nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato automaticamente**, qualora si ripresenti con le medesime caratteristiche.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [4fd0bb53-9807-445f-940b-98b2cab14858,
    8d4e1f0f-7c20-4117-9112-4713281beb3c,
    23133eba-87d8-400c-8ee4-ddd9dd213778]
to contain exactly (and in same order):
  [4fd0bb53-9807-445f-940b-98b2cab14858,
    23133eba-87d8-400c-8ee4-ddd9dd213778,
    8d4e1f0f-7c20-4117-9112-4713281beb3c]
but there were differences at these indexes:
  - element at index 1: expected "23133eba-87d8-400c-8ee4-ddd9dd213778" but was "8d4e1f0f-7c20-4117-9112-4713281beb3c"
  - element at index 2: expected "8d4e1f0f-7c20-4117-9112-4713281beb3c" but was "23133eba-87d8-400c-8ee4-ddd9dd213778"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 5. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #1]

**RESULT**

Dopo la creazione di un e-service da template SOAP pubblicato, il BFF ha ricevuto indirizzi contenenti markup o virgolette.

Per tutti e quattro gli esempi la richiesta di configurazione ha ricevuto HTTP 200 con l'ID del descrittore, anziché il rifiuto atteso. La run riportava un'attesa di HTTP 409.

**PROCEDURE**

Confrontare i valori inviati nel campo serverUrls e le risposte del BFF con il rifiuto atteso dalla feature.

Verificare dallo stack trace se il test raggiunge anche i controlli sull'interfaccia del descrittore e su quella del template.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11710

**ERROR LOG**

````text
java.lang.AssertionError: Expected status code 409 but was 200
	at it.pagopa.infrastructure.template.action.ResponseFinalizer.assertStatusCode(ResponseFinalizer.java:46)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorInterfaceGateway.shouldFailToConfigureSOAP(BffEServiceDescriptorInterfaceGateway.java:70)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway.shouldFailToConfigureSOAPInterface(BffEServiceDescriptorGateway.java:168)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy163.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 6. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #2]

**RESULT**

Dopo la creazione di un e-service da template SOAP pubblicato, il BFF ha ricevuto indirizzi contenenti markup o virgolette.

Per tutti e quattro gli esempi la richiesta di configurazione ha ricevuto HTTP 200 con l'ID del descrittore, anziché il rifiuto atteso. La run riportava un'attesa di HTTP 409.

**PROCEDURE**

Confrontare i valori inviati nel campo serverUrls e le risposte del BFF con il rifiuto atteso dalla feature.

Verificare dallo stack trace se il test raggiunge anche i controlli sull'interfaccia del descrittore e su quella del template.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11710

**ERROR LOG**

````text
java.lang.AssertionError: Expected status code 409 but was 200
	at it.pagopa.infrastructure.template.action.ResponseFinalizer.assertStatusCode(ResponseFinalizer.java:46)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorInterfaceGateway.shouldFailToConfigureSOAP(BffEServiceDescriptorInterfaceGateway.java:70)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway.shouldFailToConfigureSOAPInterface(BffEServiceDescriptorGateway.java:168)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy163.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 7. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #3]

**RESULT**

Dopo la creazione di un e-service da template SOAP pubblicato, il BFF ha ricevuto indirizzi contenenti markup o virgolette.

Per tutti e quattro gli esempi la richiesta di configurazione ha ricevuto HTTP 200 con l'ID del descrittore, anziché il rifiuto atteso. La run riportava un'attesa di HTTP 409.

**PROCEDURE**

Confrontare i valori inviati nel campo serverUrls e le risposte del BFF con il rifiuto atteso dalla feature.

Verificare dallo stack trace se il test raggiunge anche i controlli sull'interfaccia del descrittore e su quella del template.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11710

**ERROR LOG**

````text
java.lang.AssertionError: Expected status code 409 but was 200
	at it.pagopa.infrastructure.template.action.ResponseFinalizer.assertStatusCode(ResponseFinalizer.java:46)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorInterfaceGateway.shouldFailToConfigureSOAP(BffEServiceDescriptorInterfaceGateway.java:70)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway.shouldFailToConfigureSOAPInterface(BffEServiceDescriptorGateway.java:168)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy163.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 8. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #4]

**RESULT**

Dopo la creazione di un e-service da template SOAP pubblicato, il BFF ha ricevuto indirizzi contenenti markup o virgolette.

Per tutti e quattro gli esempi la richiesta di configurazione ha ricevuto HTTP 200 con l'ID del descrittore, anziché il rifiuto atteso. La run riportava un'attesa di HTTP 409.

**PROCEDURE**

Confrontare i valori inviati nel campo serverUrls e le risposte del BFF con il rifiuto atteso dalla feature.

Verificare dallo stack trace se il test raggiunge anche i controlli sull'interfaccia del descrittore e su quella del template.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11710

**ERROR LOG**

````text
java.lang.AssertionError: Expected status code 409 but was 200
	at it.pagopa.infrastructure.template.action.ResponseFinalizer.assertStatusCode(ResponseFinalizer.java:46)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorInterfaceGateway.shouldFailToConfigureSOAP(BffEServiceDescriptorInterfaceGateway.java:70)
	at it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway.shouldFailToConfigureSOAPInterface(BffEServiceDescriptorGateway.java:168)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy163.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 9. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_NULL @ /answerData/annotation

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"23de4434-6af7-4962-b03f-7195053c9ee6","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"answerData.annotation\""}]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":null,"suggestedValues":["answer-value-WVAYUuOlLoW4upB"]}}
- pathParams: {"purposeTemplateId":"13b69d89-894c-4bce-b1fb-f052497beb9d"}
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

### 10. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_BLANK_STRING @ /answerData/suggestedValues/0

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
- responseBody: {"id":"75bf9e5a-30f4-40d9-bd01-1a0384096e46","values":[],"editable":false,"annotation":{"id":"54adb7b8-0192-4d7d-b2bf-197ea696d2a5","text":"annotation-NhprE4XhFGpg9jLwe768","docs":[]},"suggestedValues":["   "]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"annotation-NhprE4XhFGpg9jLwe768"},"suggestedValues":["   "]}}
- pathParams: {"purposeTemplateId":"c4416933-7359-41ce-9c62-4d4fd394994e"}
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

### 11. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_NULL @ /description

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c923f7a7-2114-47f1-b7b6-dbdd31927178","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-5E4cCI5ldZYzt1NPQDP7KRUi","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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

### 12. BffClientContractTest::createApiClient::[payload] REMOVED @ /members/0

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
- responseBody: {"id":"9c5624f9-8022-4244-be76-18863398201d"}
- payload: {"name":"client-I5d2DCv8eCtqevrH3ECPFy8l","description":"description-oRH2QwCRsYmJlqP3tJb440TEfOnxSL5x","members":[]}
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

### 13. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"364ce0e8-0f5c-4860-9797-45d2a64fce39","code":"4ae4cf73db7120c33e893702483aa9fcbc369981e5ff693b9924491ed0fa5859","kind":"CERTIFIED","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-7ox6jGmK7kd7k7rbeviXz3z0","creationTime":"2026-11-26T21:24:06.161Z"}
- payload: {"description":"   ","name":"name-7ox6jGmK7kd7k7rbeviXz3z0"}
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

### 14. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"877d2be9-6799-4480-88d0-b4d517e14ca5","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-2xDDQc1I51uAIv3kqKv8QOQOXhYU7Tgx","name":"   "}
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

### 15. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"892661c0-f923-43b2-b2e8-0143d4b03357","code":"637a21875c00efd54e69b6f7545a4a10f46f4caf9dde60cd030ecbdf18dd4813","kind":"CERTIFIED_DISCRETE","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-ek3snv3SVD0ftdELzUv7P5fI","creationTime":"2026-11-26T21:29:29.617Z"}
- payload: {"description":"   ","name":"name-ek3snv3SVD0ftdELzUv7P5fI"}
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

### 16. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"7493b378-9dd6-41ee-aca9-d81641c65851","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-JGpZ8o1sTjGZejaFi5A5pD2Y9PpTjTjg","name":"   "}
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

### 17. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_NULL @ /description

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a768577c-b213-4623-a0be-f13da76d5b5f","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-92upyv0N17L8SHYyq0nkYni4","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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

### 18. BffClientContractTest::createConsumerClient::[payload] REMOVED @ /members/0

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
- responseBody: {"id":"3e187a85-40e2-4062-82dc-a16dd98c43fc"}
- payload: {"name":"client-92upyv0N17L8SHYyq0nkYni4","description":"description-ZqdK3aV2hDEHHulQ2UvHoHkPKg2aPoBb","members":[]}
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

### 19. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"53d90cd8-1bed-47d0-b12a-f5581a214754","kind":"DECLARED","description":"   ","name":"name-M8RcYVZ16TgpmG0HagDsmYKd","creationTime":"2026-11-26T21:22:06.457Z"}
- payload: {"description":"   ","name":"name-M8RcYVZ16TgpmG0HagDsmYKd"}
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

### 20. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"id":"1791d5e6-46d3-452b-a621-86c0b110c30c","kind":"DECLARED","description":"description-zEvRb929kf5WW1h3wIMlV8lUwoDV44Hi","name":"   ","creationTime":"2026-11-26T21:22:09.166Z"}
- payload: {"description":"description-zEvRb929kf5WW1h3wIMlV8lUwoDV44Hi","name":"   "}
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

### 21. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a0f1f81a-bad7-4eb9-832b-973bc37559ce","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-p50WTypVRDcPdzh","description":"description-jGC3QLL5scU4gXVOL5uX","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 22. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4b92a535-e342-4145-9ddc-7bf6511a2851","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-TgRY26JZQF7E37p","description":"description-vfOmTVd6NTyhrscLb7A4","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 23. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"913b03f4-7b68-4e6e-ae7f-baaff8c7cfe6","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-cvQrFs92nqsbsqc","description":"description-kO0v7eACNOPb9zuqDHzD","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 24. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"2f105612-2bb8-41f3-8e20-6871dc190e51","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-16xQkZ03J3302Qz","description":"description-EcPTJZc2KgKBCwLSDRuT","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 25. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"72cf82ba-2dd3-4be9-8e25-a087ec9267ed","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-mbdxsAfiquRCURn","description":"description-Jt5fesnbU5cfMYA3IoZp","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 26. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"376b1fac-91b2-421f-8bec-e9d3ef7001cf","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"version\""}]}
- payload: {"name":"eservice-template-llYLk8eNPnGIOSl","intendedTarget":"target-HvMzbdGTzEcIIerUM61enoRRiSWxSN","description":"description-Mrb4ccrCQ9FFjBYVrY4wpjUdxxfIae8JTMVyRO92ztdLAoYtfZ","technology":"REST","mode":"DELIVER","version":null,"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 27. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsPerConsumer

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4bcd059e-314e-4d49-a617-372a0fe90a43","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsPerConsumer\""}]}
- payload: {"name":"eservice-template-stpXPQ5kji3Bngl","intendedTarget":"target-3rbEZNhhHm2eDcmU8wkYL2fs0zwrgV","description":"description-kUkWM8NUDJdU8QdN8qD9DD3AivfaMABQHNnRjw3jCblxSOW5ZM","technology":"REST","mode":"DELIVER","version":{"description":"version-description-CkVvOhIqWDh8TRmEuLYh2IVbQIH1YS61OuH","voucherLifespan":60,"dailyCallsPerConsumer":null,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 28. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsTotal

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"1f651084-e78b-44d4-8559-e8a8071a7972","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsTotal\""}]}
- payload: {"name":"eservice-template-IWl6EGbIWpCs9Z8","intendedTarget":"target-A0WtPgxPxnhdrneWkjyztt9zEOZaAd","description":"description-ocu01QsSmsJBtolZ0XWhJlDfxF3xI9PtnPcIDSNUt2cC5RxjjS","technology":"REST","mode":"DELIVER","version":{"description":"version-description-FZtrZg9PLqEnq9qd13DBXokTeRerfXg8cky","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":null,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 29. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/agreementApprovalPolicy

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f2692ec4-e262-411e-947b-4afde40dc07e","errors":[{"code":"008-9999","detail":"Validation error: Expected 'AUTOMATIC' | 'MANUAL', received null at \"version.agreementApprovalPolicy\""}]}
- payload: {"name":"eservice-template-TQrN2C8uExN0Uyt","intendedTarget":"target-cXfUdETal9iHFzVokqhOiNkAYIvIHe","description":"description-fEAQ008OzwrWCSjj7soSyAfRKZxjsBJomaUm1nGombhpO8CnS7","technology":"REST","mode":"DELIVER","version":{"description":"version-description-twXbZZ2YpN8rdYINaYgQ59hgRvq7iHyMJej","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":null},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 30. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"13853896-8451-4470-b9c5-52f54b7b6481","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-template-wF2HZKchwDjHI62","intendedTarget":"target-48AeZv0HAhpVcqtb0DuiGfM1a4gOVi","description":"description-G226fwAKLT62ElBQ6SewHfIoWcc7R2YKPXeQphtOOT7W4F3KFb","technology":"REST","mode":"DELIVER","version":{"description":"version-description-3PbB9BKXgAYBrkU3ifITcJW6HDfTzqsUMaV","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":null,"personalData":false,"asyncExchange":false}
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

### 31. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"968780d3-0f2e-44c6-8802-e4e21149fca4","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-template-DrbDh9x95LOv6sT","intendedTarget":"target-cYXANePGgJRQDONCPwUBnKEzjRKXNy","description":"description-yncnCI7l9J48CYNs2DL1GyXOdEVTuW4hL4WbBbdi1QTKpIjoZS","technology":"REST","mode":"DELIVER","version":{"description":"version-description-RfiYFZAQNp7XyI5wi53ja3BmdV1Prl2QRfD","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":null,"asyncExchange":false}
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

### 32. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"26252274-2c14-42cb-95e7-0f9786e5d302","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-template-1i0AzQfCzT9rVER","intendedTarget":"target-eALfqOXLvKl6nMQfAttW5jqoeB2W5Z","description":"description-MtiAWjBFQh1Ke82OnowIyo9Oki7ywMyb8yAZevWoFWS2f7vBru","technology":"REST","mode":"DELIVER","version":{"description":"version-description-ITakiidMgCFTc9ehVBE1jxBGbEByZgOCoJi","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":null}
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

### 33. BffClientContractTest::createKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1U2p3aVYyQnpCWFJOcG9uZU5CZAoveHRCVEVrUUYrOSs3eTFOM3RLR0pRZXFGajJNdW1HK1UvTVBIUXhnbXpLb3pQM29zKzFqa0lEd0ZrdzVMaFFUCmYvNlpaRjB1c2hZSzNOeUFac0NDampldWVnd1c1bktRVktHZnNqK29HL2c0R3RiQnhYRlJwUGFrYnRJMW0zL1MKb1VFeWdPUjlVN001L0ZWT1lpUzRVNUhJYUFiMmdKWHpmYkNlam00dmltSFRLUFRwSUFXVUJyUTdLUHVCenp3dQpkNFhDSGpHdnVPVUdTS2dFS3BRZllpQ0lyeGtsZ0VlWlpxa2MyNTFWUE5uUzZSLzFrT3B1VmliSTVXVGJZZXVYClFwaEx4TzlLQlg5QjN4cnVBWDN3V2pwb3hMV3ZiY1Y0Tks5RmNGbksza3RYb3NVRmxzQi9mOG1KV2pRREhraHkKS3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"key-4FWuWZiL0bc7SCx"}
- pathParams: {"clientId":"6d5247e5-ae0e-4286-ba2c-b1f055598b58"}
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

### 34. BffClientContractTest::createKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid pNEWqTuxGwWPOaF8t6puj3LdlswcZKvUM89x_klKBqU already exists","correlationId":"161af7d5-f0d1-4fd6-8ca2-a495247d01a2","errors":[{"code":"006-0017","detail":"Key with kid pNEWqTuxGwWPOaF8t6puj3LdlswcZKvUM89x_klKBqU already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1U2p3aVYyQnpCWFJOcG9uZU5CZAoveHRCVEVrUUYrOSs3eTFOM3RLR0pRZXFGajJNdW1HK1UvTVBIUXhnbXpLb3pQM29zKzFqa0lEd0ZrdzVMaFFUCmYvNlpaRjB1c2hZSzNOeUFac0NDampldWVnd1c1bktRVktHZnNqK29HL2c0R3RiQnhYRlJwUGFrYnRJMW0zL1MKb1VFeWdPUjlVN001L0ZWT1lpUzRVNUhJYUFiMmdKWHpmYkNlam00dmltSFRLUFRwSUFXVUJyUTdLUHVCenp3dQpkNFhDSGpHdnVPVUdTS2dFS3BRZllpQ0lyeGtsZ0VlWlpxa2MyNTFWUE5uUzZSLzFrT3B1VmliSTVXVGJZZXVYClFwaEx4TzlLQlg5QjN4cnVBWDN3V2pwb3hMV3ZiY1Y0Tks5RmNGbksza3RYb3NVRmxzQi9mOG1KV2pRREhraHkKS3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"key-4FWuWZiL0bc7SCx"}
- pathParams: {"clientId":"929f0d0a-243e-4c1b-bc83-0943124eed16"}
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

### 35. BffClientContractTest::createKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid pNEWqTuxGwWPOaF8t6puj3LdlswcZKvUM89x_klKBqU already exists","correlationId":"17abfe84-a7b1-4ae8-8a0b-fb2f55089ad4","errors":[{"code":"006-0017","detail":"Key with kid pNEWqTuxGwWPOaF8t6puj3LdlswcZKvUM89x_klKBqU already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF1U2p3aVYyQnpCWFJOcG9uZU5CZAoveHRCVEVrUUYrOSs3eTFOM3RLR0pRZXFGajJNdW1HK1UvTVBIUXhnbXpLb3pQM29zKzFqa0lEd0ZrdzVMaFFUCmYvNlpaRjB1c2hZSzNOeUFac0NDampldWVnd1c1bktRVktHZnNqK29HL2c0R3RiQnhYRlJwUGFrYnRJMW0zL1MKb1VFeWdPUjlVN001L0ZWT1lpUzRVNUhJYUFiMmdKWHpmYkNlam00dmltSFRLUFRwSUFXVUJyUTdLUHVCenp3dQpkNFhDSGpHdnVPVUdTS2dFS3BRZllpQ0lyeGtsZ0VlWlpxa2MyNTFWUE5uUzZSLzFrT3B1VmliSTVXVGJZZXVYClFwaEx4TzlLQlg5QjN4cnVBWDN3V2pwb3hMV3ZiY1Y0Tks5RmNGbksza3RYb3NVRmxzQi9mOG1KV2pRREhraHkKS3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"key-4FWuWZiL0bc7SCx"}
- pathParams: {"clientId":"152f719e-50f1-4dd2-8ec0-b82970680bb0"}
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

### 36. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFyc2VYTDZ1VHgvSTQzRDMvNkxnMgppTEhpVllWOVJpNEdZWWtwNjRjTGNZNzZQL2JWWnBtTmh0alhoYXIyUVJ2TnA3VElnQXBzRENqSzZYbnBZMCtlCkRXdjhlZHBjUVg2WGxGNERCUGtWRjRMVmZzYk1sdndoNXlvNzZQM3dnU0VOV3h2RVM1SGlGWENWQlZMaERSNXUKM0FtdmFrcmJseUtwbEJwYy8xb0FvaWNrT1MzTU5PZ0t1NnVxWityMk14amM5RFdKbmxJSzVVaHRaYW9KbzU3UQpwMlZuTDJ5OHFBT0xqSGhibUlwQm5uZmhXbXRzREs4V01jR0JEd0tHMVRkWDhGRjFzV2FrQWdTODllK1ZhejZvCm1WVEtFSDErOGQ1YlhtektPQTJJM3I0TWEwcXd3bTZxSDF1ckRsYXZFSmY1N1JVODJzZXhjRjg1VFVTS2NxSFgKd3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"producer-key-QplWqZpuZscYQ3N"}
- pathParams: {"producerKeychainId":"70eee8d9-5f8d-4079-8737-f6666f9c01bd"}
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

### 37. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFxaFNzaHpzZWJkQzM1U2ZyY1UvOAo3ZmJuVnVQcmZRUnRBYlJBNzJaQTdjSEl2SER3SDc3a3hIY21sSk1QZW43V0IrZnNsRmgwbjBaZ3kwNDVEYVkyCkIxakVZSWpFZ1pTcHJvY2d0UVZwUXBBV0Y1aWRFa3EzQ0dkUU04dHdBQ0tYSGRBcnNxTmhDSkgydEE5U2tRQlUKRXZkVFFuTkJ6N0oxYnJncEtpZU0vbFVmRkRtaEJEempQcEJVN0RnWEl2dEE3UjFBeEMyU3kxVXZlQnE1clI1UwpxTVErNlNHR0xXeTZYWFhCdW9kSThsWjYxWFF3S1JOR0ZTQ29YWmhVVTF2V0EwV1F6WGFtQ243Vld5anFiTUZZCmltTTRGcDUrTUhlSXoxNDJHaTJsRlhJRUViOG1mQ3REMlZXU3hiaitMd1NuNWVmbEQzTnpsT1JjVHZ2ZGlTa2EKVVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"producer-key-WXVJ2729Qe54dwB"}
- pathParams: {"producerKeychainId":"3b018f81-240e-49dd-8710-d8d441b45f6e"}
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

### 38. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFrV1JXSlpFQmtYYnA5UStGVGl5MgpqSU0rTG1rN1g2L1VuZ05vNWUvUmE4Vnhudk1hZ0hteFVsT04ybEpiS1BXYm5Ic3RPSTcvOEFVUHBKSW1VVXlKCkZIMko0SjJhcDdJVXdZMDR4UTNMYjNxcHhsUVpnTUltdXhqaDNCNlpZaVUwLzYrMlo2TjVQdVRiWG8vbHl6OEIKRVBxVDZmMmEyTmJjMzM1T3hieFQ5Q1dFbUFOalBpVHo2Sk1haWorbW1mTTN6bHFpZHNJbE5Fc0JSckVxM2dyegpuOHUzcTlSY1ltaG11TU5McDAyVUk4WTdsN09vUVFCZnhSaUFURFV1UVYwcTZJaWhydk12WVM2Zm9VMmpldmt6CkpvSzZiMzdxenBaNDVpRFg0VTRsZ25CSUNtQytsRjlWT01xUjdmWDdSYWxLaFVybVJRdFlicXZPYUZ2bmg0ckMKVXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"producer-key-mmQSoP2hJcC9DmK"}
- pathParams: {"producerKeychainId":"89f867a1-e358-40a3-93b1-1d112d8cd40b"}
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

### 39. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"12936f95-dfd9-4ff5-a123-ff07482e44f4","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"eserviceId":"c0d6203a-b04e-42d9-a4c3-be68303fc900","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":null,"title":"purpose-4GT117q02BWhoss","description":"description-iagFebeTmCWIrlL","isFreeOfCharge":true,"freeOfChargeReason":"reason-kzYvEkL9gyL3C2Q","dailyCalls":1}
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

### 40. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"75b1fe4b-4e80-44bb-ac65-85ad7f4e7e1e","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"d77710b7-b828-4f5b-9439-cb0174b524e0","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-1BLeUchzOnKzy3E","description":"description-u6hTmzbmuhVQvxS","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 41. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

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
- responseBody: {"id":"1d3b4d6b-c4c3-4fb8-89f5-c09247dea491"}
- payload: {"eserviceId":"693047d5-98d2-4f47-bef4-f2ef0777157d","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-lbWs581sj5vYtQm","description":"description-b6vm0kzTiHaDrTJ","isFreeOfCharge":true,"freeOfChargeReason":"reason-XQyA6aTjQlwgVf0","dailyCalls":1}
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

### 42. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"b06c487c-4370-457c-bbd0-4dc2418e3f01"}
- payload: {"eserviceId":"59a03c65-dab0-4062-8895-ecf5b8a970e4","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-EKBrGwcHu6ntXpu","description":"description-XQLNADy7xDWsClb","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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

### 43. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"9b66a6be-7dfd-4882-bb5c-638c86da16b6"}
- payload: {"eserviceId":"e637e9f3-220a-458b-92b2-3328833000d4","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-0DcXB1jxMigDxTD","description":"description-iiGI0UFm8vw5RPj","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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

### 44. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"bb9e0cf3-e31d-4bb5-b3f2-9d7023c3ed98","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"6a77e866-63f4-48e2-bb49-bca7346c12ff","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"bfe7b90c-54af-46a7-83b6-42fd61093c52","title":"purpose-jAaZ3o9nzBlNKbB","description":"description-j0DGkkIXZiVyPZQ","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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

### 45. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /riskAnalysisId

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
- responseBody: {"type":"about:blank","title":"Risk analysis not found","status":400,"detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService 318882b7-88af-4398-8ab6-d976b628126c","correlationId":"14061268-ac3d-49bc-b4b8-ecd6890dfd74","errors":[{"code":"004-0019","detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService 318882b7-88af-4398-8ab6-d976b628126c"}]}
- payload: {"eserviceId":"318882b7-88af-4398-8ab6-d976b628126c","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"00000000-0000-0000-0000-000000000000","title":"purpose-ogXOj1cbRtLeltQ","description":"description-quvWp7DmxaWahnC","isFreeOfCharge":true,"freeOfChargeReason":"reason-TpQg8CfGpJpgdRK","dailyCalls":1}
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

### 46. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"2fc1e006-a908-4ec4-b39c-696d9e668d03","errors":[{"code":"004-0002","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"5894a0d1-85de-4aea-86a1-b32e0f961e37","title":"purpose-tOTrMrq1ckkUSL4","description":"description-zGwzePNlPvAfdTl","isFreeOfCharge":true,"freeOfChargeReason":"reason-3DC1kEh4C9F2pp9","dailyCalls":1}
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
Expected status code <404> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 47. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"ea7e483b-2884-4cfc-a0e7-e96e675aea2e"}
- payload: {"eserviceId":"bd876024-f800-40b2-b442-c47ba39d87bf","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"e49a4afc-25af-4f31-b314-176288975b74","title":"purpose-FGVWT89l93XXpwL","description":"description-K5f6vzS4ls5ccUU","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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

### 48. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"898e0163-5e58-4002-b9c3-e7be338d2f54"}
- payload: {"eserviceId":"1bb3ada3-2032-4e8c-8796-2d21d680a31d","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"0c573030-1e1a-46aa-bd45-c6b673931e62","title":"purpose-v86nZgDk8TC5baO","description":"description-QiaH2VSURp6ewRG","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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

### 49. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"734dcfb2-64f0-427d-b2df-18af5e63dec8","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-GYXNfF7htKH9XmJTaPUE","targetTenantKind":"PA","purposeTitle":"purpose-template-eoLe3AuePZULiuG","purposeDescription":"purpose-description-aJas7Ii3rq6xe31r1ZxD","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-hUftaEWzQKSyTZvOPcwm","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 50. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm

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
- responseBody: {"id":"ab7a1405-3741-4377-b70d-66207afd2752"}
- payload: {"targetDescription":"target-description-8cTLmV9uzCPnu09gXw6f","targetTenantKind":"PA","purposeTitle":"purpose-template-vjKeYmwDzHkXFRX","purposeDescription":"purpose-description-WihkTa9oZMIz5KLLKU2p","purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-4ETB9BBTMuhKdUqMNMqH","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 51. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

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
- responseBody: {"id":"f9939411-7a95-4554-a2cd-f4337fe2463e"}
- payload: {"targetDescription":"target-description-7qjvTAXhPtUSy4YBsCgD","targetTenantKind":"PA","purposeTitle":"purpose-template-SQn0pKGjGJ1ZQG9","purposeDescription":"purpose-description-Yu8jrDOOS3pf6HZun2g3","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-us4H2hc3o3Rm0TXDzOUX","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 52. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

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
- responseBody: {"id":"dbf18bfd-742f-4048-9306-70974066e46c"}
- payload: {"targetDescription":"target-description-Vp5MBeww0c4Xw94rJ96H","targetTenantKind":"PA","purposeTitle":"purpose-template-h9HOBMwmFmfAyzY","purposeDescription":"purpose-description-GjCd7SmxtahrR8IxIAoj","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-lr5K3WkMlwEGa6zFyeQA","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 53. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

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
- responseBody: {"id":"670c0977-9ed3-4c63-8c78-b2018e4a42fa"}
- payload: {"targetDescription":"target-description-p6wPv65HN72rORxTFJgj","targetTenantKind":"PA","purposeTitle":"purpose-template-GiHE2DF8oQzhoTx","purposeDescription":"purpose-description-hpTkvDUq81uvbSp9lLLq","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-Lm1WAASblgqJ2q7cQYDP","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 54. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

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
- responseBody: {"id":"1e0f0b1f-12af-4f72-a25c-27e1d93968c1"}
- payload: {"targetDescription":"target-description-DrzWVv83Iv8HeUxwmf3m","targetTenantKind":"PA","purposeTitle":"purpose-template-XL6q0GDtu0ftHkH","purposeDescription":"purpose-description-JXxu1kKVNkblx3FTJjnS","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-Bj6j5gDXFm4ZWJMSdetk","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 55. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

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
- responseBody: {"id":"b3626f34-2d0d-40ac-b851-2208c61c63e4"}
- payload: {"targetDescription":"target-description-ORFCH4uJiDBE7YJ2vFoi","targetTenantKind":"PA","purposeTitle":"purpose-template-Uh3fpNHRjxWoMZg","purposeDescription":"purpose-description-JZMJgYb9BFXlOJiMghuv","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-sdnvsK0S4Vzv8U1cMExd","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 56. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d87aa930-5a3a-4118-881e-aaee41fd0086","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-AoF5FyP9PVSHJXxygJIo","targetTenantKind":"PA","purposeTitle":"purpose-template-XYJ34UlrNcAI8Mj","purposeDescription":"purpose-description-NUALEIOyr4EdypgZWqFc","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-ncrkj1iMefWXA5HlyVmT","purposeDailyCalls":null,"handlesPersonalData":false}
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

### 57. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeDailyCalls

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
- responseBody: {"id":"fc41002d-4870-4c2c-b9c3-96325c88aa85"}
- payload: {"targetDescription":"target-description-aEdXMwM0vZGwuyUKgKv2","targetTenantKind":"PA","purposeTitle":"purpose-template-8bI5oNXGyMpTbos","purposeDescription":"purpose-description-lptCNbix8CCS6JCd8YvL","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-sfpbPVt7QbWK5hhR3RCf","handlesPersonalData":false}
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

### 58. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

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
- responseBody: {"id":"da151d75-be4f-46b3-97d9-c9db8700c944"}
- payload: {"targetDescription":"target-description-7dsJCvFNPJ1GO9C5mpTS","targetTenantKind":"PA","purposeTitle":"purpose-template-DARC2MFyBTodOkk","purposeDescription":"purpose-description-N8SB18jIMeI3hsxMxXMt","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-EL1g674qZ0WRGuZ9tmm7","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 59. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"2b57643e-c0e6-4083-a89f-bf0aca5f5940"}
- payload: {"targetDescription":"target-description-k8VeeHOgVk5gxtAV3ihg","targetTenantKind":"PA","purposeTitle":"purpose-template-AaWhR4YjfJhWOrd","purposeDescription":"purpose-description-bGw8g0Urf1EDf9Qhu9MF","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"   ","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 60. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"91b568a5-25f9-46c1-ba6b-bb5128394164"}
- payload: {"targetDescription":"target-description-cl5oPla9PQg9YprF7C6F","targetTenantKind":"PA","purposeTitle":"purpose-template-DY8772DlFeDa5jB","purposeDescription":"purpose-description-ny39hzLpWKdPEkLbRtS8","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 61. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"8b5dbbae-d5c9-4ac0-9f8a-1add53d8b8d6","kind":"VERIFIED","description":"   ","name":"name-VtcnpQifO9C3yta9KWvowag9","creationTime":"2026-11-26T21:22:14.563Z"}
- payload: {"description":"   ","name":"name-VtcnpQifO9C3yta9KWvowag9"}
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

### 62. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name","status":409,"detail":"An attribute with name     already exists","correlationId":"b330b715-6407-4370-8354-27a7d2dae8a5","errors":[{"code":"003-0002","detail":"An attribute with name     already exists"}],"userMessage":"Esiste già un attributo con questo nome. Scegli un altro nome."}
- payload: {"description":"description-K4a9fFUjSAKdh4j8WKaTRB2tN9kP1QN8","name":"   "}
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

### 63. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

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

### 64. BffDelegationContractTest::rejectConsumerDelegation::Precondition failed

**RESULT**

KO dovuto alla precondizione del test, non alla chiamata di reject. La creazione della delega consumer riceve sempre 403 (codice 010-0007): il tenant delegato 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 non è abilitato a ricevere deleghe di tipo DelegatedConsumer. Il polling ritenta il 403 per 20 secondi e poi va in timeout, nascondendo l'errore reale.

Una possibile causa del KO è il parallelismo tra scenari che modificano la disponibilità a ricevere deleghe di uno stesso ente condiviso. Una modifica concorrente può alterare la precondizione del test e produrre un falso KO. I log disponibili non identificano la modifica responsabile di questa specifica esecuzione.

**PROCEDURE**

Eseguire in locale lo scenario e se funziona: 1. Aprire il log del test e filtrare per "rejectConsumerDelegation". L'errore "pathParams supplier failed during discovery" indica che è fallita la precondizione, non la chiamata sotto test. 2. Seguire la causa annidata: createPendingConsumerDelegationId -&gt; createConsumerDelegation -&gt; polling UNTIL_SUCCESS scaduto dopo 20 secondi. Il timeout è solo l'effetto finale. 3. Nel log cercare la richiesta POST /1.0/backend-for-frontend/consumers/delegations e leggere lo status delle risposte. Risultano 14 tentativi, tutti 403. 4. Leggere il body della risposta: codice 010-0007, "Tenant 80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20 not allowed to receive delegations of kind: DelegatedConsumer". Annotare il correlationId. 5. Controllare che l'e-service fosse pubblicato e delegabile (state PUBLISHED, isConsumerDelegable true). 6. Verificare la configurazione del tenant DELEGATE_CONSUMER (80bc8c2a-...) rispetto alla possibilità di ricevere deleghe consumer.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19759

**ERROR LOG**

````text
it.pagopa.infrastructure.contract.http.ContractHttpException: pathParams supplier failed during discovery
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.materializeSource(HttpContractRuntimeCaseExecutor.java:92)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.planScope(HttpContractInvocationBuilder.java:109)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.tests(HttpContractInvocationBuilder.java:80)
	at it.pagopa.infrastructure.contract.http.HttpContractPayloadStage.tests(HttpContractPayloadStage.java:55)
	at it.pagopa.interop.suite.contract.BffDelegationContractTest.rejectConsumerDelegation(BffDelegationContractTest.java:86)
Caused by: java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.delegation.infrastructure.BffDelegationGateway.createConsumerDelegation(BffDelegationGateway.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy159.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.common.delegation.application.DelegationUseCase.createConsumerDelegation(DelegationUseCase.java:33)
	at it.pagopa.interop.common.journey.infrastructure.DelegationJourneyImpl.createConsumerDelegation(DelegationJourneyImpl.java:31)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:25)
	at jdk.proxy2/jdk.proxy2.$Proxy175.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.suite.contract.BffDelegationContractTest.createPendingConsumerDelegationId(BffDelegationContractTest.java:94)
	at it.pagopa.interop.suite.contract.BffDelegationContractTest.lambda$rejectConsumerDelegation$3(BffDelegationContractTest.java:84)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.materializeSource(HttpContractRuntimeCaseExecutor.java:81)
	... 4 more
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@a06adc0> within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 28 more

````


---

### 65. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"e532c94e-2ddd-4aac-893a-5357145c50d5","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-5LhMfiMzrg79H3x","description":"description-wAKNnzd73xYzjq8IfKTg","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"e23dcd06-e9db-4a12-b586-4a4f328e510b"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 66. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"7b5c1264-3951-4858-bf67-0d79b59ba259","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-7cIYN4ais9qi3Tu","description":"description-lM8Qy9J1j2S8kXc7jjJy","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"b5900b1b-c951-4553-bbeb-dbe460e7552e"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 67. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"eecdda9a-7eeb-42c9-94d3-a2be6cbede54","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-Fc9bQEJyja1AE5P","description":"description-4KXRp4jmRZUXEK8yHhUz","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"ba162a39-432b-4dfd-b5f2-75a73c53ab52"}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 68. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"15779264-522f-4b94-86af-de4e7a64ab73","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-Xqccc8fJ4Q2JkA9","description":"description-CHJdvHwYnRAE7A8EdA35","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
- pathParams: {"eServiceId":"66ee31d5-2f5e-4ab7-a71a-08a2aaa6ccec"}
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

### 69. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"70ff892c-e6fb-4da5-99fc-efa5bffa8b43","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-SoZb0vAZojWMF55","description":"description-scf5mKUM9Ykmq7u09ppL","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
- pathParams: {"eServiceId":"06b37047-6671-405f-ab07-5392be980896"}
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

### 70. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3d368587-08df-4bf2-9bf2-60417ff45f2c","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-MDZYcSHhjVbkL7N","description":"description-Sp1Zc3KJJq0NJaF","isFreeOfCharge":true,"freeOfChargeReason":null,"riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"d0d7c42d-795e-44fc-a185-85150f86a7ce"}
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

### 71. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f5bba8c3-dbfe-4239-a099-9af16b9cb235","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"title":"purpose-ObQr9PoYlDMxplw","description":"description-5w2KxjRL7sefkZp","isFreeOfCharge":true,"freeOfChargeReason":"reason-JEhX2jV7dT7mTtl","riskAnalysisForm":null,"dailyCalls":1}
- pathParams: {"purposeId":"7fa50c41-e377-4b27-b129-29455412db69"}
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

### 72. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/institutionalPurpose

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
- responseBody: {"purposeId":"82edb558-f97d-4b51-9308-292ba697495d","versionId":"eff16d15-f925-4376-ab00-c3f33d66c748"}
- payload: {"title":"purpose-jyVpvjZs9rCOfSJ","description":"description-Th7IHTLDSr5XylC","isFreeOfCharge":true,"freeOfChargeReason":"reason-pd0fQoHAUI47K9o","riskAnalysisForm":{"version":"3.2","answers":{"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"82edb558-f97d-4b51-9308-292ba697495d"}
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

### 73. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"purposeId":"03b0cf45-81ed-418d-a77b-05910763556f","versionId":"785e4f50-8c16-40f4-8a77-c077666304ee"}
- payload: {"title":"purpose-58UUk0V0tyH5s4i","description":"description-WrjMreUiwjDN6l3","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"03b0cf45-81ed-418d-a77b-05910763556f"}
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

### 74. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

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
- responseBody: {"purposeId":"e609a40f-55f4-4c43-8169-19003acd8c67","versionId":"777648b6-a577-431a-b4b4-af15ee4b175e"}
- payload: {"title":"purpose-L7VwnrrDJJNQZEp","description":"description-696dktZX8D66rHc","isFreeOfCharge":true,"freeOfChargeReason":"reason-8qUs0DVwVonKmUI","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"e609a40f-55f4-4c43-8169-19003acd8c67"}
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

### 75. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c34d18c5-2786-45e0-beba-278bfe1fb976","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-Q1lMQsvQy7DHD4dmQj13","targetTenantKind":"PA","purposeTitle":"purpose-template-ibiZ6a2ZF6QcdDn","purposeDescription":"purpose-description-M0PDnF6dpdBbsnC5zWDF","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-SelDH2HW7zA7xHe5FHTD","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"abc8bcb9-008c-4597-8722-313946a999f0"}
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

### 76. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

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
- responseBody: {"id":"2b6cb3bc-2ddc-4121-84bc-b3f463e01292","targetDescription":"target-description-1AXOpTTeirlii2uyPqN5","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:25:37.778Z","updatedAt":"2026-11-26T21:25:38.738Z","purposeTitle":"purpose-template-Ri0rqm4vdDQm6xB","purposeDescription":"purpose-description-9kCg7JVZX9K2Ik8XsaOE","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"3d3c4ea3-40fc-4002-aa03-88f70da32ba2","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"c7627abb-df40-4e79-ae77-0a74bbed45c6","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"11126d41-1e60-41ea-9e59-52cb4157bcfc","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"31283b00-719f-40cb-bf5e-b340ed97c278","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"fa69f486-713f-486a-89d9-86c0d951e3af","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-SB1AIIg7erYoPOQYiewH","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-1AXOpTTeirlii2uyPqN5","targetTenantKind":"PA","purposeTitle":"purpose-template-Ri0rqm4vdDQm6xB","purposeDescription":"purpose-description-9kCg7JVZX9K2Ik8XsaOE","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-SB1AIIg7erYoPOQYiewH","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"2b6cb3bc-2ddc-4121-84bc-b3f463e01292"}
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

### 77. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

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
- responseBody: {"id":"38055689-43b0-4bba-b60f-14f9be9150a8","targetDescription":"target-description-L395Ot6M39N7nWu9UX9Q","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:26:15.579Z","updatedAt":"2026-11-26T21:26:17.993Z","purposeTitle":"purpose-template-TlV7BZwjW3OpKgz","purposeDescription":"purpose-description-IpB3WT6KOvWSXv9hoIZb","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"7d0e953d-166a-4436-b819-61d84c3c0375","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"a31773e6-d751-46ea-86ed-f931e155d116","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"19a0d007-a505-48db-bec1-867df2cd9cb9","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"8a1eadb1-0d7d-40fa-9aef-28a39f4b1116","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"fd55f5b5-cd9b-41d8-918d-9d0a4e158850","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-WJmtA5OEDzGO1jiVXLZn","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-L395Ot6M39N7nWu9UX9Q","targetTenantKind":"PA","purposeTitle":"purpose-template-TlV7BZwjW3OpKgz","purposeDescription":"purpose-description-IpB3WT6KOvWSXv9hoIZb","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-WJmtA5OEDzGO1jiVXLZn","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"38055689-43b0-4bba-b60f-14f9be9150a8"}
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

### 78. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

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
- responseBody: {"id":"553a7916-3464-46b5-b239-0d2e4a1b63df","targetDescription":"target-description-xCdr75XcxdooTFHSFizV","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:26:39.738Z","updatedAt":"2026-11-26T21:26:40.713Z","purposeTitle":"purpose-template-pnD51f9yYRHQTKw","purposeDescription":"purpose-description-MkNYdZ1rfNT0HTFmPR4O","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"b6022327-880f-49be-9b8c-241fda9aa526","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"ff2be5fd-da21-457e-8ad2-54a51291b710","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"62ba2441-933f-4c49-ae6e-e9eea4611173","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"68293a92-b62a-4174-8812-15222f1597e5","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"9312c6b4-9f34-4870-9327-b1c378b24880","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-nAkCTZYmAX2mK2z46Jd6","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-xCdr75XcxdooTFHSFizV","targetTenantKind":"PA","purposeTitle":"purpose-template-pnD51f9yYRHQTKw","purposeDescription":"purpose-description-MkNYdZ1rfNT0HTFmPR4O","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-nAkCTZYmAX2mK2z46Jd6","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"553a7916-3464-46b5-b239-0d2e4a1b63df"}
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

### 79. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

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
- responseBody: {"id":"40f8f304-654c-48f8-aa62-3b9b81291462","targetDescription":"target-description-FN8aTpc514UHXai1gejX","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:26:59.918Z","updatedAt":"2026-11-26T21:27:02.318Z","purposeTitle":"purpose-template-37vFa8HIcHCJgZA","purposeDescription":"purpose-description-nWF3h3eHYQUQTgCLoJkk","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"fa41861d-9e54-43b1-b665-746786b14149","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"bcf5e47b-d1d8-40bd-a9dc-04df49dba9f1","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"1b838ca2-95a5-41e1-ac5f-2e693fd28e09","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"847ec678-d5e0-4cc1-9052-7b109319f646","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"d622bb55-0663-4ba4-95f2-97580187d215","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-paPiyKnYF5C2qNhB2khi","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-FN8aTpc514UHXai1gejX","targetTenantKind":"PA","purposeTitle":"purpose-template-37vFa8HIcHCJgZA","purposeDescription":"purpose-description-nWF3h3eHYQUQTgCLoJkk","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-paPiyKnYF5C2qNhB2khi","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"40f8f304-654c-48f8-aa62-3b9b81291462"}
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

### 80. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

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
- responseBody: {"id":"7ce7ac16-ac5b-457b-8435-faea13e21584","targetDescription":"target-description-fM95qPaG1p7xQLsJpNpb","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:27:20.341Z","updatedAt":"2026-11-26T21:27:22.700Z","purposeTitle":"purpose-template-98rM4zzjxqJ1Q0P","purposeDescription":"purpose-description-Q2uSQQj12UIXWuZzUjGs","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"fd894ee7-6224-4d49-9e9a-987417a53cad","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"907c1cc1-001e-4ac9-984b-2790cb78520e","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"9f67fb6f-c327-447b-8607-72aa767d9a85","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"0541092b-9956-4829-a750-b147ef6a5169","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"c8ce5823-fe3d-4787-a96d-4eca2bb271e2","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-7ANGYPe2QURMh82Qwj2N","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-fM95qPaG1p7xQLsJpNpb","targetTenantKind":"PA","purposeTitle":"purpose-template-98rM4zzjxqJ1Q0P","purposeDescription":"purpose-description-Q2uSQQj12UIXWuZzUjGs","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-7ANGYPe2QURMh82Qwj2N","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"7ce7ac16-ac5b-457b-8435-faea13e21584"}
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

### 81. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeFreeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6374286f-cd99-46db-98af-6cf531db1b10","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"purposeFreeOfChargeReason\""}]}
- payload: {"targetDescription":"target-description-llGtls3ryNbHTonWV9yB","targetTenantKind":"PA","purposeTitle":"purpose-template-SEfoBg24jpkiM2M","purposeDescription":"purpose-description-wUlramKmDjoUauCfKmdE","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":null,"purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"edccf838-1e8c-4df7-9efb-0fe99f9380bf"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 82. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a1422001-c7ff-4fe9-9e9a-f822584774d8","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-IfmUq7trFa8FLUrVGF0o","targetTenantKind":"PA","purposeTitle":"purpose-template-F9Ev82EAQc9q98w","purposeDescription":"purpose-description-yaL4JA7vMzUY5bP1yQ8i","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-917WnohhLMg4rXyhqs6R","purposeDailyCalls":null,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"c6e2d1f9-8c8e-4903-9418-a75594412062"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 83. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

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
- responseBody: {"id":"3c1bcac6-681c-4a8a-b177-efd920068161","targetDescription":"target-description-HrbXvipnWEgBMXJKihiL","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:28:05.082Z","updatedAt":"2026-11-26T21:28:06.023Z","purposeTitle":"purpose-template-oS5INspZS94BmQO","purposeDescription":"purpose-description-uod6l8soWFQSx7X0ZHbu","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"bc8600c7-a459-4d80-8d5a-6bbf7f3fb952","values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"id":"e0ade44a-8b50-40d4-b73e-e33d76a78306","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"074adcda-4ad6-4d32-a064-c63e3caa66f0","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"58cb4fba-92d1-4284-89c4-e792a7ee85ed","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"e898d669-f4bd-46ea-b6d8-57d40e578fe0","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-DFvRVfTfeutOqEKrjg8c","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-HrbXvipnWEgBMXJKihiL","targetTenantKind":"PA","purposeTitle":"purpose-template-oS5INspZS94BmQO","purposeDescription":"purpose-description-uod6l8soWFQSx7X0ZHbu","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-DFvRVfTfeutOqEKrjg8c","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"3c1bcac6-681c-4a8a-b177-efd920068161"}
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

### 84. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"a2555fef-b5ac-4e46-8069-6316475ec9ab","targetDescription":"target-description-9dRv4fn65WbVX69JluTQ","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-26T21:29:00.936Z","updatedAt":"2026-11-26T21:29:03.328Z","purposeTitle":"purpose-template-zR8BxnbC93mLuQE","purposeDescription":"purpose-description-AeXlperuDhwbYjZbTJZG","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"c08c14a2-7585-4a40-a6c6-c814f43dbf0e","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"b144d400-1e63-4229-8592-d916851d618d","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"a29893c8-612f-4d56-b784-145f1c088409","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"251d6709-17cd-49a0-8410-7f08a8c9b288","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"d47f885e-04d9-4daa-abb7-e2bcfd1736ce","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-9dRv4fn65WbVX69JluTQ","targetTenantKind":"PA","purposeTitle":"purpose-template-zR8BxnbC93mLuQE","purposeDescription":"purpose-description-AeXlperuDhwbYjZbTJZG","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"a2555fef-b5ac-4e46-8069-6316475ec9ab"}
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

### 85. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"93ecceb0-53e4-4b65-b5f8-77fd12c02a60","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-j7aLnx66xr7DkyL","description":"description-xAHWK4VZ9t1Dz4T","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: {"purposeId":"616ac7ba-740c-4cd1-aafd-08c78602a040"}
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

### 86. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"purposeId":"5bbd7e26-fec6-4627-aea2-fe783ba8f869","versionId":"bba75c6e-f8c3-4ad4-95c2-4553a8884ddd"}
- payload: {"title":"purpose-YTEaj507W37iIXf","description":"description-Kop2D3eMNJF4lK1","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: {"purposeId":"5bbd7e26-fec6-4627-aea2-fe783ba8f869"}
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

### 87. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"116bea32-4762-45b8-bbe8-04a357185a7d","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}]}
- payload: {"eserviceId":"3e27f6e7-2d03-430e-bc64-3646055ffd47","descriptorId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 88. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"9d14d540-9f2d-4d35-bc7b-7cb2cc6a3258","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"567c40b9-d66f-42a6-9f37-fed9188da957","descriptorId":"b228e3df-4586-48c8-81a9-d781d4b13e61","delegationId":"00000000-0000-0000-0000-000000000000"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$1(DefaultHttpContractPolicy.java:89)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 89. WebEServiceDetailsCatalogPageContractTest::checkCustomThresholdsOnCatalog10::Precondition failed

**RESULT**

KO dovuto alla precondizione del test, non alla chiamata di reject. La creazione della delega riceve sempre 403 (codice 010-0007): il tenant ... non è abilitato a ricevere deleghe.. . Il polling ritenta il 403 per 20 secondi e poi va in timeout, nascondendo l'errore reale.

Una possibile causa del KO è il parallelismo tra scenari che modificano la disponibilità a ricevere deleghe di uno stesso ente condiviso. Una modifica concorrente può alterare la precondizione del test e produrre un falso KO. I log disponibili non identificano la modifica responsabile di questa specifica esecuzione.

**PROCEDURE**

Eseguire in locale lo scenario e se funziona: 1. Aprire il log del test. "Precondition failed" indica che il KO avviene nella preparazione. 2. Seguire la causa annidata, polling UNTIL_SUCCESS scaduto dopo 20 secondi è solo l'effetto finale. 3. Cercare nel log le POST relative alle deleghe e leggere lo status delle risposte: tutte 403. 4. Leggere il body: codice 010-0007, "Tenant ... not allowed to receive delegations...". Annotare i correlationId.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19759

**ERROR LOG**

````text
java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.delegation.infrastructure.BffDelegationGateway.createConsumerDelegation(BffDelegationGateway.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy159.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.common.delegation.application.DelegationUseCase.createConsumerDelegation(DelegationUseCase.java:33)
	at it.pagopa.interop.common.journey.infrastructure.DelegationJourneyImpl.createConsumerDelegation(DelegationJourneyImpl.java:31)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:25)
	at jdk.proxy2/jdk.proxy2.$Proxy175.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.prepareCustomThresholdsForOtherTenant(WebEServiceDetailsCatalogPageContractTest.java:438)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.checkCustomThresholdsOnCatalog10(WebEServiceDetailsCatalogPageContractTest.java:278)
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@cae61a7> within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 23 more

````


---

### 90. WebEServiceDetailsCatalogPageContractTest::checkCustomThresholdsOnCatalog7::Precondition failed

**RESULT**

KO dovuto alla precondizione del test, non alla chiamata di reject. La creazione della delega riceve sempre 403 (codice 010-0007): il tenant ... non è abilitato a ricevere deleghe.. . Il polling ritenta il 403 per 20 secondi e poi va in timeout, nascondendo l'errore reale.

Una possibile causa del KO è il parallelismo tra scenari che modificano la disponibilità a ricevere deleghe di uno stesso ente condiviso. Una modifica concorrente può alterare la precondizione del test e produrre un falso KO. I log disponibili non identificano la modifica responsabile di questa specifica esecuzione.

**PROCEDURE**

Eseguire in locale lo scenario e se funziona: 1. Aprire il log del test. "Precondition failed" indica che il KO avviene nella preparazione. 2. Seguire la causa annidata, polling UNTIL_SUCCESS scaduto dopo 20 secondi è solo l'effetto finale. 3. Cercare nel log le POST relative alle deleghe e leggere lo status delle risposte: tutte 403. 4. Leggere il body: codice 010-0007, "Tenant ... not allowed to receive delegations...". Annotare i correlationId.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19759

**ERROR LOG**

````text
java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.delegation.infrastructure.BffDelegationGateway.createConsumerDelegation(BffDelegationGateway.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy159.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.common.delegation.application.DelegationUseCase.createConsumerDelegation(DelegationUseCase.java:33)
	at it.pagopa.interop.common.journey.infrastructure.DelegationJourneyImpl.createConsumerDelegation(DelegationJourneyImpl.java:31)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:25)
	at jdk.proxy2/jdk.proxy2.$Proxy175.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.prepareCustomThresholdsForOtherTenant(WebEServiceDetailsCatalogPageContractTest.java:438)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.checkCustomThresholdsOnCatalog7(WebEServiceDetailsCatalogPageContractTest.java:212)
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@5616df43> within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 23 more

````


---

### 91. WebEServiceDetailsCatalogPageContractTest::checkCustomThresholdsOnCatalog8::Precondition failed

**RESULT**

KO dovuto alla precondizione del test, non alla chiamata di reject. La creazione della delega riceve sempre 403 (codice 010-0007): il tenant ... non è abilitato a ricevere deleghe.. . Il polling ritenta il 403 per 20 secondi e poi va in timeout, nascondendo l'errore reale.

Una possibile causa del KO è il parallelismo tra scenari che modificano la disponibilità a ricevere deleghe di uno stesso ente condiviso. Una modifica concorrente può alterare la precondizione del test e produrre un falso KO. I log disponibili non identificano la modifica responsabile di questa specifica esecuzione.

**PROCEDURE**

Eseguire in locale lo scenario e se funziona: 1. Aprire il log del test. "Precondition failed" indica che il KO avviene nella preparazione. 2. Seguire la causa annidata, polling UNTIL_SUCCESS scaduto dopo 20 secondi è solo l'effetto finale. 3. Cercare nel log le POST relative alle deleghe e leggere lo status delle risposte: tutte 403. 4. Leggere il body: codice 010-0007, "Tenant ... not allowed to receive delegations...". Annotare i correlationId.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19759

**ERROR LOG**

````text
java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.delegation.infrastructure.BffDelegationGateway.createConsumerDelegation(BffDelegationGateway.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy159.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.common.delegation.application.DelegationUseCase.createConsumerDelegation(DelegationUseCase.java:33)
	at it.pagopa.interop.common.journey.infrastructure.DelegationJourneyImpl.createConsumerDelegation(DelegationJourneyImpl.java:31)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:25)
	at jdk.proxy2/jdk.proxy2.$Proxy175.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.prepareCustomThresholdsForOtherTenant(WebEServiceDetailsCatalogPageContractTest.java:438)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.checkCustomThresholdsOnCatalog8(WebEServiceDetailsCatalogPageContractTest.java:234)
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@4e246f3d> within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 23 more

````


---

### 92. WebEServiceDetailsCatalogPageContractTest::checkCustomThresholdsOnCatalog9::Precondition failed

**RESULT**

KO dovuto alla precondizione del test, non alla chiamata di reject. La creazione della delega riceve sempre 403 (codice 010-0007): il tenant ... non è abilitato a ricevere deleghe.. . Il polling ritenta il 403 per 20 secondi e poi va in timeout, nascondendo l'errore reale.

Una possibile causa del KO è il parallelismo tra scenari che modificano la disponibilità a ricevere deleghe di uno stesso ente condiviso. Una modifica concorrente può alterare la precondizione del test e produrre un falso KO. I log disponibili non identificano la modifica responsabile di questa specifica esecuzione.

**PROCEDURE**

Eseguire in locale lo scenario e se funziona: 1. Aprire il log del test. "Precondition failed" indica che il KO avviene nella preparazione. 2. Seguire la causa annidata, polling UNTIL_SUCCESS scaduto dopo 20 secondi è solo l'effetto finale. 3. Cercare nel log le POST relative alle deleghe e leggere lo status delle risposte: tutte 403. 4. Leggere il body: codice 010-0007, "Tenant ... not allowed to receive delegations...". Annotare i correlationId.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19759

**ERROR LOG**

````text
java.lang.IllegalStateException: Polling in timeout dopo 20 secondi.

	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:68)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:93)
	at it.pagopa.infrastructure.template.action.PollingAction.handle(PollingAction.java:32)
	at it.pagopa.infrastructure.template.action.TestChain.withPolling(TestChain.java:23)
	at it.pagopa.interop.bff.delegation.infrastructure.BffDelegationGateway.createConsumerDelegation(BffDelegationGateway.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy159.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.common.delegation.application.DelegationUseCase.createConsumerDelegation(DelegationUseCase.java:33)
	at it.pagopa.interop.common.journey.infrastructure.DelegationJourneyImpl.createConsumerDelegation(DelegationJourneyImpl.java:31)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.interop.common.journey.infrastructure.config.JourneyInvocationHandler.invoke(JourneyInvocationHandler.java:25)
	at jdk.proxy2/jdk.proxy2.$Proxy175.createConsumerDelegation(Unknown Source)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.prepareCustomThresholdsForOtherTenant(WebEServiceDetailsCatalogPageContractTest.java:438)
	at it.pagopa.interop.suite.contract.WebEServiceDetailsCatalogPageContractTest.checkCustomThresholdsOnCatalog9(WebEServiceDetailsCatalogPageContractTest.java:256)
Caused by: org.awaitility.core.ConditionTimeoutException: Lambda expression in it.pagopa.utils.async.PollingUtils expected the predicate to return <true> but it returned <false> for input of <it.pagopa.infrastructure.response.ApiResponse@6b65f7fe> within 20 seconds.
	at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:167)
	at org.awaitility.core.AbstractHamcrestCondition.await(AbstractHamcrestCondition.java:86)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:1160)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:712)
	at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:729)
	at it.pagopa.utils.async.PollingUtils.pollUntil(PollingUtils.java:48)
	... 23 more

````


---

### 93. WebConsumerPurposeDetailsPageContractTest::consumerPurposeDetailsPageContract::Consumer purpose details page shows the expected content and filters

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
expected: "description-rWOBclvKJgHzPXI"
 but was: null
at WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$1(WebConsumerPurposeDetailsPageContractTest.java:81)
	at it.pagopa.interop.suite.contract.WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$2(WebConsumerPurposeDetailsPageContractTest.java:71)

````

</details>
