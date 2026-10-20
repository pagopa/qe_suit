package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
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
 * Contract test della pagina "In arrivo per &lt;impresa&gt;" della persona giuridica ({@code {baseUrl}/notifiche}).
 * <p>
 * Si verifica solo quello che c'è per tutti gli utenti: il titolo con il nome dell'impresa. Filtri, tabella e
 * paginazione compaiono solo se l'impresa ha ricevuto almeno una notifica, il banner per attivare il domicilio digitale
 * solo agli amministratori senza gruppo e senza domicilio: non vengono verificati.
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationPGContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Titolo "In arrivo per &lt;impresa&gt;": con Francesco Petrarca, "In arrivo per Le Epistolae srl".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        User recipient = userProvider.resolve(UserAlias.IMPRESA_2);
        // il titolo contiene il nome dell'impresa
        String title = NotificationPage.TITLE_PREFIX + recipient.getOrganization();

        return webContractValidator.asRecipient(recipient)
                .on(NotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo con il nome dell'impresa",
                                page -> {},
                                page -> page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title))
                        )
                ));
    }
}
