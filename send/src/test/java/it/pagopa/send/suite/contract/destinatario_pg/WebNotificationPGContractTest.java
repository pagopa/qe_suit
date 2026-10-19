package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "In arrivo per &lt;impresa&gt;" della persona giuridica ({@code {baseUrl}/notifiche}).
 * <p>
 * Si verifica solo quello che c'è per tutti gli utenti: il titolo con il nome dell'impresa. Filtri, tabella e
 * paginazione compaiono solo se l'impresa ha ricevuto almeno una notifica, il banner per attivare il domicilio digitale
 * solo agli amministratori senza gruppo e senza domicilio: non vengono verificati.
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
public class WebNotificationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    // il titolo contiene il nome dell'impresa
    private static final String TITLE = NotificationPage.TITLE_PREFIX + RECIPIENT.getOrganization();

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Titolo "In arrivo per &lt;impresa&gt;": con Francesco Petrarca, "In arrivo per Le Epistolae srl".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo con il nome dell'impresa",
                                page -> {},
                                page -> page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE))
                        )
                ));
    }
}
