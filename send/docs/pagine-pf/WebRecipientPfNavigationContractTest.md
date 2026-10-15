# Navigazione del portale persone fisiche

`WebRecipientPfNavigationContractTest` apre ogni pagina del portale cittadini di SEND e controlla che il suo
`assertLoaded()` passi, cioè che la pagina sia caricata. Testi, contenuto e validazioni di ogni pagina li controllano i
contract test di pagina, elencati nella tabella.

`{baseUrl}` è la proprietà `url.notifiche.cittadino.base` del profilo attivo, ad esempio
`https://cittadini.test.notifichedigitali.it` sul profilo `test`. Utente: Lucrezia Borgia.

## Pagine

| Pagina | Indirizzo | Test di navigazione | Page object | `assertLoaded()` controlla | Contract test |
|---|---|---|---|---|---|
| In arrivo | `/notifiche` | `shouldReachNotificationListPF` | [`NotificationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NotificationPFPage.java) | titolo e campi dei filtri | [`WebNotificationPFContractTest`](WebNotificationPFContractTest.md) |
| I tuoi recapiti | `/recapiti` | `shouldReachAddressPF` | [`AddressPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/AddressPFPage.java) | titolo e card del domicilio digitale, dell'app IO e dell'email | [`WebAddressPFContractTest`](WebAddressPFContractTest.md) |
| Attiva domicilio digitale su SEND | `/recapiti/domicilio-digitale/attivazione` | `shouldReachDigitalDomicileActivationPF` | [`DigitalDomicileActivationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DigitalDomicileActivationPFPage.java) | titolo del wizard, i tre passi, "Continua" e "Annulla" | [`WebDigitalDomicileActivationPFContractTest`](WebDigitalDomicileActivationPFContractTest.md) |
| Deleghe | `/deleghe` | `shouldReachDelegationsPF` | [`DelegationsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DelegationsPFPage.java) | titolo, "Aggiungi una delega" e le due sezioni | [`WebDelegationsPFContractTest`](WebDelegationsPFContractTest.md) |
| Aggiungi una delega | `/deleghe/nuova` | `shouldReachNewDelegationPF` | [`NewDelegationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NewDelegationPFPage.java) | titolo, campi e pulsanti del form | [`WebNewDelegationPFContractTest`](WebNewDelegationPFContractTest.md) |
| Configura SEND | `/onboarding` | `shouldReachOnboardingPF` | [`ConfigureAddressSendPage`](../../src/main/java/it/pagopa/send/web/infrastructure/page/ConfigureAddressSendPage.java) | titolo, pulsanti delle due card mostrate a tutti e "Salta e vai alle tue notifiche" | [`WebConfigureAddressSendPFContractTest`](WebConfigureAddressSendPFContractTest.md) |
| Il meglio di SEND | `/onboarding/domicilio-digitale` | `shouldReachOnboardingDigitalDomicilePF` | [`OnboardingDigitalDomicilePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingDigitalDomicilePFPage.java) | titolo, "Esci" e nomi dei quattro passi | nessuno, vedi sotto |
| Attivazione avvisi | `/onboarding/avvisi` | `shouldReachOnboardingAlertsPF` | [`OnboardingAlertsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingAlertsPFPage.java) | che la pagina sia caricata; ogni passo, di avere un contenuto | [`WebOnboardingAlertsPFContractTest`](WebOnboardingAlertsPFContractTest.md) |
| Tutto, sull'app IO | `/onboarding/io` | `shouldReachOnboardingIoPF` | [`OnboardingIoPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingIoPFPage.java) | titolo, "Esci", "Scarica l'app IO" e "Ho già scaricato e installato l'app" | [`WebOnboardingIoPFContractTest`](WebOnboardingIoPFContractTest.md) |
| Stato della piattaforma | `/app-status` | `shouldReachAppStatusPF` | [`AppStatusPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/AppStatusPFPage.java) | titolo, stato attuale e ultimo aggiornamento | [`WebAppStatusPFContractTest`](WebAppStatusPFContractTest.md) |
| Assistenza | `/assistenza` | `shouldReachSupportPF` | [`SupportPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/SupportPFPage.java) | titolo, campi e pulsanti del form | [`WebSupportPFContractTest`](WebSupportPFContractTest.md) |
| I tuoi dati | `/profilo` | `shouldReachProfilePF` | [`ProfilePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/ProfilePFPage.java) | titolo e le tre righe dei dati | [`WebProfilePFContractTest`](WebProfilePFContractTest.md) |
| Termini di servizio | `/termini-di-servizio` | `shouldReachTermsOfServicePF` | [`TermsOfServicePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/TermsOfServicePFPage.java) | titolo e almeno una sezione | [`WebTermsOfServicePFContractTest`](WebTermsOfServicePFContractTest.md) |
| Termini di servizio SERCQ | `/termini-di-servizio/sercq-send` | `shouldReachSercqTermsOfServicePF` | [`SercqTermsOfServicePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/SercqTermsOfServicePFPage.java) | titolo e almeno una sezione | [`WebSercqTermsOfServicePFContractTest`](WebSercqTermsOfServicePFContractTest.md) |

