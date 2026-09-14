package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DelegationsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
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
 * Sostituisce gli scenari "verifica la raggiungibilità delle pagine" di
 * {@code features/pf/navigazione-send-pf.feature}, lato destinatario Persona Fisica.
 * Si veda {@link WebMittenteNavigationContractTest} per il razionale (scenario no-op: il
 * framework naviga e chiama {@code assertLoaded()} prima ancora di eseguirlo).
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
public class WebRecipientPfNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldReachNotificationPF() {
        return reachabilityTest(NotificationPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachAddressPF() {
        return reachabilityTest(AddressPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachDelegationsPF() {
        return reachabilityTest(DelegationsPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachAppStatusPF() {
        return reachabilityTest(AppStatusPFPage.class);
    }

    private <P extends Page> Stream<DynamicTest> reachabilityTest(Class<P> pageType) {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(pageType)
                .tests(Stream.of(new WebScenario<>(
                        "controllo caricamento pagina " + pageType.getSimpleName(),
                        page -> {},
                        page -> {}
                )));
    }
}
