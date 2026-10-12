package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.AppStatusScenarios;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
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
 * Contract test della pagina "Stato della piattaforma" del cittadino ({@code {baseUrl}/app-status}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>stato attuale dei servizi;</li>
 *     <li>storico dei disservizi e sua paginazione.</li>
 * </ul>
 * Gli scenari sono comuni alla pagina dell'impresa ({@link AppStatusScenarios}). Nessuno scenario scarica le
 * attestazioni.
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
public class WebAppStatusPFContractTest {

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowAppStatusTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(AppStatusScenarios.texts());
    }

    // stato attuale dei servizi

    @TestFactory
    Stream<DynamicTest> shouldShowCurrentStatus() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(AppStatusScenarios.currentStatus());
    }

    // storico dei disservizi, se presente

    @TestFactory
    Stream<DynamicTest> shouldShowDowntimeHistory() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AppStatusPFPage.class)
                .tests(AppStatusScenarios.downtimeHistory());
    }
}
