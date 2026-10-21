package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
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
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationDetailPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
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
 * Contract test della pagina di dettaglio di una notifica a valore legale della persona giuridica
 * ({@code {baseUrl}/notifiche/<IUN>/dettaglio}).
 * <p>
 * Prima degli scenari il mittente {@code COMUNE_1} invia via BFF una notifica a valore legale all'impresa
 * {@code IMPRESA_1}; ogni scenario ne apre il dettaglio direttamente dall'URL, con il suo IUN.
 * Si verifica quello che c'è in ogni notifica: breadcrumb, riepilogo e titoli delle sezioni. Banner, pagamenti, avvisi e
 * disservizi dipendono dalla notifica e dall'utente: non vengono verificati.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationDetailPGContractTest {

    // testi attesi

    private static final String LEGAL_VALUE = "Notifica a valore legale";
    // "Notifica depositata il giorno " seguito dalla data
    private static final String DEPOSIT_DATE_PATTERN = "Notifica depositata il giorno \\d{2}/\\d{2}/\\d{4}";
    // es. XYDV-UNLJ-TVND-202305-M-1
    private static final String IUN_PATTERN = "[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d";
    private static final String IUN_LABEL = "Codice IUN";
    private static final String LEGAL_DISCLAIMER = "Notifica inviata tramite SEND, la piattaforma disciplinata dall’art. 26 del D.L. 76/2020";
    private static final String DOCUMENTS_TITLE = "Documenti allegati";
    private static final String STATUS_TITLE = "Stato della notifica";
    private static final String TIMELINE_DETAILS = "Vai al dettaglio";
    private static final String NOTIFICATION_DETAILS_TITLE = "Dettaglio della notifica";

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final LegalNotificationCreationRequest creationRequest;
    private final UserProvider userProvider;

    /**
     * Testi del dettaglio della notifica inviata all'impresa:
     * <ul>
     *     <li>breadcrumb: "In arrivo per &lt;impresa&gt;" e, come voce corrente, il titolo della notifica;</li>
     *     <li>riepilogo: etichetta "Notifica a valore legale", logo del mittente, mittente non vuoto, "Notifica
     *     depositata il giorno gg/mm/aaaa", "Codice IUN" con lo IUN nel suo formato e l'informativa su SEND;</li>
     *     <li>sezioni: "Documenti allegati", "Stato della notifica" con lo stato corrente non vuoto e "Vai al
     *     dettaglio" abilitato, "Dettaglio della notifica".</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationDetailTexts() {
        // l'impresa deve avere un codice fiscale numerico: SEND non accetta altri codici per i destinatari PG
        User impresa = userProvider.resolve(UserAlias.IMPRESA_1);
        String notificationsBreadcrumb = NotificationPage.TITLE_PREFIX + impresa.getOrganization();
        LegalNotificationDomain createdNotification = sendLegalNotification(impresa);

        return webContractValidator.asRecipient(impresa)
                .on(NotificationDetailPGPage.class, createdNotification.getIun())
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb con il nome dell'impresa e l'oggetto della notifica",
                                detail -> {},
                                detail -> {
                                    detail.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(notificationsBreadcrumb));
                                    String title = detail.title().read();
                                    detail.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: valore legale, mittente, data di deposito, codice IUN e informativa",
                                detail -> {},
                                detail -> {
                                    // l'etichetta è resa in maiuscolo dal CSS
                                    detail.legalValueChip().readAndAssert(h -> Assertions.assertThat(h).isEqualToIgnoringCase(LEGAL_VALUE));
                                    Assertions.assertThat(detail.senderLogo().get(FindPolicy.PRESENT)).as("logo del mittente").isPresent();
                                    detail.sender().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    detail.depositDate().readAndAssert(h -> Assertions.assertThat(h).matches(DEPOSIT_DATE_PATTERN));
                                    detail.iunLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                                    detail.iun().readAndAssert(h -> Assertions.assertThat(h).matches(IUN_PATTERN));
                                    detail.legalDisclaimer().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_DISCLAIMER));
                                }
                        ),

                        new WebScenario<>(
                                "sezioni documenti, stato della notifica con stato corrente e vai al dettaglio, dettaglio della notifica",
                                detail -> {},
                                detail -> {
                                    detail.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
                                    detail.statusTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STATUS_TITLE));
                                    detail.currentStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    detail.timelineDetailsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_DETAILS));
                                    // stato iniziale: "Vai al dettaglio" è abilitato
                                    Assertions.assertThat(detail.timelineDetailsButton().isDisabled()).isFalse();
                                    detail.notificationDetailsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_DETAILS_TITLE));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User impresa) {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        // destinatario: l'impresa, con la sua denominazione invece del nome della persona che accede
        RecipientSpec recipient = recipientSpecFactory.build(impresa, Map.of(
                "denomination", impresa.getOrganization(),
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
                            "subject", "Notifica di test per il dettaglio PG",
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
