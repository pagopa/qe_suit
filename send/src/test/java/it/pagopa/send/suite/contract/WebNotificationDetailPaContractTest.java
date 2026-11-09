package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.component.NotificationDetailsDrawer;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationSummarySection;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina di dettaglio di una notifica inviata dalla PA ({@code {baseUrl}/dashboard/:iun/dettaglio}),
 * aperta direttamente dalla rotta con una notifica senza pagamenti inviata dal test.
 * <p>
 * Lo stato della notifica non è verificato, perché cambia nel tempo. "Annulla notifica" non viene mai cliccato.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationDetailPaContractTest {

    // testi attesi

    private static final String NOTIFICATIONS = "Notifiche";
    private static final String LEGAL_VALUE = "Notifica a valore legale";
    private static final String[] SUMMARY_LABELS = {"Numero protocollo", "Oggetto della notifica", "Mittente", "Persona destinataria"};
    private static final String GO_TO_DETAIL = "Vai al dettaglio";
    private static final String DOCUMENTS = "Documenti allegati";
    private static final String CANCEL_NOTIFICATION = "Annulla notifica";
    private static final String NOTIFICATION_STATUS = "Stato della notifica";
    private static final String NOTIFICATION_DETAIL = "Dettaglio della notifica";
    // "Gruppi" compare solo se la notifica ha un gruppo: non è verificato
    private static final String[] DRAWER_LABELS = {"Codice IUN", "Numero protocollo", "Oggetto della notifica", "Mittente", "Persona destinataria", "Testo della notifica"};

    private static final String NOTIFICATION_SUBJECT = "SUIT contract test dettaglio notifica";

    private final SendJourney sendJourney;
    private final WebBrowserContractValidator webContractValidator;
    private final RecipientSpecFactory recipientSpecFactory;
    private final UserProvider userProvider;

    /**
     * Dettaglio di una notifica a valore legale senza pagamenti, inviata dal test e appena accettata: breadcrumb,
     * riepilogo con etichette e valori, pulsanti "Vai al dettaglio" e "Annulla notifica" abilitati, titoli delle
     * sezioni e pannello "Dettaglio della notifica" aperto da "Vai al dettaglio".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationDetail() {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        User recipientUser = userProvider.resolve(UserAlias.USER_1);
        RecipientSpec recipient = recipientSpecFactory.build(recipientUser, Map.of(
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));

        LegalNotificationDomain notification = sendJourney
                .withSender(sender)
                .prepareNotification(Map.of(
                        "subject", NOTIFICATION_SUBJECT,
                        "physicalCommunication", "REGISTERED_LETTER_890",
                        "feePolicy", "FLAT_RATE"
                ))
                .withRecipient(recipient)
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .get(LegalNotificationDomain.class);
        String iun = notification.getIun();

        return webContractValidator.as(sender)
                .on(MittenteNotificationDetailsPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb notifiche e iun",
                                page -> {},
                                page -> {
                                    page.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                                    page.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(iun));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: notifica a valore legale e iun come titolo",
                                page -> {},
                                page -> {
                                    // il tag è mostrato in maiuscolo dallo stile
                                    page.notificationSummarySection().legalValueTag().readAndAssert(h -> Assertions.assertThat(h).isEqualToIgnoringCase(LEGAL_VALUE));
                                    page.notificationSummarySection().iunHeader().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(iun));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: etichette e valori dei dati",
                                page -> {},
                                page -> {
                                    MittenteNotificationSummarySection summary = page.notificationSummarySection();
                                    summary.detailLabels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(SUMMARY_LABELS));
                                    List<String> values = summary.detailValues().readAll();
                                    Assertions.assertThat(values).hasSize(SUMMARY_LABELS.length);
                                    Assertions.assertThat(values.get(0)).as("numero protocollo").isNotBlank();
                                    Assertions.assertThat(values.get(1)).as("oggetto").isEqualTo(NOTIFICATION_SUBJECT);
                                    Assertions.assertThat(values.get(2)).as("mittente").isEqualTo(notification.getSenderDenomination());
                                    Assertions.assertThat(values.get(3)).as("destinatario").contains(recipientUser.getTaxId());
                                }
                        ),

                        new WebScenario<>(
                                "pulsanti vai al dettaglio e annulla notifica abilitati",
                                page -> {},
                                page -> {
                                    page.notificationSummarySection().openDetailsSidebarButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GO_TO_DETAIL));
                                    Assertions.assertThat(page.notificationSummarySection().openDetailsSidebarButton().isDisabled()).isFalse();
                                    page.cancelNotificationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL_NOTIFICATION));
                                    Assertions.assertThat(page.cancelNotificationButton().isDisabled()).isFalse();
                                }
                        ),

                        new WebScenario<>(
                                "titoli delle sezioni documenti, stato e dettaglio della notifica",
                                page -> {},
                                page -> {
                                    page.attachmentSection().header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS));
                                    page.notificationStatusSection().header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_STATUS));
                                    page.notificationStatusSection().detailsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GO_TO_DETAIL));
                                    page.notificationDetailSectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_DETAIL));
                                }
                        ),

                        new WebScenario<>(
                                "pannello dettaglio della notifica aperto da vai al dettaglio",
                                page -> page.notificationSummarySection().openDetailsSidebarButton().click(),
                                page -> {
                                    NotificationDetailsDrawer drawer = page.notificationDetailsDrawer();
                                    drawer.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_DETAIL));
                                    drawer.labels().readAllAndAssert(h -> Assertions.assertThat(h).containsSubsequence(DRAWER_LABELS));
                                }
                        )
                ));
    }
}
