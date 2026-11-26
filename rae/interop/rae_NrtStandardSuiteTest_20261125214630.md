# 20261125 Report Analisi Esiti (RAE) NrtStandardSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release |  |
| Scope | NIGHTLY |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19821 |
| Data e ora | 2026-11-25T21:09:59Z |
| Durata | 00:35:58 |
| Numero KO | 116 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/45701583162/attempts/1 |
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

## 20 - KO DOVUTI A BUG DI PRODOTTO NOTI

<details>
<summary>Mostra/nascondi 20 KO</summary>

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

### 3. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"3b8fe7ca-ab63-4cd2-a4b9-e9eaee2d09af","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"a5a5f575-fb9d-4425-93cf-a36e7778e012"}
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

### 4. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"c23d3b80-316b-4ebc-8b8e-5f56ff943e71","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}],"userMessage":"Non puoi richiedere la fruizione di questa versione perché non è la più recente. Aggiorna la pagina e, se disponibile, crea una richiesta per l'ultima versione."}
- payload: {"eserviceId":"73905636-5c81-4002-991f-bcd4207dbbfd","descriptorId":"00000000-0000-0000-0000-000000000000"}
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

### 5. BffAgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"fbee282c-c653-431c-a5f0-dd44b64eddde","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"121bb4c6-725f-422f-9436-1dc81734ea6a","descriptorId":"c2711a83-c8be-4b8f-bc82-afcdb0ab32b2","delegationId":"00000000-0000-0000-0000-000000000000"}
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

### 6. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_WRONG_TYPE_NUMBER @ /purposeTemplateId

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 500. L'operazione termina con un errore interno invece di gestire il parametro di path. Il formato del parametro richiede una risposta 400. Il difetto di prodotto è tracciato in PIN-11455.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_WRONG_TYPE_NUMBER di BffPurposeTemplateContractTest::publishPurposeTemplate su POST /purposeTemplates/{purposeTemplateId}/publish. Inviare un numero in /purposeTemplateId al posto del tipo previsto. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Verificare che la risorsa di partenza sia nello stato richiesto dall'operazione.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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

### 7. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

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

### 8. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_EMPTY_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione vuota non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11605.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_EMPTY_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa vuota, mantenendo la proprietà presente. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- pathParams: {"delegationId":"db9903e0-6f9a-4e0b-8829-871e7cfc2d22"}
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

### 9. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_BLANK_STRING @ /rejectionReason

**RESULT**

La suite attende HTTP 400; il BFF restituisce HTTP 204. La motivazione composta da soli spazi non fornisce un contenuto effettivo. Il rifiuto della delega viene accettato con HTTP 204. Il difetto di prodotto è tracciato in PIN-11444.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_BLANK_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con tre spazi e controllare che il valore venga trasmesso senza normalizzazioni. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

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
- pathParams: {"delegationId":"f201c834-5595-45e2-a482-4eb19d7a1a8e"}
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

### 10. BffDelegationContractTest::rejectConsumerDelegation::[payload] REPLACED_WITH_LONG_STRING @ /rejectionReason

**RESULT**

Il setup iniziale utilizzava una delega assente e riceveva HTTP 404, impedendo la verifica della motivazione. Con una delega esistente e rifiutabile, la richiesta con rejectionReason di 5.000 caratteri restituisce HTTP 204 anziché 400. Il difetto di prodotto consiste nell'accettazione di una motivazione eccessivamente lunga e nella mancanza di un limite dichiarato e applicato. Il caso è tracciato in PIN-11604.

**PROCEDURE**

Eseguire il solo scenario REPLACED_WITH_LONG_STRING di BffDelegationContractTest::rejectConsumerDelegation su POST /consumers/delegations/{delegationId}/reject. Sostituire /rejectionReason con una stringa di 5.000 caratteri, mantenendo validi gli altri dati. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore. Predisporre per ogni scenario una delega distinta, esistente e in stato rifiutabile, con un chiamante autorizzato al rifiuto.

**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**

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
- pathParams: {"delegationId":"b0febb79-e221-4ee0-b60a-0ae23da87bed"}
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
- pathParams: {"delegationId":"703698d0-d0c9-4263-8313-6e73192dfa68"}
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
- pathParams: {"delegationId":"1c0960c3-c99c-4955-9392-bf000562dcac"}
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
- pathParams: {"delegationId":"5d85e689-1cad-491d-84e1-2e58a857a12b"}
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

### 16. WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: 11 sezioni ordinate, titoli esatti e paragrafi non vuoti

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

### 17. WebProducerAgreementContractTest::shouldShowAgreementDetailToProducer::richiesta di fruizione ricevuta visibile all'utente API del Comune di Milano

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
	at jdk.proxy2/jdk.proxy2.$Proxy263.readAndAssert(Unknown Source)
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
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

````


---

### 18. WebProducerPurposeContractTest::shouldShowPurposeDetailToProducer::finalità ricevuta visibile all'utente API del Comune di Milano

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
	at jdk.proxy2/jdk.proxy2.$Proxy358.assertLoaded(Unknown Source)
	at it.pagopa.interop.suite.contract.WebProducerPurposeContractTest.lambda$shouldShowPurposeDetailToProducer$4(WebProducerPurposeContractTest.java:86)

````


---

### 19. WebProducerAgreementContractTest::shouldShowReceivedAgreementListToProducer::richieste di fruizione ricevute accessibili all'utente API del Comune di Milano

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
	at jdk.proxy2/jdk.proxy2.$Proxy263.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementListPage.assertLoaded(ProducerAgreementListPage.java:20)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy308.assertLoaded(Unknown Source)
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

### 20. WebProducerPurposeContractTest::shouldShowReceivedPurposeListToProducer::finalità ricevute visibili all'utente API del Comune di Milano

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
	at jdk.proxy2/jdk.proxy2.$Proxy263.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.purpose.infrastructure.page.ProducerPurposeListPage.assertLoaded(ProducerPurposeListPage.java:29)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy356.assertLoaded(Unknown Source)
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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"267cdbf9-b144-48aa-bee7-97e3c7d8b6bf","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"8e9977ba-bf06-43fc-aeba-60bf3ae4b81b","descriptorId":"d6d106e4-e86e-401f-9a67-af068b14dced","delegationId":null}
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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"869ec5c9-b691-4f99-b218-66107e13f914","errors":[{"code":"017-9999","detail":"Validation error: Expected string, received null at \"delegationId\""}]}
- payload: {"eserviceId":"7edb1ec0-87df-4c64-ab9a-4c70540453d7","descriptorId":"5da5d6e2-e36c-4035-b573-56da1c96cc11","delegationId":null}
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
	at jdk.proxy2/jdk.proxy2.$Proxy326.getHref(Unknown Source)
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

## 91 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 91 KO</summary>

### 1. [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente [Given=BFF,When=BFF,Then=BFF] [Examples: (senza nome) | Example #4 | line: 54]

<table>
<tbody><tr><td width="100%">
<details>
<summary><strong>⚠️ Corrispondenza storica da validare</strong></summary>
<p>L’analisi è stata recuperata da una RAE precedente. Verificare le differenze prima di confermarla.</p>
</details>
</td></tr></tbody>
</table>

**RESULT**

Nel KO analizzato sono presenti tutti e tre gli e-service attesi, ma il loro ordine relativo è diverso da quello previsto. La ri-esecuzione ha restituito una sequenza diversa da quella del KO iniziale. In QA il disallineamento è stato osservato per tutti e quattro i criteri espliciti di ordinamento per nome e data di creazione, sia crescente sia decrescente. L'ordine relativo deve rispettare il criterio selezionato sull'insieme dei risultati paginati e restare stabile a parità di dati, filtri e criterio. La causa tecnica resta da accertare.

**PROCEDURE**

Rieseguire in locale PRIVATE_CATALOG_ORDERING_PAGINATION_2 per l'esempio in KO, con un breakpoint all'inizio dell'hook After, prima che il teardown archivi o elimini gli e-service. Con l'esecuzione in pausa, consultare i log della riesecuzione e risalire ai nomi dei tre e-service a partire dagli ID riportati nell'asserzione. Confrontare il loro ordine relativo con quello atteso per il criterio selezionato. Mantenere l'esecuzione in pausa e ripetere da frontend la ricerca del Gherkin nel catalogo autenticato come PagoPA. Cercare «Sorte», filtrare per erogatore «Comune di Milano» e selezionare lo stesso ordinamento dell'esempio in KO. Scorrere tutte le pagine e verificare l'ordine relativo dei tre e-service identificati nei log. Ripetere la ricerca con gli stessi dati, filtri e ordinamento per verificare se la sequenza resta stabile.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11313

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Relative e-service order across catalog pages] 
Expecting actual:
  [d9171885-90b4-4a0e-9349-83c49564da86,
    95ef60e2-1cec-4175-81bf-eb9513d1bb46,
    edf620ea-5498-40a2-8d4d-4da6c65c3525]
to contain exactly (and in same order):
  [d9171885-90b4-4a0e-9349-83c49564da86,
    edf620ea-5498-40a2-8d4d-4da6c65c3525,
    95ef60e2-1cec-4175-81bf-eb9513d1bb46]
but there were differences at these indexes:
  - element at index 1: expected "edf620ea-5498-40a2-8d4d-4da6c65c3525" but was "95ef60e2-1cec-4175-81bf-eb9513d1bb46"
  - element at index 2: expected "95ef60e2-1cec-4175-81bf-eb9513d1bb46" but was "edf620ea-5498-40a2-8d4d-4da6c65c3525"

	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.scanRelativeOrder(EServiceCatalogPaginationUseCase.java:162)
	at it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase.shouldContainInRelativeOrder(EServiceCatalogPaginationUseCase.java:82)
	at it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps.shouldContainEServicesInRelativeOrder(EServiceCatalogAssertionSteps.java:63)
	at ✽.tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:(classpath:feature-expanded/catalog/private-catalog-ordering-pagination.feature:43)

````


---

### 2. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.1] - Impossibilità di archiviare un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

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

### 3. [ESERVICE_ARCHIVING_GRACE_PERIOD_1.2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione [Given=BFF,When=WEB,Then=WEB]

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

### 4. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_NULL @ /answerData/annotation

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6a77ca41-f2f8-47e1-81f2-0325b077bb2b","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"answerData.annotation\""}]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":null,"suggestedValues":["answer-value-AXg834AuuqeSdBW"]}}
- pathParams: {"purposeTemplateId":"8c0c4ec1-625c-41f9-94da-f0c4626fac92"}
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

### 5. BffPurposeTemplateContractTest::addPurposeTemplateRiskAnalysisAnswer::[payload] REPLACED_WITH_BLANK_STRING @ /answerData/suggestedValues/0

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
- responseBody: {"id":"a31c2775-4323-4b25-bb00-f46408168967","values":[],"editable":false,"annotation":{"id":"d20a6928-b8b0-479b-81a2-fdae047df2ec","text":"annotation-VPq9xGyXo27YkZTHXzoW","docs":[]},"suggestedValues":["   "]}
- payload: {"answerKey":"institutionalPurpose","answerData":{"values":[],"editable":false,"annotation":{"text":"annotation-VPq9xGyXo27YkZTHXzoW"},"suggestedValues":["   "]}}
- pathParams: {"purposeTemplateId":"a09fe4bb-c1eb-480a-81e7-785bc266f0a4"}
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

### 6. BffClientContractTest::createApiClient::[payload] REPLACED_WITH_NULL @ /description

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"5adbd858-98bf-4b27-b64a-bb1090a2bde8","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-DJcWaiv5VSneLTNdOBcseF9a","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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

### 7. BffClientContractTest::createApiClient::[payload] REMOVED @ /members/0

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
- responseBody: {"id":"5c14e268-8456-4d95-8b8a-d6f48dbf4da8"}
- payload: {"name":"client-ZclKpBVhTfQGKEPYkt3vFdEi","description":"description-sqBFLW8MdxArJATqtGkZd0sbvQbyMPi5","members":[]}
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

### 8. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"de6ad7eb-34e2-49e8-bfb9-8b40e87981ec","code":"c3389e18c285a1d3e6ac7bb488731888514907611eef115354a8c047c5249f50","kind":"CERTIFIED","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-etlZowKzXXmYGwnRYJER6LBv","creationTime":"2026-11-25T21:25:24.252Z"}
- payload: {"description":"   ","name":"name-etlZowKzXXmYGwnRYJER6LBv"}
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

### 9. BffCertifiedAttributeContractTest::createCertifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"16024c89-66a8-4be7-8a69-dabe475dcdbd","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-TdYAbMr2jiAaab9IxBHUnZ8yfx3YQN75","name":"   "}
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

### 10. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"7fbcb303-8aa2-4e92-9a01-8f9923d87061","code":"3fa6977e77e11af04ee30d84d08e9afcc93ff69a896276623b2febe19b6e70de","kind":"CERTIFIED_DISCRETE","description":"   ","origin":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","name":"name-OdFNN0aW8kzB1i8llJk3Im9J","creationTime":"2026-11-25T21:38:41.868Z"}
- payload: {"description":"   ","name":"name-OdFNN0aW8kzB1i8llJk3Im9J"}
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

### 11. BffCertifiedDiscreteAttributeContractTest::createCertifiedDiscreteAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name or code","status":409,"detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists","correlationId":"1078e54a-f739-4db1-8f23-03119011a335","errors":[{"code":"003-0002","detail":"An attribute with name     or code 0aad7da77d2ed59c396c99a74e49f3a4524dcdbcb5163251b1433d640247aeb4 and origin e79a24cd-8edc-441e-ae8d-e87c3aea0059 already exists"}],"userMessage":"Esiste già un attributo certificato con lo stesso nome. Scegli un altro nome."}
- payload: {"description":"description-7E1KBir8eZdwn9AdBSC6uuS7hdj3RKWA","name":"   "}
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
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 12. BffClientContractTest::createConsumerClient::[payload] REPLACED_WITH_NULL @ /description

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ec9ed8f0-7ab4-468d-a1f5-70942ac7d9a9","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"description\""}]}
- payload: {"name":"client-VInMSjihfDZBIfiDjDNrXgs0","description":null,"members":["5d717b97-5308-49a5-8682-187663278f24"]}
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

### 13. BffClientContractTest::createConsumerClient::[payload] REMOVED @ /members/0

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
- responseBody: {"id":"cb6abe74-d5fe-4e66-a657-7fd7c4765486"}
- payload: {"name":"client-VInMSjihfDZBIfiDjDNrXgs0","description":"description-2ItnBkRFFrDrocjNuTKm1tCMsRw4vEVH","members":[]}
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

### 14. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"a6c4e5c2-540e-4f12-86dc-da951ea07e60","kind":"DECLARED","description":"   ","name":"name-itMNZCckXxjgTEr2flSq72Zz","creationTime":"2026-11-25T21:22:16.534Z"}
- payload: {"description":"   ","name":"name-itMNZCckXxjgTEr2flSq72Zz"}
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

### 15. BffDeclaredAttributeContractTest::createDeclaredAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"id":"7fd750d9-87f1-485d-9ded-0a30498e2ed3","kind":"DECLARED","description":"description-vz0yvXlNpu7r7PaNcs0PrLUCfQWI8KyI","name":"   ","creationTime":"2026-11-25T21:22:19.731Z"}
- payload: {"description":"description-vz0yvXlNpu7r7PaNcs0PrLUCfQWI8KyI","name":"   "}
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

### 16. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ebc4adc4-5b64-45fe-9b81-5b12cdef3696","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-atTOEeGRRcBVmmE","description":"description-fuijeja5LutXKgVZLVvJ","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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

### 17. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"5778e79f-9ca9-48bd-958a-70fc615806a7","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-3s0Jk9KAP6qEJN4","description":"description-3cf9fuLzSf1gb8JkoWOj","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
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

### 18. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"4c152a50-f5c9-4e2a-8a12-00dcd2e999e4","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-dopxtOUpU9b1DWW","description":"description-o9cYmEi4Bi5WWJxcbiPr","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
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

### 19. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"56d07c59-adf1-49f6-a8dc-9eb77abb9130","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-CtYvH3oLyaAYcQ5","description":"description-9fjCjMLWON2NJ1K76kd6","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
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

### 20. BffEServiceContractTest::createEService::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"0bffbac1-a122-40e8-8310-254a7f9c7dcc","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-99KDN7UwVqi20As","description":"description-9IY4MO4v7JE4MydzqNUI","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
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

### 21. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c1f6bb55-96e4-4b16-8f58-d909c7fed468","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"version\""}]}
- payload: {"name":"eservice-template-HrbGgc4ZF4LtZr0","intendedTarget":"target-T6Z1Ol5qWKaCyhtYMLD5IUVv3KmUox","description":"description-eNcV9LNLWEUIybG7RwZci7V1f2XSXtGbDLV5fPiIbp5nZ30Ua6","technology":"REST","mode":"DELIVER","version":null,"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 22. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsPerConsumer

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"10af084d-4832-4ade-b105-f5e7d324021d","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsPerConsumer\""}]}
- payload: {"name":"eservice-template-H3WM4fxJ8esDXWO","intendedTarget":"target-L9G8T0SzEM9Xb98g182QwZ9oUEPvBX","description":"description-UHXcuz6MQfJiQ2LLDYv1PzAMt8fc4TkCjErHooYquo55CY2DJq","technology":"REST","mode":"DELIVER","version":{"description":"version-description-YVVfcfREtWnEbXLDNw4zApPKtJEOSBTxcDK","voucherLifespan":60,"dailyCallsPerConsumer":null,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 23. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/dailyCallsTotal

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"aee2445e-ce06-4c36-a50f-fa1cf8a5d02d","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"version.dailyCallsTotal\""}]}
- payload: {"name":"eservice-template-vKh1zxY3TieTbMf","intendedTarget":"target-Mz4hS0ItExcAIPCMOt6rUisUJFmaFc","description":"description-YN6wQaKrPuWwM7TFUHOeICxeOzXQeOkSbc0pOXaWe6ReULdAgP","technology":"REST","mode":"DELIVER","version":{"description":"version-description-9rCJO3SE3IybefPrd2g2FG3J4tjbUtZJyoz","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":null,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 24. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /version/agreementApprovalPolicy

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"cd02c47f-75d3-4487-9bf3-e647678e205f","errors":[{"code":"008-9999","detail":"Validation error: Expected 'AUTOMATIC' | 'MANUAL', received null at \"version.agreementApprovalPolicy\""}]}
- payload: {"name":"eservice-template-IgD7M0eTBMDtqEl","intendedTarget":"target-FGfvZ2aeUZVfRY3MV6rALEtA8FUjfC","description":"description-LKgkG7TwUkmx7b7i5pLlm2b0HpuCpiqakzRVLsNAi76utQ7Gyh","technology":"REST","mode":"DELIVER","version":{"description":"version-description-stV5qGaB1rH1oPoJeU7j0Wul5LsxnTSRp8M","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":null},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":false}
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

### 25. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a95c6802-bf38-43b2-9f0b-702512eda980","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-template-qj5t4nDx64sBreP","intendedTarget":"target-83fY5T5pmXot79QSu65vMLzxPtaEUt","description":"description-jUxkwnOLJ7MjbJBOiHJORF4qZ5k6k4VKW86rIyx6ZAUt09JEPo","technology":"REST","mode":"DELIVER","version":{"description":"version-description-1cJGb0WGse0buIB3OgcIQCsoW4y339PQB0d","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":null,"personalData":false,"asyncExchange":false}
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

### 26. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c250dd01-1722-465e-9b8a-2e5a503a36cd","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-template-VoFz8p0kutkB8sb","intendedTarget":"target-3UzcTfDxK5iKa2Oy1Z4QbRPQLyb06L","description":"description-UoA1bTqfjBYxNkpBhi23wR0285KBAZgzVGugsSDIokRJhv9k65","technology":"REST","mode":"DELIVER","version":{"description":"version-description-MOK60uHdB6oKMGoV3AQNVuet0c1XnI12QOx","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":null,"asyncExchange":false}
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

### 27. BffEServiceTemplateContractTest::createEServiceTemplate::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c53596b1-2f14-4856-8474-50f2dcb53f97","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-template-0d2J4yfIPaH1qXb","intendedTarget":"target-p6JuJc675hLtIc687pnq7fl7f0A6TR","description":"description-VTAXBQ67b5k2Lh8QTWKbGgprDtg3Lgrw2T4XFSjvCMWoxGFN5z","technology":"REST","mode":"DELIVER","version":{"description":"version-description-YjZVq2QLjPWGjHEhhXRi8CqgJpgGayuD6tg","voucherLifespan":60,"dailyCallsPerConsumer":1,"dailyCallsTotal":10,"agreementApprovalPolicy":"AUTOMATIC"},"isSignalHubEnabled":false,"personalData":false,"asyncExchange":null}
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

### 28. BffClientContractTest::createKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzaUNXNXVjVXVXQ0hJbUY2dkNaSAorQVpLbTFIblY0b2ZqeGs3SjIxaUt3alEwaE9aYVMwbHozWHRXRWNzN1hZaDd0eVM5blFCd3ErUGZZVVpyZDM1ClpyZ1V6MWU2UVNKWGJ4TGloYm1GRmwxejFUWkJRQW9QMG5hdVRERFkwZE1VMnJiVkFacWlraUhGdE9oeXF6QmkKY2RNODlheGsxMTVmYmh4RHRQM1l3ZUZ5NG9DSFJoS04wRnlWOUo4UUxrREVIL1NBaWtWZ3ZkT0tVNEtkQU40OAp2T25FUW1CT2g0d1RtVW5Jem9VWEcvZ0p1QjJjTU8zQWsvOE41ckFKYjBYajlvcU9UL01HenZFZU1TeVoxbWRNCnlkZms0VTlnblF6bEtpb2pOdllZdWtmbHR3VU5Pd0diY3UrS3lwY29peU9wUTZjM0FiZ1JuN0I1clVEcG9hTFgKWXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"key-f2z3rOc2cIRVgDf"}
- pathParams: {"clientId":"ff071d3d-f30f-49de-bac3-79b55dbbf210"}
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

### 29. BffClientContractTest::createKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid TP5hHi1vhwIGNk6H2AsA1oTnr54bvZ69SovN2lotKGQ already exists","correlationId":"5a3da2a7-8ece-46b9-8244-635251f9af0e","errors":[{"code":"006-0017","detail":"Key with kid TP5hHi1vhwIGNk6H2AsA1oTnr54bvZ69SovN2lotKGQ already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzaUNXNXVjVXVXQ0hJbUY2dkNaSAorQVpLbTFIblY0b2ZqeGs3SjIxaUt3alEwaE9aYVMwbHozWHRXRWNzN1hZaDd0eVM5blFCd3ErUGZZVVpyZDM1ClpyZ1V6MWU2UVNKWGJ4TGloYm1GRmwxejFUWkJRQW9QMG5hdVRERFkwZE1VMnJiVkFacWlraUhGdE9oeXF6QmkKY2RNODlheGsxMTVmYmh4RHRQM1l3ZUZ5NG9DSFJoS04wRnlWOUo4UUxrREVIL1NBaWtWZ3ZkT0tVNEtkQU40OAp2T25FUW1CT2g0d1RtVW5Jem9VWEcvZ0p1QjJjTU8zQWsvOE41ckFKYjBYajlvcU9UL01HenZFZU1TeVoxbWRNCnlkZms0VTlnblF6bEtpb2pOdllZdWtmbHR3VU5Pd0diY3UrS3lwY29peU9wUTZjM0FiZ1JuN0I1clVEcG9hTFgKWXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"key-f2z3rOc2cIRVgDf"}
- pathParams: {"clientId":"c809df9b-9ebd-4e0e-9e6c-235789bb3d0f"}
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

### 30. BffClientContractTest::createKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

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
- responseBody: {"type":"about:blank","title":"Key already exists","status":409,"detail":"Key with kid TP5hHi1vhwIGNk6H2AsA1oTnr54bvZ69SovN2lotKGQ already exists","correlationId":"ece0d87d-480b-4ff7-9bf1-3a0580e12810","errors":[{"code":"006-0017","detail":"Key with kid TP5hHi1vhwIGNk6H2AsA1oTnr54bvZ69SovN2lotKGQ already exists"}],"userMessage":"Hai inserito una chiave già registrata su PDND. Se te ne serve una nuova, genera una coppia RSA da almeno 2048 bit e inserisci la chiave pubblica in formato PEM."}
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFzaUNXNXVjVXVXQ0hJbUY2dkNaSAorQVpLbTFIblY0b2ZqeGs3SjIxaUt3alEwaE9aYVMwbHozWHRXRWNzN1hZaDd0eVM5blFCd3ErUGZZVVpyZDM1ClpyZ1V6MWU2UVNKWGJ4TGloYm1GRmwxejFUWkJRQW9QMG5hdVRERFkwZE1VMnJiVkFacWlraUhGdE9oeXF6QmkKY2RNODlheGsxMTVmYmh4RHRQM1l3ZUZ5NG9DSFJoS04wRnlWOUo4UUxrREVIL1NBaWtWZ3ZkT0tVNEtkQU40OAp2T25FUW1CT2g0d1RtVW5Jem9VWEcvZ0p1QjJjTU8zQWsvOE41ckFKYjBYajlvcU9UL01HenZFZU1TeVoxbWRNCnlkZms0VTlnblF6bEtpb2pOdllZdWtmbHR3VU5Pd0diY3UrS3lwY29peU9wUTZjM0FiZ1JuN0I1clVEcG9hTFgKWXdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"key-f2z3rOc2cIRVgDf"}
- pathParams: {"clientId":"26776b14-acd4-4254-a144-4978e5c5e23c"}
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

### 31. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_EMPTY_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF4cjZiRzE1dWdERTEzRFJuM1BQOQpNZHNiSFJQdUtnbS9sSk1xbXF2dGk1MDdVU1gwNlFmN0pnUnZCZEUxeFdxQjhEQ3VWRFpGZS9iejIxWHgxOEVHCmZ2S3U2N3ZCd1hGSWFaQ3VPRjZyYTVMTEZxenlXNUI0SmdHcVEyTTlkb2cvd2xtbkorZytXeTBwcUxldkdjem8KaExZY0N5TEhFbnFuTjdyY0dxTlEvbVRseTR1cEQ1dmZrMThwRk1UR1pUMVZjUjNmdDROd01ZSDhVTVpJaUVsOApodTlydHJzYTNnaG1WN3BxcXU2dGRUVUtOMXJTVFBBWHpHTjdiYndnREhkZ2treEIraFU4ZmtKdXB6QVBhcjBDCjhWa05GRkpLZmlQNEM3TU5hMldDTGRvNWtIMnMyeVd0cVU4MzVNWW5pWnExV0F4MjJsT2dwR3JCYnNONFh6Zy8KeFFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"","name":"producer-key-dJ09yot8GXOAQ1Z"}
- pathParams: {"producerKeychainId":"bea1b818-e289-4ef4-812b-b6c90f1a5d66"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 32. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_BLANK_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFuMkYyREY4d1FrdUJaOGVEQU1JSwpsR0VoYnJYMUh1R01UWXRENXY4bkJjMkxmZWZDQnZYeEh4clBJMU1HcUpZakt2SGF5TmdLeEtwRlI3R29JOEZ0CnQ0Tm1vMUQ4M21udS9JcnlENUJaR2pLVE4yRUZDU1FYM3psa25wZWU4MDdqQnRHNHZjbTJmUC9kWmpBdkplT2UKOCs0YWxXNEZyMUR2RnEwOVkrNFlrS3k5TXBOWHZFemxiMmVycUp4MWtObkhPWFhyb0lCY1F2OWl3QzlQalloWgpaQmxHM1BtaHl6ZVpRbVpvNU1lck5sWm5TOHozUVhXeHJiNnlJYWE1Zk9sazI4cklvNUdKWHVmanVZOGdQeUt4ClFTMXp5R2RuTUhOZ2poYkg1R3pRRTFGWWpJbVVOY21tSmFSSkpCOVBNK0NYSzZCam03ek02bHJ1ajU4UGhhNGsKZ1FJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"   ","name":"producer-key-rlndC5PcG1TkZaK"}
- pathParams: {"producerKeychainId":"2025364a-bbe8-4dba-87c0-93d9c7e28551"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 33. BffProducerKeychainContractTest::createProducerKey::[payload] REPLACED_WITH_LONG_STRING @ /alg

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
- payload: {"key":"LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFybWdNaUovazIxRzB4NFVqYmdDcgpiSG9hUFRNY0dsaHFJbG4zMVB6blBzRitTRVgvcFhrMy9NQ0d4U2Viai9oV0tBOVJYY2dVck1FWlN3WWNCakxWCklGcUxDNGxxd1RDWStvUU11aXZqOHo4Y3B6enhSbnJoM0Y0b3Z3b1JQb0d5NVF1NTZaYWpCQU5XODdibndIYlUKZ1N0TWxVS0RUVVdBdUpCRnZiK3VES0hZYm9WU1Q1alNMeTUzT0R4clRrZWRBZjIxMURoZisrYlJHR2R2S05CTwpwUzl6Z2xhTmgrNWlPOFduYUd3MExmUGhMaUZTMW5RNFFsakxkdVpIT3JrWndhL0hDV3R3T29oU1RyaVZoVHVUCnpxYW5qOTJmVWdkS2hMU3VoR0c1SURrblBzc2YweTdDSFVUbkJtTXNacnI5RW1HYXE5SnBwTGl3dXdhZjhRWjAKTFFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==","use":"SIG","alg":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","name":"producer-key-wrw45IwvL8ciRxB"}
- pathParams: {"producerKeychainId":"dc63e2fa-ae13-42a5-a553-738e92c121b7"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 10 more

````


---

### 34. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"b19ae902-5c7e-4a35-b44e-a01b0cf5cb41","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"eserviceId":"267aa2c6-72df-4e95-93a0-c7f88d6c4df0","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":null,"title":"purpose-X6aXyF0AaNrAgkH","description":"description-qxYapLUCJhlYM5F","isFreeOfCharge":true,"freeOfChargeReason":"reason-n9lFUQIJTzZ6iJd","dailyCalls":1}
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

### 35. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"711c1ccc-8786-436d-a944-d906bffae8aa","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"511466bf-3c82-4c03-8c50-6854ab8bbf03","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-QcqxOF9GP85u82v","description":"description-veTvubfgKPekH6p","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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

### 36. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

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
- responseBody: {"id":"f057fe94-14b4-4d15-ad01-3131ebd33e4e"}
- payload: {"eserviceId":"19ea40c4-499a-4e2f-8991-0052bdeddfb6","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-haGdQfiWrbIZdY1","description":"description-B9dhfSue4v9IcvQ","isFreeOfCharge":true,"freeOfChargeReason":"reason-dXg7r67Q648COYo","dailyCalls":1}
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

### 37. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"30866f4a-a864-428a-8c18-01758c34d885"}
- payload: {"eserviceId":"0f35ff59-1767-4fe3-a44a-e37ef96c5fff","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-twk2j7N5T6o6Bcj","description":"description-NY3ryAC7OjVSUdf","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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

### 38. BffPurposeContractTest::createPurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"30f78f7d-cf27-4c80-9b11-6321a61b7c98"}
- payload: {"eserviceId":"5635d1ab-66e8-40f3-931a-4c134ff480ab","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"title":"purpose-4JewVAQHdzbE7Gu","description":"description-rwByh4cYBHcd81n","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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

### 39. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"d93dfb8e-0b98-4dfb-889b-0f1e0e8df7bd","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"eserviceId":"b42d71d2-49e6-4a16-a94a-fe5a7b2abc2a","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"2266548a-9283-4601-a7ba-2909a4370622","title":"purpose-YdTzgyTzO06HmcA","description":"description-bfx90RfO13XRR1c","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
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

### 40. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"f0a999aa-4995-4d4b-bee1-3d54b5b4e8a1","errors":[{"code":"004-0002","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"1fffe46b-dc0a-482e-ac5d-4c36421a8092","title":"purpose-fw2o3UGbvAO8dPi","description":"description-EydiysFhdvquyLX","isFreeOfCharge":true,"freeOfChargeReason":"reason-SBGwxbC41JT21x3","dailyCalls":1}
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

### 41. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_NIL_UUID @ /riskAnalysisId

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
- responseBody: {"type":"about:blank","title":"Risk analysis not found","status":400,"detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService 448ab7e6-8b8c-4f81-9c73-c0374e82accd","correlationId":"96bbe717-3b08-4012-ac39-b0b34d7a5d49","errors":[{"code":"004-0019","detail":"Risk Analysis 00000000-0000-0000-0000-000000000000 not found for EService 448ab7e6-8b8c-4f81-9c73-c0374e82accd"}]}
- payload: {"eserviceId":"448ab7e6-8b8c-4f81-9c73-c0374e82accd","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"00000000-0000-0000-0000-000000000000","title":"purpose-27lwNYVAzRiG5l0","description":"description-CtywsUIL35OBF4f","isFreeOfCharge":true,"freeOfChargeReason":"reason-xM0tYhzT7KILNTP","dailyCalls":1}
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

### 42. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_BLANK_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"a86789e3-3220-417a-89aa-b4e2cb34e4bc"}
- payload: {"eserviceId":"ee296302-9a3a-43c0-ac99-a1b0975ab4d4","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"72192111-3e33-4f35-84a4-21b1484e696a","title":"purpose-eTMoJ5YnmZfFyJB","description":"description-aRoxKkMIzEZuAcY","isFreeOfCharge":true,"freeOfChargeReason":"   ","dailyCalls":1}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 43. BffPurposeContractTest::createPurposeForReceiveEservice::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"id":"4b22c957-51b1-4fca-b9e4-2c4e9ce76a76"}
- payload: {"eserviceId":"c200ef05-d8fa-427b-b8da-5d9172ae10ef","consumerId":"80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20","riskAnalysisId":"8c49c20c-9093-4819-8990-46ff52443bd9","title":"purpose-QGQbddZHxPxnykf","description":"description-UY9XWFsn3niOw0k","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
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
	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 7 more

````


---

### 44. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"ac170266-93dd-4bff-95c2-5c2dc4dab327","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-PvShV8bUQCqBobew3n6e","targetTenantKind":"PA","purposeTitle":"purpose-template-coeKs374dAOZmkQ","purposeDescription":"purpose-description-XU7MIpaRU9dqnwVO1Cfk","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-DyRo4tcihmiTbOvfjmla","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 45. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm

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
- responseBody: {"id":"de1a5f9f-8c6a-4d52-bddb-91d61292502a"}
- payload: {"targetDescription":"target-description-3JNVRNnfPzmEOPhar6gS","targetTenantKind":"PA","purposeTitle":"purpose-template-r3KH2ud14THq9cZ","purposeDescription":"purpose-description-t5pYyORqIz72ZpfdmpMx","purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-0fy44NzcSCwquBFpP3He","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 46. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

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
- responseBody: {"id":"bd2f1144-7770-4c18-a06d-d2bd8b4e187e"}
- payload: {"targetDescription":"target-description-O8XF9yfdbfr1fWjZBFIz","targetTenantKind":"PA","purposeTitle":"purpose-template-GbdDoqN5fO2MUhi","purposeDescription":"purpose-description-dQ9dObZj4pDVuffuDH8E","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-g9fJ9YKX7HVZ6a4lyNdu","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 47. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

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
- responseBody: {"id":"56109239-5d84-4199-a4b5-d9fe0f9a1b55"}
- payload: {"targetDescription":"target-description-WNM3rVkziTXc5yLz6fMk","targetTenantKind":"PA","purposeTitle":"purpose-template-l5KryzpOCYcU4by","purposeDescription":"purpose-description-MVSXb9Ur1bHvqIm9t7Ub","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-G9Ax4M2irDoXy0F8e98k","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 48. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

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
- responseBody: {"id":"96550524-668b-456e-a36a-9e92b795b66c"}
- payload: {"targetDescription":"target-description-YajLFGZ7zjRNbcl816Ar","targetTenantKind":"PA","purposeTitle":"purpose-template-gd6VyFaRi3e4FRa","purposeDescription":"purpose-description-rxEc9FOlH0Qj3iCktWnJ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-yVmgyEjSy8QsGDU9M6Gh","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 49. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

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
- responseBody: {"id":"3e7463cc-4e8b-4071-a377-e147488a2b03"}
- payload: {"targetDescription":"target-description-Lw5i9Pahiie2Mi7xx9iK","targetTenantKind":"PA","purposeTitle":"purpose-template-Ys6dBrfQ20oUAwd","purposeDescription":"purpose-description-HuDV43uxBTBOSokZSqi8","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-HZKKxlFQsyqfNAmkq9lY","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 50. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

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
- responseBody: {"id":"77bdb514-bb9b-458d-8161-69cbb6fc4fad"}
- payload: {"targetDescription":"target-description-15rmEcLrzX9dZCGqCdFG","targetTenantKind":"PA","purposeTitle":"purpose-template-UnqpoL5uf8ZtVfU","purposeDescription":"purpose-description-IVJdWj6lvIlz3IKQVSQ6","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-F4V3egHGdv2RmT8xL1GU","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 51. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"e2454a2c-2aeb-4f39-bd07-2fb267456800","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-JmeVzxMuklSNaU7RN3hc","targetTenantKind":"PA","purposeTitle":"purpose-template-AQ4OBf2M3T1gYjy","purposeDescription":"purpose-description-2ffFuD7XEUr4lqQdcJsi","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-oojFD6CjXNap0ZRsFPgj","purposeDailyCalls":null,"handlesPersonalData":false}
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

### 52. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REMOVED @ /purposeDailyCalls

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
- responseBody: {"id":"3f3caa3b-fb29-47cd-9ff3-c7b238f6371c"}
- payload: {"targetDescription":"target-description-LC7QelfZyXJvbMvQhGaC","targetTenantKind":"PA","purposeTitle":"purpose-template-bi90MMTPiU8AYvR","purposeDescription":"purpose-description-QkkOSuZViY1iw9I8ZAVS","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-iES4kARmwoVTamTrWD93","handlesPersonalData":false}
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

### 53. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

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
- responseBody: {"id":"d14f8699-cbd5-46f6-b7df-24bb933b71e7"}
- payload: {"targetDescription":"target-description-ynal6nI8EKBWi9Vv3CMo","targetTenantKind":"PA","purposeTitle":"purpose-template-iPOILtKU27nIdfV","purposeDescription":"purpose-description-Ce39vVfdv9UYWv5pL3qk","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-4WSK9kP18DX1NunG4oC0","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 54. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"683da936-c29c-4e11-8544-9d433c2fe5d6"}
- payload: {"targetDescription":"target-description-eMfuE2f7BiZQEKMu6cer","targetTenantKind":"PA","purposeTitle":"purpose-template-kjrzWieTU5RWx0M","purposeDescription":"purpose-description-Dm2ZZj4QudTF8tvLDg54","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"   ","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 55. BffPurposeTemplateContractTest::createPurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"e246d92f-dfde-45c3-8ea7-d3a2b508d9fe"}
- payload: {"targetDescription":"target-description-XCvk4RTCMBjXyztr5QrO","targetTenantKind":"PA","purposeTitle":"purpose-template-Lc9y4dfponQhjku","purposeDescription":"purpose-description-XIcBen8QuIT5DQuUKK9q","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
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

### 56. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /description

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
- responseBody: {"id":"74baea63-cfa3-4976-aa22-7bdd3ebb85db","kind":"VERIFIED","description":"   ","name":"name-O4DX61jLbfX4I5kE8KFkgzHv","creationTime":"2026-11-25T21:22:43.468Z"}
- payload: {"description":"   ","name":"name-O4DX61jLbfX4I5kE8KFkgzHv"}
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

### 57. BffVerifiedAttributeContractTest::createVerifiedAttribute::[payload] REPLACED_WITH_BLANK_STRING @ /name

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
- responseBody: {"type":"about:blank","title":"Duplicate attribute name","status":409,"detail":"An attribute with name     already exists","correlationId":"cacccf0a-1fa4-4d93-90eb-ddd0e3e37f89","errors":[{"code":"003-0002","detail":"An attribute with name     already exists"}],"userMessage":"Esiste già un attributo con questo nome. Scegli un altro nome."}
- payload: {"description":"description-Yj1uTBWgiyltlkUEKpqFDis6GXxZicNW","name":"   "}
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

### 58. BffPurposeTemplateContractTest::publishPurposeTemplate::[pathParams] REPLACED_WITH_NIL_UUID @ /purposeTemplateId

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

### 59. BffEServiceContractTest::queryEServicesCatalog::[payload] REPLACED_WITH_LONG_STRING @ /keyword

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

### 60. BffPurposeTemplateContractTest::unsuspendPurposeTemplate::[pathParams] REPLACED_WITH_MALFORMED_UUID @ /purposeTemplateId

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

### 61. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isSignalHubEnabled

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8d68c98d-85d0-410b-854c-5a256feb44aa","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isSignalHubEnabled\""}]}
- payload: {"name":"eservice-HOGKH9Y7oSCFzu9","description":"description-qL85P3YmuRo3w6mbGNrV","technology":"REST","mode":"DELIVER","isSignalHubEnabled":null,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"8ed5a6cf-b1a0-4666-acc0-01999056b9ae"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
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
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 11 more

````


---

### 62. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /personalData

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6c0bb5de-5bf7-480a-ba81-d7f2c9c371f9","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"personalData\""}]}
- payload: {"name":"eservice-AZhXHVtkzQrpQF5","description":"description-THvSDeJUIwHIKCYYcj7m","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":null,"asyncExchange":false}
- pathParams: {"eServiceId":"8e9f9d42-fc44-4d4a-a1ac-1e6d5970f116"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
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
Expected status code <200> but was <400>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 11 more

````


---

### 63. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isConsumerDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"a440efd6-b28d-4620-8e83-eb5726f76d1e","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isConsumerDelegable\""}]}
- payload: {"name":"eservice-Kn3lD6sqJUXwcbP","description":"description-bmBZWe6wFsUZG3z2J121","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":null,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"70c68cfc-0282-4b8e-bd4b-1a776b232ea2"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 64. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /isClientAccessDelegable

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"8018fd1f-0d0b-439d-a9f4-ce6cda0dd737","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"isClientAccessDelegable\""}]}
- payload: {"name":"eservice-qSx0NlOafdK9Olw","description":"description-pTKsRTVLuYmNvB7CtNYv","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":null,"personalData":false,"asyncExchange":false}
- pathParams: {"eServiceId":"dcf17bf5-114c-4705-bc58-5b49bd2eb79f"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 65. BffEServiceContractTest::updateEServiceById::[payload] REPLACED_WITH_NULL @ /asyncExchange

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"7734f46a-868b-44f3-be39-292b8ea4daf7","errors":[{"code":"008-9999","detail":"Validation error: Expected boolean, received null at \"asyncExchange\""}]}
- payload: {"name":"eservice-vfaL2MNy5H7c12O","description":"description-tR6GVMtnA8emVe0HuVKA","technology":"REST","mode":"DELIVER","isSignalHubEnabled":false,"isConsumerDelegable":true,"isClientAccessDelegable":true,"personalData":false,"asyncExchange":null}
- pathParams: {"eServiceId":"a6f76d26-58d4-4b11-95f9-5d5ffcaedda2"}
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
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$successStatus$0(DefaultHttpContractPolicy.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 6 more

````


---

### 66. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"5fae53f3-b59e-44b0-b14c-5ac86e2544b2","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-APsVkzkFj5JIcEy","description":"description-zf2dAnk9qLnponh","isFreeOfCharge":true,"freeOfChargeReason":null,"riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"35593016-8b8d-48f9-99ec-7266dcad003e"}
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

### 67. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_NULL @ /riskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"f98bd4ea-8e9d-431e-a365-05c00deb99bd","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"riskAnalysisForm\""}]}
- payload: {"title":"purpose-u3VkxklcfyPXJj7","description":"description-a10fF31VZUUaY5D","isFreeOfCharge":true,"freeOfChargeReason":"reason-zyVQnPfhlzt8Koo","riskAnalysisForm":null,"dailyCalls":1}
- pathParams: {"purposeId":"183ef337-6b7e-421e-8954-9ce2c787efa3"}
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

### 68. BffPurposeContractTest::updatePurpose::[payload] REMOVED @ /riskAnalysisForm/answers/institutionalPurpose

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
- responseBody: {"purposeId":"81e1d794-c193-4ef9-a6c1-8e680a963956","versionId":"4cce7e8f-ad8e-445f-bbe8-e59d7d7a5207"}
- payload: {"title":"purpose-v9i0SFQCrp5kl2s","description":"description-wdZPdZjiNYpPULn","isFreeOfCharge":true,"freeOfChargeReason":"reason-rFEOASay9pDn88I","riskAnalysisForm":{"version":"3.2","answers":{"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"81e1d794-c193-4ef9-a6c1-8e680a963956"}
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

### 69. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"purposeId":"6938a2dd-74cb-4c23-8b66-d359356a5779","versionId":"42bf9ddb-5609-4ac6-833f-1335e1716fa5"}
- payload: {"title":"purpose-S8rkA3b2XlQ8dgz","description":"description-uMFs5gcntYQPNHM","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["prova prova prova"],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"6938a2dd-74cb-4c23-8b66-d359356a5779"}
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

### 70. BffPurposeContractTest::updatePurpose::[payload] REPLACED_WITH_BLANK_STRING @ /riskAnalysisForm/answers/institutionalPurpose/0

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
- responseBody: {"purposeId":"f3141a4a-b341-4162-95d8-f0baab8b5a6d","versionId":"4d2f48ed-11f7-4a97-ba83-33e3b014ec1e"}
- payload: {"title":"purpose-AsCNSYt2RAAoeOX","description":"description-OXV7Hc0Zm0swhG4","isFreeOfCharge":true,"freeOfChargeReason":"reason-DiWyyiOU46tLEN8","riskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":["   "],"isRequestOnBehalfOfThirdParties":["NO"],"purpose":["INSTITUTIONAL"],"usesPersonalData":["NO"],"usesThirdPartyPersonalData":["NO"]}},"dailyCalls":1}
- pathParams: {"purposeId":"f3141a4a-b341-4162-95d8-f0baab8b5a6d"}
	at it.pagopa.infrastructure.contract.http.HttpContractFailureDiagnostics.enrich(HttpContractFailureDiagnostics.java:22)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:61)
	at it.pagopa.infrastructure.contract.http.HttpContractInvocationBuilder.lambda$toDynamicTest$0(HttpContractInvocationBuilder.java:94)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool.helpJoin(ForkJoinPool.java:1883)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:440)
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
Expected status code <400> but was <200>.

	at org.codehaus.groovy.vmplugin.v8.IndyInterface.fromCache(IndyInterface.java:344)
	at io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:516)
	at io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:714)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:139)
	at io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:147)
	at io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:89)
	at it.pagopa.infrastructure.contract.http.DefaultHttpContractPolicy$BuilderImpl.lambda$scenarioStatus$2(DefaultHttpContractPolicy.java:94)
	at it.pagopa.infrastructure.contract.http.HttpContractRuntimeCaseExecutor.execute(HttpContractRuntimeCaseExecutor.java:59)
	... 13 more

````


---

### 71. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeRiskAnalysisForm

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"3f58c6b6-6a3a-40d5-9663-814929766c6e","errors":[{"code":"008-9999","detail":"Validation error: Expected object, received null at \"purposeRiskAnalysisForm\""}]}
- payload: {"targetDescription":"target-description-SA2HIfYkyTIfQyUqaosN","targetTenantKind":"PA","purposeTitle":"purpose-template-SSfHn1yFKyPFREJ","purposeDescription":"purpose-description-MZGrcPLBDwvGj7Mjy0ex","purposeRiskAnalysisForm":null,"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-89bVouaboa3gTLCNSjtL","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"5ce78def-048e-4eaf-874f-041e965d428b"}
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

### 72. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/institutionalPurpose/annotation

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
- responseBody: {"id":"e4aa8db5-1531-484c-9407-585183d3f864","targetDescription":"target-description-yzR4WvGKoBhwLYHoYsHo","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:26:27.958Z","updatedAt":"2026-11-25T21:26:30.558Z","purposeTitle":"purpose-template-JEnuwnhTMN7suzf","purposeDescription":"purpose-description-334etbWENoTna5oBxxId","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"c23fd3df-7714-43c4-abdd-d23bb3106075","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"3edf02a7-87dd-4abe-a6b0-cc1c2953ebe0","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"95d6d60a-b63c-4d03-b688-a2f0d7afe400","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"d33e7096-ff41-4d45-ad70-2caf3b7f8b33","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"60a1e8f9-5053-460f-bf1b-8c6e2e5a0923","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-e5GSCc2YXFvMbwOOMIGa","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-yzR4WvGKoBhwLYHoYsHo","targetTenantKind":"PA","purposeTitle":"purpose-template-JEnuwnhTMN7suzf","purposeDescription":"purpose-description-334etbWENoTna5oBxxId","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-e5GSCc2YXFvMbwOOMIGa","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"e4aa8db5-1531-484c-9407-585183d3f864"}
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

### 73. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/isRequestOnBehalfOfThirdParties/annotation

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
- responseBody: {"id":"f1178d31-1ad4-40fe-ad79-06feeed6cbeb","targetDescription":"target-description-o1su2bgWqby2c1dV4AzA","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:27:16.731Z","updatedAt":"2026-11-25T21:27:19.323Z","purposeTitle":"purpose-template-NVrNR9qr0IZNsfq","purposeDescription":"purpose-description-8kqrQzCUlpQQgP2rieGf","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"a0ab504e-d64f-43c6-8187-2adce753372b","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"a8c3d55b-0465-406c-8192-a274311bdd7f","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"4c4373c1-b4b3-4215-bdba-49e87e6788a9","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"ff697190-55c4-4e8d-915f-52446fcca2b6","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"851ac488-4188-41c4-9b53-6d1910a1a0cd","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-489wXo9Ol1LJTFTyPtHK","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-o1su2bgWqby2c1dV4AzA","targetTenantKind":"PA","purposeTitle":"purpose-template-NVrNR9qr0IZNsfq","purposeDescription":"purpose-description-8kqrQzCUlpQQgP2rieGf","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-489wXo9Ol1LJTFTyPtHK","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"f1178d31-1ad4-40fe-ad79-06feeed6cbeb"}
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

### 74. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/purpose/annotation

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
- responseBody: {"id":"40ddd570-b50a-4798-8449-0fab6a14f1e6","targetDescription":"target-description-K8xUvhpX6iOycUydvlVK","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:28:02.832Z","updatedAt":"2026-11-25T21:28:05.447Z","purposeTitle":"purpose-template-c3l9dKduPf62kb0","purposeDescription":"purpose-description-SS2Yn6NBcgm7EmMJWjeo","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"39834373-34df-448e-bc5f-4184df35d739","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"bd34fb8c-d0d7-4ee6-814a-b2a22541a8c1","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"d5f67f52-9d3f-4dd3-aec6-568badc79816","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"bbb4876a-02d8-4f33-a281-5b0db7b12150","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"a6db87ff-93dd-442b-b4f6-2b3d39b4be46","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-yGebYfM1n4UGIUsTC6pu","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-K8xUvhpX6iOycUydvlVK","targetTenantKind":"PA","purposeTitle":"purpose-template-c3l9dKduPf62kb0","purposeDescription":"purpose-description-SS2Yn6NBcgm7EmMJWjeo","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-yGebYfM1n4UGIUsTC6pu","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"40ddd570-b50a-4798-8449-0fab6a14f1e6"}
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

### 75. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesPersonalData/annotation

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
- responseBody: {"id":"d1ea6652-65f7-434f-b1fb-8b0a06dd1501","targetDescription":"target-description-HHfHdlumaTENeBCN9lg1","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:28:50.363Z","updatedAt":"2026-11-25T21:28:51.410Z","purposeTitle":"purpose-template-edeWglkqaCLQPct","purposeDescription":"purpose-description-yWj0J2nLtZ54lyoU5qT5","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"3d887910-04c5-4bc8-a1da-0e9d3a680d96","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"d7001aa4-8bf6-4b7a-99f8-69b1958d50a1","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"6652e184-23a9-4ad9-9643-5b5afb4964b8","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"6dc33011-5e84-4b7c-b4c6-f900185ac978","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"b47d3a12-411b-48ef-8cb0-a57b10d35a3c","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-rd9meBtgfjyDjNZk5AJX","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-HHfHdlumaTENeBCN9lg1","targetTenantKind":"PA","purposeTitle":"purpose-template-edeWglkqaCLQPct","purposeDescription":"purpose-description-yWj0J2nLtZ54lyoU5qT5","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-rd9meBtgfjyDjNZk5AJX","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"d1ea6652-65f7-434f-b1fb-8b0a06dd1501"}
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

### 76. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REMOVED @ /purposeRiskAnalysisForm/answers/usesThirdPartyPersonalData/annotation

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
- responseBody: {"id":"a47f428d-e038-429b-aadf-e95e6c506a01","targetDescription":"target-description-1XMA4Z1p9QST2vrPpg7D","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:29:33.269Z","updatedAt":"2026-11-25T21:29:35.844Z","purposeTitle":"purpose-template-7o4nMn2xvvl6wYB","purposeDescription":"purpose-description-wfOyFvH7xWj7q4FeZkrR","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"8fa76e04-591b-44d8-9b86-0572e692077d","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"f3248c9c-b654-4ee6-9dfc-d562a93cb3bf","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"e2e08cc0-6a85-44c6-90ed-491838eca64b","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"96a2e9c1-1109-4b2f-9c45-da5028d0b29c","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"386ee806-3d66-4ad1-a14d-75fea7a9df21","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-jki1HuJ84VVUnU8tlxX1","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-1XMA4Z1p9QST2vrPpg7D","targetTenantKind":"PA","purposeTitle":"purpose-template-7o4nMn2xvvl6wYB","purposeDescription":"purpose-description-wfOyFvH7xWj7q4FeZkrR","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-jki1HuJ84VVUnU8tlxX1","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"a47f428d-e038-429b-aadf-e95e6c506a01"}
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

### 77. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeFreeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"c48480a0-bb79-4d51-9afe-28368863e591","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"purposeFreeOfChargeReason\""}]}
- payload: {"targetDescription":"target-description-ALrDXWGZ8B81f6jgpKQU","targetTenantKind":"PA","purposeTitle":"purpose-template-hyCf6vqWBzh6xXb","purposeDescription":"purpose-description-lyp43OzgtKuWvV17i896","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":null,"purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"a4f4c0e7-3d09-4bb4-8c48-a060b7bc80d9"}
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

### 78. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_NULL @ /purposeDailyCalls

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6a3e9179-8a9c-413a-a4c5-a13debfb1609","errors":[{"code":"008-9999","detail":"Validation error: Expected number, received null at \"purposeDailyCalls\""}]}
- payload: {"targetDescription":"target-description-1ro2K00C1wJBrf3Pb5Y7","targetTenantKind":"PA","purposeTitle":"purpose-template-RxegJY8meuuRTrx","purposeDescription":"purpose-description-jBygltF1WDjRpbeQ6YZZ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-k9R7ddX8BImJnGE3OGzi","purposeDailyCalls":null,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"ea325d58-2b8a-497f-970d-119fe6b2ffce"}
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

### 79. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_BLANK_STRING @ /purposeRiskAnalysisForm/answers/institutionalPurpose/suggestedValues/0

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
- responseBody: {"id":"cb691e80-c17c-4ad6-86f0-4a82045af42f","targetDescription":"target-description-wRZzYvKfs4njInNQCUgp","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:31:11.100Z","updatedAt":"2026-11-25T21:31:12.175Z","purposeTitle":"purpose-template-0cjiMhiJIskroaI","purposeDescription":"purpose-description-dbY1hsKMvHCv1Rf5bt52","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"df5acda4-9bf1-48ed-b7eb-87b22d5c4b51","values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"id":"53cb40a9-aee5-411a-b27e-0e62b56afd14","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"2c004680-c04d-456a-b864-757b623bf12b","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"8715c45c-162a-42ca-842c-73681a2da2d3","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"2bb915fb-53e0-4017-8dd3-f897ea8524d5","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-Mmsls0XukpqtL0HUUYq7","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-wRZzYvKfs4njInNQCUgp","targetTenantKind":"PA","purposeTitle":"purpose-template-0cjiMhiJIskroaI","purposeDescription":"purpose-description-dbY1hsKMvHCv1Rf5bt52","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["   "]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"free-of-charge-reason-Mmsls0XukpqtL0HUUYq7","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"cb691e80-c17c-4ad6-86f0-4a82045af42f"}
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

### 80. BffPurposeTemplateContractTest::updatePurposeTemplate::[payload] REPLACED_WITH_LONG_STRING @ /purposeFreeOfChargeReason

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
- responseBody: {"id":"3338ac75-bd54-487c-b75d-8645c0dae01b","targetDescription":"target-description-xKmvDD3a4z4jhywGsHZF","targetTenantKind":"PA","creatorId":"e79a24cd-8edc-441e-ae8d-e87c3aea0059","state":"DRAFT","createdAt":"2026-11-25T21:32:30.248Z","updatedAt":"2026-11-25T21:32:32.832Z","purposeTitle":"purpose-template-DLQG6wuFfNoURGE","purposeDescription":"purpose-description-IYTa37eolnb5EWiUITdQ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"id":"c8c6da7a-5ce3-4ed4-881e-a9bceb4a064c","values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"id":"77d255c8-47f0-40cd-8e4a-50a1c1213e52","values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"id":"7c87455f-507b-4042-b9e6-2145d5b292f8","values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"id":"26476de0-be66-4505-9811-4bc6f6bd1d24","values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"id":"90eb55cb-768d-478e-ab8d-6c47f99a92fa","values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- payload: {"targetDescription":"target-description-xKmvDD3a4z4jhywGsHZF","targetTenantKind":"PA","purposeTitle":"purpose-template-DLQG6wuFfNoURGE","purposeDescription":"purpose-description-IYTa37eolnb5EWiUITdQ","purposeRiskAnalysisForm":{"version":"3.2","answers":{"institutionalPurpose":{"values":[],"editable":false,"suggestedValues":["prova prova prova"]},"isRequestOnBehalfOfThirdParties":{"values":["NO"],"editable":false,"suggestedValues":[]},"purpose":{"values":["INSTITUTIONAL"],"editable":false,"suggestedValues":[]},"usesPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]},"usesThirdPartyPersonalData":{"values":["NO"],"editable":false,"suggestedValues":[]}}},"purposeIsFreeOfCharge":true,"purposeFreeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","purposeDailyCalls":1,"handlesPersonalData":false}
- pathParams: {"purposeTemplateId":"3338ac75-bd54-487c-b75d-8645c0dae01b"}
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

### 81. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_NULL @ /freeOfChargeReason

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
- responseBody: {"type":"about:blank","title":"Bad request","status":400,"detail":"Incorrect value for body","correlationId":"6a79e9b7-74cc-4165-81a2-960991c92ec7","errors":[{"code":"008-9999","detail":"Validation error: Expected string, received null at \"freeOfChargeReason\""}]}
- payload: {"title":"purpose-egzCxFoa4YwRxGs","description":"description-MCCtpCSX6c3YMmL","isFreeOfCharge":true,"freeOfChargeReason":null,"dailyCalls":1}
- pathParams: {"purposeId":"6d007bcb-4786-4ed5-89bd-d8ec83705a2f"}
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

### 82. BffPurposeContractTest::updateReversePurpose::[payload] REPLACED_WITH_LONG_STRING @ /freeOfChargeReason

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
- responseBody: {"purposeId":"0a50a622-1dff-46ed-8baa-c7d09aaf90ca","versionId":"abf88d9b-214c-4e22-bce0-5acb5b2da702"}
- payload: {"title":"purpose-WfYfzWpH88Ycvof","description":"description-lQJe5zuLvYPQOz5","isFreeOfCharge":true,"freeOfChargeReason":"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","dailyCalls":1}
- pathParams: {"purposeId":"0a50a622-1dff-46ed-8baa-c7d09aaf90ca"}
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

### 83. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /eserviceId

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
- responseBody: {"type":"about:blank","title":"EService not found","status":400,"detail":"EService 00000000-0000-0000-0000-000000000000 not found","correlationId":"d8a86f18-de4c-4fa1-b41d-df0fe822a484","errors":[{"code":"002-0005","detail":"EService 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"00000000-0000-0000-0000-000000000000","descriptorId":"4ee71fb5-ec98-403e-aeba-f3a54e434155"}
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

### 84. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /descriptorId

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
- responseBody: {"type":"about:blank","title":"Descriptor provided is not the latest descriptor","status":400,"detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor","correlationId":"9e26396d-3177-4ec5-bf87-c9d75a055714","errors":[{"code":"002-0021","detail":"Descriptor with descriptorId: 00000000-0000-0000-0000-000000000000 is not the latest descriptor"}]}
- payload: {"eserviceId":"9549bd08-4583-4074-99b5-af29ae77bfb6","descriptorId":"00000000-0000-0000-0000-000000000000"}
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

### 85. M2Mv3AgreementContractTest::createAgreement::[payload] REPLACED_WITH_NIL_UUID @ /delegationId

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
- responseBody: {"type":"about:blank","title":"Delegation not found","status":400,"detail":"Delegation 00000000-0000-0000-0000-000000000000 not found","correlationId":"c9f75af3-5858-43c3-8ba7-f03a8056de3c","errors":[{"code":"002-0026","detail":"Delegation 00000000-0000-0000-0000-000000000000 not found"}]}
- payload: {"eserviceId":"6b379acc-cffa-429f-a0f8-8627bcb2f063","descriptorId":"ac485f30-f427-432c-b031-9ae555528917","delegationId":"00000000-0000-0000-0000-000000000000"}
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

### 86. WebConsumerPurposeDetailsPageContractTest::consumerPurposeDetailsPageContract::Consumer purpose details page shows the expected content and filters

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
expected: "description-Hh1ShiMJFlY1lNd"
 but was: null
at WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$1(WebConsumerPurposeDetailsPageContractTest.java:81)
	at it.pagopa.interop.suite.contract.WebConsumerPurposeDetailsPageContractTest.lambda$contractScenarios$2(WebConsumerPurposeDetailsPageContractTest.java:71)

````


---

### 87. WebPurposeTemplateContractTest::createPurposeTemplate::creating purpose template with personal data treatment disabled

**RESULT**

Nella NRT il test non ha individuato titolo, descrizione e switch della gratuità. La prima verifica dello stato dello switch è terminata con NoSuchElementException, prima del click previsto per disattivarlo.

In una precedente NRT, dopo la conferma il test leggeva ancora il titolo del catalogo anziché raggiungere il form. Per il KO attuale, però, il log non riporta il titolo o l'URL effettivi: non è possibile confermare che sia rimasto sul catalogo anche in questa esecuzione.

L'analisi ha evidenziato che il controllo di caricamento accetta un h1 non vuoto, senza verificare che appartenesse alla pagina attesa. Inoltre, il raggiungimento del form dipende dal flusso di creazione via WEB, introducendo passaggi estranei alle verifiche dei componenti.

Queste osservazioni da sole non portano automaticamente ad una causa del KO, ma suggeriscono che il test non sia sufficientemente robusto.

**PROCEDURE**

Analizzare il log NRT dello scenario con trattamento dati personali disabilitato, ricostruendo la navigazione e le verifiche successive alla conferma della creazione.

Confrontare il KO con le esecuzioni precedenti e con la riesecuzione locale. Esaminare i controlli di caricamento della pagina, lo stato iniziale dello switch e la verifica della presenza del campo motivazione.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19830

**ERROR LOG**

````text
java.util.NoSuchElementException: Impossibile verificare lo stato checked: l'elemento UI non è presente nella pagina.
	at it.pagopa.infrastructure.suit.component.Switch.lambda$isChecked$1(Switch.java:22)
	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
	at it.pagopa.infrastructure.suit.component.Switch.isChecked(Switch.java:22)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy310.isChecked(Unknown Source)
	at it.pagopa.interop.suite.contract.WebPurposeTemplateContractTest.lambda$assertLandedOnCreatedTemplate$4(WebPurposeTemplateContractTest.java:84)
	at org.assertj.core.api.SoftAssertionsProvider.assertSoftly(SoftAssertionsProvider.java:122)
	at org.assertj.core.api.SoftAssertions.assertSoftly(SoftAssertions.java:165)
	at it.pagopa.interop.suite.contract.WebPurposeTemplateContractTest.assertLandedOnCreatedTemplate(WebPurposeTemplateContractTest.java:74)

````


---

### 88. WebEServiceThresholdContractTest::shouldValidateThresholdFields::soglia per fruitore con valore decimale (virgola)

**RESULT**

Il test avvia la navigazione verso la pagina di creazione dell'e-service, ma fallisce nel controllo iniziale del titolo h1: il valore atteso è "Crea e-service", mentre il testo letto risulta vuoto.

Il test termina prima della compilazione dei dati e dell'inserimento della soglia. Il KO non permette quindi di valutare la gestione del decimale con virgola. Anche gli altri tre scenari sulle soglie falliscono nello stesso punto e con lo stesso errore.

L'attuale assertLoaded() confronta immediatamente il titolo letto con quello atteso. Un caricamento non ancora completato è una possibile causa, ma non è dimostrata dai log. È stata individuata un'attività di miglioramento della sincronizzazione del controllo; la causa del KO resta da confermare ma è ipotizzabile come un rallentamento temporaneo del caricamento della pagina dovuto all'esecuzione parallela dei test.

**PROCEDURE**

Analizzare il log dello scenario e confrontare lo stack trace con EServiceCreatePage.assertLoaded() e con la sequenza del test.

Verificare se il fallimento avviene prima o dopo l'inserimento della soglia e confrontare l'esito con gli altri scenari della stessa factory.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19831

**ERROR LOG**

````text
java.lang.AssertionError: Expected Crea e-service found 
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy306.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage.assertLoaded(EServiceCreatePage.java:44)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy351.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:45)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

````


---

### 89. WebEServiceThresholdContractTest::shouldValidateThresholdFields::soglia totale con valore decimale (virgola)

**RESULT**

Il test avvia la navigazione verso la pagina di creazione dell'e-service, ma fallisce nel controllo iniziale del titolo h1: il valore atteso è "Crea e-service", mentre il testo letto risulta vuoto.

Il test termina prima della compilazione dei dati e dell'inserimento della soglia. Il KO non permette quindi di valutare la gestione del decimale con virgola. Anche gli altri tre scenari sulle soglie falliscono nello stesso punto e con lo stesso errore.

L'attuale assertLoaded() confronta immediatamente il titolo letto con quello atteso. Un caricamento non ancora completato è una possibile causa, ma non è dimostrata dai log. È stata individuata un'attività di miglioramento della sincronizzazione del controllo; la causa del KO resta da confermare ma è ipotizzabile come un rallentamento temporaneo del caricamento della pagina dovuto all'esecuzione parallela dei test.

**PROCEDURE**

Analizzare il log dello scenario e confrontare lo stack trace con EServiceCreatePage.assertLoaded() e con la sequenza del test.

Verificare se il fallimento avviene prima o dopo l'inserimento della soglia e confrontare l'esito con gli altri scenari della stessa factory.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19831

**ERROR LOG**

````text
java.lang.AssertionError: Expected Crea e-service found 
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy306.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage.assertLoaded(EServiceCreatePage.java:44)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy351.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:45)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

````


---

### 90. WebEServiceThresholdContractTest::shouldValidateThresholdFields::soglia per fruitore con valore decimale (punto)

**RESULT**

Il test avvia la navigazione verso la pagina di creazione dell'e-service, ma fallisce nel controllo iniziale del titolo h1: il valore atteso è "Crea e-service", mentre il testo letto risulta vuoto.

Il test termina prima della compilazione dei dati e dell'inserimento della soglia. Il KO non permette quindi di valutare la gestione del decimale con virgola. Anche gli altri tre scenari sulle soglie falliscono nello stesso punto e con lo stesso errore.

L'attuale assertLoaded() confronta immediatamente il titolo letto con quello atteso. Un caricamento non ancora completato è una possibile causa, ma non è dimostrata dai log. È stata individuata un'attività di miglioramento della sincronizzazione del controllo; la causa del KO resta da confermare ma è ipotizzabile come un rallentamento temporaneo del caricamento della pagina dovuto all'esecuzione parallela dei test.

**PROCEDURE**

Analizzare il log dello scenario e confrontare lo stack trace con EServiceCreatePage.assertLoaded() e con la sequenza del test.

Verificare se il fallimento avviene prima o dopo l'inserimento della soglia e confrontare l'esito con gli altri scenari della stessa factory.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19831

**ERROR LOG**

````text
java.lang.AssertionError: Expected Crea e-service found 
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy306.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage.assertLoaded(EServiceCreatePage.java:44)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy351.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:45)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

````


---

### 91. WebEServiceThresholdContractTest::shouldValidateThresholdFields::soglia totale con valore decimale (punto)

**RESULT**

Il test avvia la navigazione verso la pagina di creazione dell'e-service, ma fallisce nel controllo iniziale del titolo h1: il valore atteso è "Crea e-service", mentre il testo letto risulta vuoto.

Il test termina prima della compilazione dei dati e dell'inserimento della soglia. Il KO non permette quindi di valutare la gestione del decimale con virgola. Anche gli altri tre scenari sulle soglie falliscono nello stesso punto e con lo stesso errore.

L'attuale assertLoaded() confronta immediatamente il titolo letto con quello atteso. Un caricamento non ancora completato è una possibile causa, ma non è dimostrata dai log. È stata individuata un'attività di miglioramento della sincronizzazione del controllo; la causa del KO resta da confermare ma è ipotizzabile come un rallentamento temporaneo del caricamento della pagina dovuto all'esecuzione parallela dei test.

**PROCEDURE**

Analizzare il log dello scenario e confrontare lo stack trace con EServiceCreatePage.assertLoaded() e con la sequenza del test.

Verificare se il fallimento avviene prima o dopo l'inserimento della soglia e confrontare l'esito con gli altri scenari della stessa factory.

**LINK SUITE BUG:** https://pagopa.atlassian.net/browse/QA-19831

**ERROR LOG**

````text
java.lang.AssertionError: Expected Crea e-service found 
	at it.frontend.e2e.framework.web.capability.impl.ReadableImpl.readAndAssert(ReadableImpl.java:35)
	at java.base/java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:732)
	at it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler.handle(AbstractCapabilityHandler.java:73)
	at it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher.dispatch(DefaultCapabilityDispatcher.java:33)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.resolveCapabilityMethod(DefaultBinderInvocationHandler.java:82)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:62)
	at jdk.proxy2/jdk.proxy2.$Proxy306.readAndAssert(Unknown Source)
	at it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage.assertLoaded(EServiceCreatePage.java:44)
	at java.base/java.lang.reflect.Proxy.invokeDefault(Proxy.java:1348)
	at java.base/java.lang.reflect.InvocationHandler.invokeDefault(InvocationHandler.java:264)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.handleDefaultMethod(DefaultBinderInvocationHandler.java:73)
	at it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler.invoke(DefaultBinderInvocationHandler.java:55)
	at jdk.proxy2/jdk.proxy2.$Proxy351.assertLoaded(Unknown Source)
	at it.pagopa.infrastructure.contract.browser.WebContractRuntimeCaseExecutor.execute(WebContractRuntimeCaseExecutor.java:45)
	at it.pagopa.infrastructure.contract.browser.WebContractInvocationBuilder$PageStageImpl.lambda$tests$0(WebContractInvocationBuilder.java:62)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinTask.awaitDone(ForkJoinTask.java:436)
	at java.base/java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:979)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

````

</details>
