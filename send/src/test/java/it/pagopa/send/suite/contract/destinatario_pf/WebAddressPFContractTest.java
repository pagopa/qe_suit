package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "I tuoi recapiti" del cittadino ({@code {baseUrl}/recapiti}).
 * <p>
 * Si verifica solo quello che c'è per tutti gli utenti: intestazione, titoli delle card e presenza dello stato del
 * domicilio digitale e di SEND sull'app IO. Il contenuto delle card dipende dai recapiti dell'utente (attivi o da
 * attivare) e non viene verificato.
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAddressPFContractTest {

    // testi attesi

    private static final String TITLE = "I tuoi recapiti";
    private static final String SUBTITLE = "Gestisci i recapiti digitali su cui ricevere le comunicazioni a valore legale di SEND e/o i relativi messaggi di cortesia.";
    private static final String LEGAL_TITLE = "Il tuo domicilio digitale";
    private static final String IO_TITLE = "SEND sull’app IO";
    private static final String EMAIL_TITLE = "Il tuo indirizzo email";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Intestazione (titolo e sottotitolo), titoli delle card "Il tuo domicilio digitale", "SEND sull’app IO" e "Il tuo
     * indirizzo email" e presenza dello stato del domicilio digitale e di SEND sull'app IO, senza verificarne il
     * valore.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowAddressTexts() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.USER_1))
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                }
                        ),

                        new WebScenario<>(
                                "titoli delle card",
                                page -> {},
                                page -> {
                                    page.legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_TITLE));
                                    page.ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_TITLE));
                                    page.emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_TITLE));
                                }
                        ),

                        // il valore dello stato dipende dai recapiti dell'utente: si verifica solo che ci sia
                        new WebScenario<>(
                                "stato del domicilio digitale e di SEND sull'app IO",
                                page -> {},
                                page -> {
                                    page.legalContactsStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    page.ioStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                }
                        )
                ));
    }
}
