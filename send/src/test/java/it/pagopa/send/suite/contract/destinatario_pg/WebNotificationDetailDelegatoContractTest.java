package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationDetailPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationTimelinePGPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina di dettaglio di una notifica a valore legale ricevuta tramite delega dalla persona
 * giuridica ({@code {baseUrl}/notifiche-delegato/<id delega>/<IUN>/dettaglio}).
 * <p>
 * Prima degli scenari l'impresa {@code IMPRESA_1} ha una delega attiva della persona fisica {@code USER_1}: si riusa
 * quella già presente, altrimenti {@code USER_1} la crea via BFF e l'impresa la accetta. La delega resta attiva per i
 * giri successivi. Poi il mittente {@code COMUNE_1} invia via BFF una notifica a valore legale a {@code USER_1}; ogni
 * scenario ne apre il dettaglio come impresa delegata, direttamente dall'URL con l'id della delega e lo IUN.
 * Si verifica quello che c'è in ogni notifica delegata: breadcrumb, riepilogo, titoli delle sezioni e persona
 * destinataria. Banner, pagamenti, avvisi e disservizi dipendono dalla notifica e dall'utente: non vengono verificati.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationDetailDelegatoContractTest {

    // testi attesi

    private static final String SUBJECT = "Notifica di test per il dettaglio PG da delega";
    private static final String LEGAL_VALUE = "Notifica a valore legale";
    // "Notifica depositata il giorno " seguito dalla data
    private static final String DEPOSIT_DATE_PATTERN = "Notifica depositata il giorno \\d{2}/\\d{2}/\\d{4}";
    private static final String IUN_LABEL = "Codice IUN";
    // abstract di default delle notifiche di test (notifications/templates/default.yaml)
    private static final String ABSTRACT = "Abstract della notifica";
    private static final String LEGAL_DISCLAIMER = "Notifica inviata tramite SEND, la piattaforma disciplinata dall’art. 26 del D.L. 76/2020";
    private static final String DOCUMENTS_TITLE = "Documenti allegati";
    private static final String DOCUMENTS_AVAILABILITY = "I documenti saranno disponibili online fino a 120 giorni dalla data in cui la notifica SEND ha assunto valore di legge. Salvali sul tuo dispositivo!";
    // l'allegato non ha titolo: il portale mostra il nome del file caricato
    private static final String DOCUMENT_NAME_PATTERN = "PN_NOTIFICATION_ATTACHMENTS-\\w+\\.pdf";
    private static final String STATUS_TITLE = "Stato della notifica";
    private static final String TIMELINE_DETAILS = "Vai al dettaglio";
    private static final String NOTIFICATION_DETAILS_TITLE = "Dettaglio della notifica";
    private static final String RECIPIENT_TITLE = "Persona destinataria";

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final LegalNotificationCreationRequest creationRequest;
    private final UserProvider userProvider;

    /**
     * Testi del dettaglio della notifica inviata al delegante e aperta dall'impresa delegata:
     * <ul>
     *     <li>titolo uguale all'oggetto della notifica; breadcrumb: "In arrivo dalle deleghe" e, come voce corrente,
     *     l'oggetto della notifica;</li>
     *     <li>riepilogo: etichetta "Notifica a valore legale", logo del mittente, il mittente {@code COMUNE_1}, "Notifica
     *     depositata il giorno gg/mm/aaaa", "Codice IUN" con lo IUN della notifica inviata, l'abstract e l'informativa
     *     su SEND;</li>
     *     <li>sezioni: "Documenti allegati" con la disponibilità per 120 giorni e il PDF allegato, "Stato della notifica" con lo stato corrente non vuoto
     *     e "Vai al dettaglio" abilitato, "Dettaglio della notifica";</li>
     *     <li>"Vai al dettaglio" apre la pagina "Stato della notifica";</li>
     *     <li>"Persona destinataria" con denominazione e codice fiscale del delegante.</li>
     * </ul>
     * Non vengono verificati i testi che dipendono dall'avanzamento della notifica mentre la pagina è aperta:
     * l'"Avviso di avvenuta ricezione", che SEND genera circa 30 secondi dopo l'accettazione, e la descrizione dello stato
     * corrente (es. "Il delegato ... ha letto la notifica."), che cambia quando SEND registra la lettura dell'impresa.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDelegatedNotificationDetailTexts() {
        User delegator = userProvider.resolve(UserAlias.USER_1);
        User impresa = userProvider.resolve(UserAlias.IMPRESA_1);
        // la delega non riguarda una notifica: vale per tutte quelle del delegante, quindi si riusa tra un giro e l'altro
        Delegation delegation = sendJourney.withActiveDelegation(delegator, impresa).get(Delegation.class);
        // la notifica va al delegante: l'impresa la vede tra quelle "In arrivo dalle deleghe"
        LegalNotificationDomain createdNotification = sendLegalNotification(delegator);
        String senderName = userProvider.resolve(UserAlias.COMUNE_1).getOrganization();

        return webContractValidator.asRecipient(impresa)
                .on(DelegatedNotificationDetailPGPage.class, delegation.getMandateId(), createdNotification.getIun())
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo e breadcrumb con in arrivo dalle deleghe e l'oggetto della notifica",
                                detail -> {},
                                detail -> {
                                    detail.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBJECT));
                                    detail.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DelegatedNotificationPage.TITLE));
                                    detail.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBJECT));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: valore legale, mittente, data di deposito, codice IUN, abstract e informativa",
                                detail -> {},
                                detail -> {
                                    // l'etichetta è resa in maiuscolo dal CSS
                                    detail.legalValueChip().readAndAssert(h -> Assertions.assertThat(h).isEqualToIgnoringCase(LEGAL_VALUE));
                                    Assertions.assertThat(detail.senderLogo().get(FindPolicy.PRESENT)).as("logo del mittente").isPresent();
                                    detail.sender().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(senderName));
                                    detail.depositDate().readAndAssert(h -> Assertions.assertThat(h).matches(DEPOSIT_DATE_PATTERN));
                                    detail.iunLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                                    detail.iun().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(createdNotification.getIun()));
                                    detail.abstractText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ABSTRACT));
                                    detail.legalDisclaimer().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_DISCLAIMER));
                                }
                        ),

                        new WebScenario<>(
                                "sezioni documenti con il pdf allegato, stato della notifica con stato corrente e vai al dettaglio, dettaglio della notifica",
                                detail -> {},
                                detail -> {
                                    detail.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
                                    detail.documentsAvailability().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_AVAILABILITY));
                                    detail.firstDocument().readAndAssert(h -> Assertions.assertThat(h).matches(DOCUMENT_NAME_PATTERN));
                                    detail.statusTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STATUS_TITLE));
                                    detail.currentStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    detail.timelineDetailsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_DETAILS));
                                    // stato iniziale: "Vai al dettaglio" è abilitato
                                    Assertions.assertThat(detail.timelineDetailsButton().isDisabled()).isFalse();
                                    detail.notificationDetailsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_DETAILS_TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "vai al dettaglio apre lo stato della notifica",
                                detail -> detail.timelineDetailsButton().click(),
                                detail -> {
                                    NotificationTimelinePGPage timeline = detail.notificationTimeline();
                                    timeline.assertLoaded();
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "persona destinataria con nome e codice fiscale del delegante",
                                detail -> {},
                                detail -> {
                                    detail.recipientTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RECIPIENT_TITLE));
                                    detail.recipient().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(delegator.getDenomination() + " - " + delegator.getTaxId()));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User delegator) {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        RecipientSpec recipient = recipientSpecFactory.build(delegator, Map.of(
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        // la richiesta di creazione è condivisa (singleton) e non viene svuotata tra un invio e l'altro: senza
        // svuotarla, la notifica includerebbe anche i destinatari rimasti da invii precedenti
        creationRequest.getRecipients().clear();
        try {
            return sendJourney
                    .withSender(sender)
                    .prepareNotification(Map.of(
                            "subject", SUBJECT,
                            "physicalCommunication", "REGISTERED_LETTER_890",
                            "feePolicy", "FLAT_RATE"
                    ))
                    .withRecipient(recipient)
                    .sendNotification(sender, NotificationStatus.ACCEPTED)
                    .get(LegalNotificationDomain.class);
        }
        finally {
            // anche dopo un invio fallito: i destinatari non restano per gli invii successivi
            creationRequest.getRecipients().clear();
        }
    }
}
