package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationDetailsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationTimelinePFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Contract test del dettaglio di una notifica del cittadino ({@code {baseUrl}/notifiche/<IUN>/dettaglio} per le
 * notifiche a valore legale, {@code {baseUrl}/comunicazione/<IUN>/dettaglio} per le comunicazioni) e della pagina
 * "Stato della notifica" ({@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>dettaglio di una notifica a valore legale;</li>
 *     <li>dettaglio di una comunicazione;</li>
 *     <li>timeline della notifica a valore legale.</li>
 * </ul>
 * Ogni scenario parte da "In arrivo", filtra per tipologia e apre la prima notifica già letta, cioè senza il pallino di
 * notifica nuova: aprire una notifica a valore legale non letta la segnerebbe come letta, con valore di presa visione.
 * Se non c'è una notifica già letta della tipologia lo scenario termina senza verificarla. Nessuno scenario scarica
 * documenti o preme "Paga".
 */
@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationDetailsPFContractTest {

    // testi attesi

    private static final String NOTIFICATIONS = "In arrivo";
    private static final String IUN_LABEL = "Codice IUN";
    private static final String IUN_PATTERN = "[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-[A-Z0-9]";
    private static final String DOCUMENTS_TITLE = "Documenti allegati";

    // notifica a valore legale
    private static final String LEGAL_DEPOSIT_PATTERN = "Notifica depositata il giorno \\d{2}/\\d{2}/\\d{4}";
    private static final String LEGAL_DOCUMENTS_MESSAGE = "I documenti saranno disponibili online fino a 120 giorni dalla data in cui la notifica SEND ha assunto valore di legge. Salvali sul tuo dispositivo!";
    private static final String STATUS_TITLE = "Stato della notifica";
    private static final String TIMELINE_DETAILS = "Vai al dettaglio";
    private static final String AAR_TITLE = "Dettaglio della notifica";
    private static final String AAR_LABEL = "Avviso di avvenuta ricezione";
    private static final String DOWNTIMES_TITLE = "Disservizi";

    // comunicazione
    private static final String COMMUNICATION_DEPOSIT_PATTERN = "Comunicazione depositata il giorno \\d{2}/\\d{2}/\\d{4}";
    private static final String COMMUNICATION_DOCUMENTS_MESSAGE = "Conserva gli allegati sul tuo dispositivo: saranno disponibili online fino a 180 giorni dalla data di deposito che trovi in alto!";
    private static final String LEGAL_EFFECTS_NOTE = "Questa comunicazione potrebbe produrre effetti giuridici. Consulta gli allegati, se presenti, per saperne di più.";
    private static final String PAYMENTS_TITLE = "Pagamenti";
    private static final String PAY_PATTERN = "Paga( [\\d.]+,\\d{2} €)?";
    private static final String CONTACT_SENDER_TITLE = "Contatta il mittente";

    // timeline
    private static final String TIMELINE_TITLE = "Stato della notifica";
    // la pagina mostra il mese in maiuscolo, es. "02 OTT, 12:52"
    private static final String EVENT_DATE_PATTERN = "\\d{2} [A-Z]{3}, \\d{2}:\\d{2}";

    private final WebBrowserContractValidator webContractValidator;

    // dettaglio di una notifica a valore legale

    @TestFactory
    Stream<DynamicTest> shouldShowLegalNotificationDetails() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        legalScenario("se presente, intestazione con breadcrumb, oggetto, mittente, data di deposito e IUN", details -> {
                            details.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                            String subject = details.title().read();
                            Assertions.assertThat(subject).isNotBlank();
                            details.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(subject));
                            details.sender().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                            details.depositDate().readAndAssert(h -> Assertions.assertThat(h).matches(LEGAL_DEPOSIT_PATTERN));
                            details.iunLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                            details.iun().readAndAssert(h -> Assertions.assertThat(h).matches(IUN_PATTERN));
                        }),

                        legalScenario("se presente, documenti allegati con messaggio sulla disponibilità", details -> {
                            details.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
                            // il messaggio è preceduto dall'icona, il cui nome fa parte del testo letto
                            details.documentsMessage().readAndAssert(h -> Assertions.assertThat(h).endsWith(LEGAL_DOCUMENTS_MESSAGE));
                            details.documentButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                        }),

                        legalScenario("se presente, stato della notifica con stato corrente e vai al dettaglio", details -> {
                            details.statusTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STATUS_TITLE));
                            details.currentStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                            details.timelineDetailsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_DETAILS));
                        }),

                        legalScenario("se presenti, avviso di avvenuta ricezione e disservizi", details -> {
                            String content = details.content().read();
                            // l'avviso di avvenuta ricezione non c'è per tutte le notifiche
                            if (content.contains(AAR_LABEL)) {
                                details.aarTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(AAR_TITLE));
                                details.aarLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(AAR_LABEL));
                            }
                            if (content.contains(DOWNTIMES_TITLE)) {
                                details.downtimesTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOWNTIMES_TITLE));
                            }
                        })
                ));
    }

    // dettaglio di una comunicazione

    @TestFactory
    Stream<DynamicTest> shouldShowCommunicationDetails() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        communicationScenario("se presente, intestazione con breadcrumb, oggetto, mittente, data di deposito e IUN", details -> {
                            details.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                            String subject = details.title().read();
                            Assertions.assertThat(subject).isNotBlank();
                            details.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(subject));
                            details.sender().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                            details.depositDate().readAndAssert(h -> Assertions.assertThat(h).matches(COMMUNICATION_DEPOSIT_PATTERN));
                            details.iunLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                            details.iun().readAndAssert(h -> Assertions.assertThat(h).matches(IUN_PATTERN));
                        }),

                        communicationScenario("se presente, messaggio del mittente e nota sugli effetti giuridici", details -> {
                            // il testo del messaggio è scritto dal mittente: si verifica solo che ci sia
                            details.communicationGreeting().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                            details.legalEffectsNote().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_EFFECTS_NOTE));
                        }),

                        communicationScenario("se presente, documenti allegati con messaggio sulla disponibilità", details -> {
                            details.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
                            details.documentsMessage().readAndAssert(h -> Assertions.assertThat(h).endsWith(COMMUNICATION_DOCUMENTS_MESSAGE));
                            details.documentButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                        }),

                        communicationScenario("se presenti, pagamenti con paga non premuto e contatti del mittente", details -> {
                            String content = details.content().read();
                            if (content.contains(PAYMENTS_TITLE)) {
                                details.paymentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PAYMENTS_TITLE));
                                // il pulsante mostra l'importo se il pagamento è stato recuperato, es. "Paga 120,00 €"
                                Assertions.assertThat(details.payButton().get(FindPolicy.PRESENT).map(b -> b.getText().trim()))
                                        .hasValueSatisfying(h -> Assertions.assertThat(h).matches(PAY_PATTERN));
                            }
                            if (content.contains(CONTACT_SENDER_TITLE)) {
                                details.contactSenderTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTACT_SENDER_TITLE));
                                details.contactSenderLabels().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                            }
                        })
                ));
    }

    // timeline della notifica a valore legale

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationTimeline() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        legalScenario("se presente, vai al dettaglio apre la timeline con breadcrumb, titolo ed eventi", details -> {
                            String subject = details.title().read();
                            details.timelineDetailsButton().click();
                            NotificationTimelinePFPage timeline = details.notificationTimeline();
                            timeline.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_TITLE));
                            timeline.eventTitles().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                            // non tutti gli eventi hanno una data (es. "Invio della notifica in corso")
                            timeline.eventDates().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).matches(EVENT_DATE_PATTERN)));
                            // il breadcrumb della notifica mostra "Dettaglio notifica" finché non arrivano i dati, quindi si legge dopo gli eventi
                            timeline.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                            timeline.notificationBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(subject));
                            timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_TITLE));
                        })
                ));
    }

    /**
     * Filtra "In arrivo" sulle notifiche a valore legale, apre la prima già letta e verifica il dettaglio. Se non ce n'è
     * una già letta lo scenario termina senza verificarla.
     */
    private WebScenario<NotificationPFPage> legalScenario(String name, Consumer<NotificationDetailsPFPage> assertion) {
        return new WebScenario<>(
                name,
                NotificationPFPage::filterLegalNotifications,
                page -> {
                    if (page.firstReadLegalNotificationDetailsButton().get(FindPolicy.PRESENT).isEmpty()) {
                        // Nessuna notifica a valore legale già letta in prima pagina: il test termina senza aprirne una
                        return;
                    }
                    page.firstReadLegalNotificationDetailsButton().click();
                    waitForDetails(page.notificationDetails());
                    assertion.accept(page.notificationDetails());
                }
        );
    }

    /**
     * Filtra "In arrivo" sulle comunicazioni, apre la prima già letta e verifica il dettaglio. Se non ce n'è una già
     * letta lo scenario termina senza verificarla.
     */
    private WebScenario<NotificationPFPage> communicationScenario(String name, Consumer<NotificationDetailsPFPage> assertion) {
        return new WebScenario<>(
                name,
                NotificationPFPage::filterCommunications,
                page -> {
                    if (page.firstReadCommunicationDetailsButton().get(FindPolicy.PRESENT).isEmpty()) {
                        // Nessuna comunicazione già letta in prima pagina: il test termina senza aprirne una
                        return;
                    }
                    page.firstReadCommunicationDetailsButton().click();
                    waitForDetails(page.notificationDetails());
                    assertion.accept(page.notificationDetails());
                }
        );
    }

    /**
     * Attende il dettaglio leggendo lo IUN, che nella lista non c'è: senza questa attesa la lettura del contenuto della
     * pagina, usata per sapere quali sezioni ci sono, potrebbe avvenire quando è ancora mostrata la lista.
     */
    private static void waitForDetails(NotificationDetailsPFPage details) {
        details.iun().readAndAssert(h -> Assertions.assertThat(h).matches(IUN_PATTERN));
        details.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
    }
}
