# 20261126 Report Analisi Esiti (RAE) EServiceSoapInterfaceSuiteTest QA

| Campo | Valore |
| --- | --- |
| Release | [INTEROP]-2.25-RC1 |
| Scope | PROMO |
| Issue Jira | https://pagopa.atlassian.net/browse/QA-19832 |
| Data e ora | 2026-11-26T20:40:27Z |
| Durata | 00:02:37 |
| Numero KO | 4 |
| Run GitHub Actions | https://github.com/pagopa/qe_suit/actions/runs/45892603401/attempts/1 |
| Suite | EServiceSoapInterfaceSuiteTest |
| Ambiente | qa |
| Executor | giuseppe-veldorale |
| Source branch | feature/PIN-10024-runner |

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

## 4 - KO DA ANALIZZARE

<details>
<summary>Mostra/nascondi 4 KO</summary>

### 1. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #1]

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
	at jdk.proxy2/jdk.proxy2.$Proxy160.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 2. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #2]

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
	at jdk.proxy2/jdk.proxy2.$Proxy160.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 3. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #3]

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
	at jdk.proxy2/jdk.proxy2.$Proxy160.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````


---

### 4. [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP [Given=BFF,When=BFF,Then=BFF] [Examples: markup-e-virgolette | Example #4]

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
	at jdk.proxy2/jdk.proxy2.$Proxy160.shouldFailToConfigureSOAPInterface(Unknown Source)
	at it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase.shouldFailToConfigureSOAPInterface(EServiceDescriptorUseCase.java:173)
	at it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps.rejectSoapInterface(EServiceTemplateInstanceSteps.java:73)
	at ✽.l'erogatore tenta di configurare l'interfaccia SOAP del descrittore dell'e-service appena istanziato ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:(classpath:feature-expanded/eservice-template/eservice-template-instance-interface.feature:140)

````

</details>
