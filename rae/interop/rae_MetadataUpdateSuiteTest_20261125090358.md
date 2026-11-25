# 20261125 Report Analisi Esiti (RAE) MetadataUpdateSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release | [INTEROP]-2.25.0-RC1 |
| Scope | PROMO |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19770 |
| Data e ora | 2026-11-25T08:52:41Z |
| Durata | 00:10:40 |
| Numero KO | 1 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/45579976598/attempts/1 |
| Suite | MetadataUpdateSuiteTest |
| Ambiente | qa |
| Executor | manu71 |
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

## 1 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 1 KO</summary>

### 1. [CLIENT_UPDATE_METADATA_2B] - L'amministratore aggiorna i metadati del client interop ma la modifica risulta vietata [Given=BFF,When=M2M,Then=M2M]

**RESULT**

La richiesta viene rifiutata con HTTP 404, ma il codice di errore atteso per questo scenario è 403.

**PROCEDURE**

Eseguire nello scenario CLIENT_UPDATE_METADATA_2B la PATCH /v3/clients/{clientId}. Confrontare lo status atteso con la risposta e leggere codice e dettaglio dell'eventuale errore.

**LINK PROD BUG:** https://pagopa.atlassian.net/browse/PIN-11643

**ERROR LOG**

````text
org.opentest4j.AssertionFailedError: [Modifica dei metadati del client vietata] 
expected: 403
 but was: 404
	at it.pagopa.interop.m2m.client.infrastructure.M2MClientGateway.shouldFailToUpdateMetadataAsForbidden(M2MClientGateway.java:103)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at it.pagopa.infrastructure.channel.ChannelRouter.invoke(ChannelRouter.java:43)
	at it.pagopa.interop.common.infrastructure.ChannelRoutingInterceptor.invoke(ChannelRoutingInterceptor.java:40)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)
	at jdk.proxy2/jdk.proxy2.$Proxy153.shouldFailToUpdateMetadataAsForbidden(Unknown Source)
	at it.pagopa.interop.common.client.application.ClientUseCase.shouldFailToUpdateMetadataAsForbidden(ClientUseCase.java:75)
	at it.pagopa.interop.common.client.infrastructure.cucumber.ClientSteps.shouldFailToUpdateMetadataAsForbidden(ClientSteps.java:168)
	at ✽.la richiesta di modifica fallisce come operazione vietata(classpath:feature-expanded/client/client-update-metadata.feature:115)

````

</details>
