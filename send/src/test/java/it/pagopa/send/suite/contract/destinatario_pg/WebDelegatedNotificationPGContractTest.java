package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
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
 * Contract test della pagina "In arrivo dalle deleghe" della persona giuridica ({@code {baseUrl}/notifiche-delegato}).
 * <p>
 * Si verifica solo quello che vale per tutti gli utenti: il titolo, e l'assenza del filtro "Tipologia" e del banner per
 * attivare il domicilio digitale, che sono solo in "In arrivo per &lt;impresa&gt;". Filtri, tabella e paginazione
 * compaiono solo se ci sono notifiche delegate all'impresa, il selettore del gruppo solo se l'impresa ha dei gruppi: non
 * vengono verificati.
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
public class WebDelegatedNotificationPGContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Titolo "In arrivo dalle deleghe".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDelegatedNotificationListTexts() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_2))
                .on(DelegatedNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo della pagina",
                                page -> {},
                                page -> page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DelegatedNotificationPage.TITLE))
                        )
                ));
    }

    /**
     * Componenti di "In arrivo per &lt;impresa&gt;" che questa pagina non ha mai: filtro "Tipologia" e banner per
     * attivare il domicilio digitale, entrambi assenti dal DOM. L'assenza si verifica cercando il {@code main} della
     * pagina solo se non li contiene, per non attendere il timeout del framework su un elemento che non c'è.
     */
    @TestFactory
    Stream<DynamicTest> shouldNotShowNotificationListOnlyComponents() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_2))
                .on(DelegatedNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "filtro Tipologia assente",
                                page -> {},
                                page -> Assertions.assertThat(page.noCommunicationTypeSelect().get(FindPolicy.PRESENT))
                                        .as("filtro Tipologia assente").isPresent()
                        ),

                        new WebScenario<>(
                                "banner per attivare il domicilio digitale assente",
                                page -> {},
                                page -> Assertions.assertThat(page.noAddDomicileBanner().get(FindPolicy.PRESENT))
                                        .as("banner del domicilio digitale assente").isPresent()
                        )
                ));
    }
}
