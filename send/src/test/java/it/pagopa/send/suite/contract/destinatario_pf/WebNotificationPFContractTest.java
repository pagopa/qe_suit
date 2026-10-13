package it.pagopa.send.suite.contract.destinatario_pf;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina "In arrivo" del cittadino ({@code {baseUrl}/notifiche}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e dei filtri;</li>
 *     <li>messaggi di validazione dei filtri.</li>
 * </ul>
 * La tabella delle notifiche e il banner per attivare il domicilio digitale dipendono dall'utente (notifiche ricevute,
 * domicilio attivo) e non vengono verificati.
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
public class WebNotificationPFContractTest {

    // testi attesi

    // il titolo è "In arrivo" o, per chi ha deleghe a suo carico, "In arrivo per te": si controlla la parte comune
    private static final String TITLE = "In arrivo";
    private static final String COMMUNICATION_TYPE_LABEL = "Tipologia";
    private static final String IUN_LABEL = "Codice IUN";
    private static final String START_DATE_LABEL = "Dal";
    private static final String END_DATE_LABEL = "Al";
    private static final String FILTER = "Filtra";
    private static final List<String> COMMUNICATION_TYPES = List.of("Notifiche a valore legale", "Comunicazioni");

    // messaggi di validazione
    private static final String INVALID_IUN_MESSAGE = "Inserisci un codice IUN valido";
    // le date ammesse vanno da 10 anni fa a oggi
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // dati di prova

    private static final String INVALID_IUN = "abc";
    private static final String TOO_OLD_DATE = "01/01/2000";
    private static final String FUTURE_DATE = "01/01/2099";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e dei filtri

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, etichette dei filtri e filtra disabilitato",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).startsWith(TITLE));
                                    page.communicationTypeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COMMUNICATION_TYPE_LABEL));
                                    page.iunSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                                    page.startDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(START_DATE_LABEL));
                                    page.endDateSearchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(END_DATE_LABEL));
                                    // "Filtra" è disabilitato finché non si compila un filtro, quindi il testo si legge dall'elemento presente
                                    Assertions.assertThat(page.filterButton().get(FindPolicy.PRESENT).map(b -> b.getText())).hasValue(FILTER);
                                    Assertions.assertThat(page.filterButton().isDisabled()).isTrue();
                                }
                        ),

                        new WebScenario<>(
                                "tipologia propone notifiche a valore legale e comunicazioni",
                                page -> page.communicationTypeSelect().click(),
                                page -> page.communicationTypeOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(COMMUNICATION_TYPES))
                        )
                ));
    }

    // messaggi di validazione dei filtri

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationFilters() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "IUN non valido",
                                page -> {},
                                page -> {
                                    page.iunSearchInput().write(INVALID_IUN);
                                    page.filterButton().click();
                                    page.iunErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(INVALID_IUN_MESSAGE));
                                }
                        ),

                        new WebScenario<>(
                                "data dal precedente a 10 anni fa",
                                page -> {},
                                page -> {
                                    // i campi data accettano il testo solo dopo essere stati svuotati
                                    page.startDateSearchInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + TOO_OLD_DATE);
                                    page.startDateErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(dateRangeMessage()));
                                }
                        ),

                        new WebScenario<>(
                                "data al nel futuro",
                                page -> {},
                                page -> {
                                    page.endDateSearchInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + FUTURE_DATE);
                                    page.endDateErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(dateRangeMessage()));
                                }
                        )
                ));
    }

    // messaggio delle date fuori intervallo, calcolato sulla data di oggi
    private static String dateRangeMessage() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Rome"));
        return "Inserisci una data compresa tra " + today.minusYears(10).format(DATE_FORMAT) + " e " + today.format(DATE_FORMAT);
    }
}
