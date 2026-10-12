package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.NewDelegationScenarios;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NewDelegationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "Aggiungi una delega" del cittadino ({@code {baseUrl}/deleghe/nuova}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e del form;</li>
 *     <li>presenza degli elementi e dei pulsanti;</li>
 *     <li>valori iniziali del form;</li>
 *     <li>messaggi di validazione.</li>
 * </ul>
 * Gli scenari sono comuni al form dell'impresa ({@link NewDelegationScenarios}). Nessuno scenario invia realmente la
 * richiesta: quando viene premuto "Invia la richiesta" il codice fiscale usato è sempre non valido.
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
public class WebNewDelegationPFContractTest {

    private static final String SUBTITLE = "Inserisci i dati della persona fisica o giuridica a cui vuoi delegare la lettura delle tue notifiche.";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e del form

    @TestFactory
    Stream<DynamicTest> shouldShowNewDelegationTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(NewDelegationScenarios.texts(SUBTITLE));
    }

    // presenza degli elementi e dei pulsanti, compresi quelli che compaiono in base alle scelte

    @TestFactory
    Stream<DynamicTest> shouldShowNewDelegationElements() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(NewDelegationScenarios.elements());
    }

    // stato del form all'apertura della pagina

    @TestFactory
    Stream<DynamicTest> shouldStartWithDefaultValues() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(NewDelegationScenarios.defaultValues());
    }

    // messaggi di validazione mostrati dal portale

    @TestFactory
    Stream<DynamicTest> shouldValidateNewDelegationForm() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(NewDelegationScenarios.validations());
    }
}
