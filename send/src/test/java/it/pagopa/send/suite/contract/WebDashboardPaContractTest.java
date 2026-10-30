package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
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
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_search.infrastructure.suit.component.NotificationsTable;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Notifiche" della PA ({@code {baseUrl}/dashboard}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>intestazione della pagina e pulsanti, con la pagina aperta direttamente dalla rotta;</li>
 *     <li>filtri e tabella delle notifiche, con una notifica inviata dal test.</li>
 * </ul>
 * Filtri e tabella compaiono solo se l'ente ha inviato almeno una notifica: per non dipendere dai dati dell'utenza, il
 * test ne invia una via BFF. Lo stato della notifica nella tabella non è verificato, perché cambia nel tempo.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebDashboardPaContractTest {

    // testi attesi

    private static final String TITLE = "Notifiche";
    private static final String SUBTITLE = "Qui trovi tutte le notifiche inviate dall'ente. Puoi filtrarle per Codice Fiscale, Codice IUN, data di invio e stato.";
    private static final String LANGUAGE_SETTINGS = "Impostazioni lingua";
    private static final String NEW_NOTIFICATION = "Invia una notifica";
    private static final String[] FILTER_LABELS = {"Codice Fiscale", "Codice IUN", "Dal", "Al", "Stato"};
    private static final String ALL_STATUSES = "Tutti gli stati";
    private static final String FILTER = "Filtra";
    // l'ultima colonna, quella del pulsante per aprire la notifica, non ha intestazione. La colonna "Gruppi", presente nel
    // sorgente di riferimento, non è mostrata dal portale in ambiente test
    private static final String[] TABLE_HEADERS = {"Data", "Codice fiscale", "Oggetto", "Codice IUN", "Stato", ""};

    // le notifiche del giorno mostrano "Oggi" al posto della data
    private static final String TODAY = "Oggi";
    private static final String OPEN = "Apri";

    private static final String NOTIFICATION_SUBJECT = "SUIT contract test dashboard";

    private final SendJourney sendJourney;
    private final WebBrowserContractValidator webContractValidator;
    private final RecipientSpecFactory recipientSpecFactory;
    private final UserProvider userProvider;

    /**
     * Intestazione della pagina aperta direttamente dalla rotta: titolo, sottotitolo e pulsanti "Impostazioni lingua" e
     * "Invia una notifica" abilitati.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDashboardHeader() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(DashboardPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina",
                                page -> {},
                                page -> {
                                    page.header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                }
                        ),

                        new WebScenario<>(
                                "pulsanti impostazioni lingua e invia una notifica abilitati",
                                page -> {},
                                page -> {
                                    page.languageSettingsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LANGUAGE_SETTINGS));
                                    Assertions.assertThat(page.languageSettingsButton().isDisabled()).isFalse();
                                    page.newNotificationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_NOTIFICATION));
                                    Assertions.assertThat(page.newNotificationButton().isDisabled()).isFalse();
                                }
                        )
                ));
    }

    /**
     * Filtri e tabella, con una notifica inviata dal test: etichette e stato iniziale dei filtri, intestazioni della
     * tabella e riga della notifica inviata.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowFiltersAndNotifications() {
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
                // appena accettata la notifica non compare ancora nella pagina: compare entro l'avvio dell'invio
                .waitForNotificationStatus(NotificationStatus.DELIVERING)
                .get(LegalNotificationDomain.class);

        return webContractValidator.as(sender)
                .on(DashboardPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "etichette dei filtri",
                                page -> {},
                                page -> page.filterLabels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(FILTER_LABELS))
                        ),

                        new WebScenario<>(
                                "filtri vuoti, tutti gli stati e filtra disabilitato",
                                page -> {},
                                page -> {
                                    page.recipientIdSearchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.iunSearchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.startDateSearchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.endDateSearchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.statusFilter().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ALL_STATUSES));
                                    // un pulsante disabilitato non risulta visibile: il testo si legge dall'elemento presente
                                    Assertions.assertThat(page.filterButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(FILTER);
                                    Assertions.assertThat(page.filterButton().isDisabled()).isTrue();
                                }
                        ),

                        new WebScenario<>(
                                "intestazioni della tabella",
                                page -> {},
                                page -> page.notificationsTable().headers().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(TABLE_HEADERS))
                        ),

                        new WebScenario<>(
                                "riga della notifica inviata: data, destinatario, oggetto e apri",
                                page -> {},
                                page -> {
                                    NotificationsTable table = page.notificationsTable();
                                    // le colonne si leggono per tutte le righe: la riga della notifica è quella del suo IUN
                                    int row = table.iuns().readAll().indexOf(notification.getIun());
                                    Assertions.assertThat(row).as("riga della notifica %s", notification.getIun()).isNotNegative();
                                    Assertions.assertThat(table.dateCells().readAll().get(row)).as("data").isEqualTo(TODAY);
                                    // nella pagina della PA la seconda colonna è il codice fiscale del destinatario
                                    Assertions.assertThat(table.senders().readAll().get(row)).as("codice fiscale").isEqualTo(recipientUser.getTaxId());
                                    Assertions.assertThat(table.subjectCells().readAll().get(row)).as("oggetto").isEqualTo(NOTIFICATION_SUBJECT);
                                    Assertions.assertThat(table.openButtons().readAll().get(row)).as("pulsante").isEqualTo(OPEN);
                                }
                        )
                ));
    }
}
