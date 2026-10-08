package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina di onboarding "Configura SEND" del cittadino ({@code {baseUrl}/onboarding}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e delle card;</li>
 *     <li>pulsanti, compresi i wizard aperti dalle card.</li>
 * </ul>
 * Nessuno scenario preme "Salta e vai alle tue notifiche", che segna la configurazione come fatta, né conferma i wizard.
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
public class WebConfigureAddressSendPFContractTest {

    // testi attesi

    private static final String TITLE = "Configura SEND";
    private static final String SUBTITLE = "Ottieni il massimo e risparmi!";

    private static final String DIGITAL_DOMICILE_CARD = "Scelgo il meglio di SEND";
    private static final String RECOMMENDED = "Consigliato";
    private static final List<String> DIGITAL_DOMICILE_BULLETS = List.of(
            "Attivi il domicilio digitale e risparmi i costi di notifica legati alle raccomandate.",
            "Ti avvisiamo alla ricezione di una notifica SEND via email e su IO."
    );
    private static final String DIGITAL_DOMICILE_BUTTON = "Attiva il meglio di SEND";

    private static final String COURTESY_CARD = "Voglio solo gli avvisi";
    private static final List<String> COURTESY_BULLETS = List.of(
            "Ricevi avvisi via email, SMS e sull’app IO, così hai più possibilità di leggere in tempo la comunicazione ed evitare i costi di notifica legati alle raccomandate.",
            "Se non apri la notifica SEND entro 5 giorni dalla ricezione, riceverai comunque una raccomandata con i relativi costi aggiuntivi."
    );
    private static final String COURTESY_BUTTON = "Attiva solo gli avvisi";

    private static final String IO_CARD = "Preferisco attivare solo SEND sull’app IO";
    private static final List<String> IO_BULLETS = List.of(
            "Ricevi avvisi e l’accesso alle comunicazioni a valore legale direttamente attraverso IO.",
            "Se non apri la notifica SEND entro 5 giorni dalla ricezione, riceverai comunque una raccomandata con i relativi costi aggiuntivi."
    );
    private static final String IO_BUTTON = "Attiva SEND su IO";

    private static final String SKIP = "Salta e vai alle tue notifiche";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e delle card

    @TestFactory
    Stream<DynamicTest> shouldShowConfigureSendTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(ConfigureAddressSendPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<ConfigureAddressSendPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "intestazione della pagina",
                        page -> {},
                        page -> {
                            page.header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                        }
                ),

                new WebScenario<>(
                        "card il meglio di SEND",
                        page -> {},
                        page -> {
                            page.cardTitles().readAllAndAssert(h -> Assertions.assertThat(h).contains(DIGITAL_DOMICILE_CARD));
                            page.recommendedLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RECOMMENDED));
                            page.digitalDomicileBullets().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(DIGITAL_DOMICILE_BULLETS));
                        }
                ),

                new WebScenario<>(
                        "card solo avvisi",
                        page -> {},
                        page -> {
                            page.cardTitles().readAllAndAssert(h -> Assertions.assertThat(h).contains(COURTESY_CARD));
                            page.courtesyBullets().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(COURTESY_BULLETS));
                        }
                )
        );
    }

    // pulsanti, compresi i wizard aperti dalle card

    @TestFactory
    Stream<DynamicTest> shouldShowConfigureSendButtons() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(ConfigureAddressSendPage.class)
                .tests(buttonScenarios());
    }

    private Stream<WebScenario<ConfigureAddressSendPage>> buttonScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "pulsanti delle card e per saltare la configurazione",
                        page -> {},
                        page -> {
                            page.digitalDomicileButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DIGITAL_DOMICILE_BUTTON));
                            page.courtesyButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COURTESY_BUTTON));
                            page.skipConfigButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SKIP));
                        }
                ),

                new WebScenario<>(
                        "attiva il meglio di SEND apre il wizard il meglio di SEND",
                        page -> {
                            page.oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);  // il banner dei cookie copre le card
                            page.digitalDomicileButton().click();
                        },
                        page -> page.digitalDomicileWizard().assertLoaded()
                ),

                new WebScenario<>(
                        "attiva solo gli avvisi apre il wizard attivazione avvisi",
                        page -> {
                            page.oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);  // il banner dei cookie copre le card
                            page.courtesyButton().click();
                        },
                        page -> page.alertsWizard().assertLoaded()
                )
        );
    }

    // terza card: mostrata solo agli utenti senza recapiti di cortesia, quindi si verifica se presente

    @TestFactory
    Stream<DynamicTest> shouldShowConfigureSendIoCard() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(ConfigureAddressSendPage.class)
                .tests(ioCardScenarios());
    }

    private Stream<WebScenario<ConfigureAddressSendPage>> ioCardScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "se presente, testi della card app IO",
                        page -> {},
                        page -> {
                            if (!page.isIoCardShown()) {
                                // L'utente ha recapiti di cortesia: la card non è mostrata, quindi il test termina senza verificarla
                                return;
                            }
                            page.cardTitles().readAllAndAssert(h -> Assertions.assertThat(h).contains(IO_CARD));
                            page.ioBullets().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(IO_BULLETS));
                            page.ioButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_BUTTON));
                        }
                ),

                new WebScenario<>(
                        "se presente, attiva SEND su IO apre la pagina tutto, sull'app IO",
                        page -> {},
                        page -> {
                            if (!page.isIoCardShown()) {
                                // L'utente ha recapiti di cortesia: la card non è mostrata, quindi il test termina senza verificarla
                                return;
                            }
                            page.oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);  // il banner dei cookie copre le card
                            page.ioButton().click();
                            page.ioPage().assertLoaded();
                        }
                )
        );
    }
}
