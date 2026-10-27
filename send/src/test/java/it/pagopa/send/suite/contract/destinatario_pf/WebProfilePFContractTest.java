package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.ProfilePFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina "I tuoi dati" del cittadino ({@code {baseUrl}/profilo}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>dati dell'utente collegato.</li>
 * </ul>
 * I dati mostrati sono quelli dell'utente con cui si apre la pagina, quindi si confrontano con nome, cognome e codice
 * fiscale dell'utenza {@link UserAlias#USER_1} dell'ambiente.
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProfilePFContractTest {

    // testi attesi

    private static final String TITLE = "I tuoi dati";
    private static final String SUBTITLE = "Questi dati vengono ricavati dal tuo SPID o CIE e non sono modificabili.";
    private static final List<String> LABELS = List.of("Nome", "Cognome", "Codice fiscale");

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    private User recipient() {
        return userProvider.resolve(UserAlias.USER_1);
    }

    /**
     * Testi della pagina: titolo, sottotitolo ed etichette "Nome", "Cognome" e "Codice fiscale".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowProfileTexts() {
        return webContractValidator.asRecipient(recipient())
                .on(ProfilePFPage.class)
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
                                "etichette dei dati",
                                page -> {},
                                page -> page.labels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(LABELS))
                        )
                ));
    }

    /**
     * Nome, cognome e codice fiscale dell'utente collegato, confrontati con quelli dell'utenza configurata.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowProfileData() {
        User recipient = recipient();
        return webContractValidator.asRecipient(recipient)
                .on(ProfilePFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "nome, cognome e codice fiscale dell'utente collegato",
                                page -> {},
                                page -> {
                                    page.firstName().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(recipient.getDenomination()));
                                    page.lastName().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(recipient.getFamilyName()));
                                    page.taxCode().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(recipient.getTaxId()));
                                }
                        )
                ));
    }
}
