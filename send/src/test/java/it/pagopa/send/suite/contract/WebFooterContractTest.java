package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

@ActiveProfiles({"hotfix", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Slf4j
public class WebFooterContractTest {
    private final WebBrowserContractValidator webContractValidator;


    /**
     * Verifica l'assenza della dicitura "Socio Unico" nel footer
     * della Dashboard per PA.
     *
     * <p>Il test legge il contenuto testuale del footer, lo registra
     * nei log e verifica che non contenga la dicitura "socio unico",
     * indipendentemente dalla combinazione di lettere maiuscole e minuscole.</p>
     *
     * <p>Il test fallisce se la dicitura è presente nel footer.</p>
     *
     * @return uno {@link Stream} di {@link DynamicTest} contenente
     *         lo scenario di verifica del footer della Dashboard PA
     */
    @TestFactory
    Stream<DynamicTest> captionOnlySpaPAFooter() {
        return captionOnlySpaPAFooterScenarios();
    }

    private Stream<DynamicTest> captionOnlySpaPAFooterScenarios()
    {
        String token = "";
        return webContractValidator.as(Tenant.GROSSINI, List.of())
                .on(DashboardPage.class,token)
                .tests(Stream.of(new WebScenario<>(
                        "Check assenza dicitura 'Socio Unico'",
                        page -> {
                            log.info("Contenuto letto: \n{}", page.footer().caption().read());
                        },
                        page ->
                                Assertions.assertFalse(page.footer().caption().read().toLowerCase().contains("socio unico"))
                )));
    }


    /**
     * Verifica l'assenza della dicitura "Socio Unico" nel footer
     * della pagina di notifica destinata a una Persona Fisica (PF).
     *
     * <p>Il test viene eseguito e controlla
     * che il contenuto testuale del footer non includa la dicitura
     * "socio unico", indipendentemente dalla combinazione di lettere
     * maiuscole e minuscole.</p>
     *
     * <p>Il contenuto del footer viene registrato nei log.
     * Il test fallisce se la dicitura è presente.</p>
     *
     * @return uno {@link Stream} di {@link DynamicTest} contenente
     *         lo scenario di verifica del footer della notifica PF
     */
    @TestFactory
    Stream<DynamicTest> captionOnlySpaPFFooter() {
        return captionOnlySpaPFFooterScenarios();
    }

    private Stream<DynamicTest> captionOnlySpaPFFooterScenarios() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "Check assenza dicitura 'Socio Unico'",
                        page -> {
                            log.info("Contenuto letto: \n{}", page.footer().caption().read());
                        },
                        page -> {
                            Assertions.assertFalse(page.footer().caption().read().toLowerCase().contains("socio unico"));
                        }
                )));
    }

    /**
     * Verifica l'assenza della dicitura "Socio Unico" nel footer
     * della pagina di notifica destinata a una Persona Giuridica (PG).
     *
     * <p>Il test viene eseguito e controlla
     * che il contenuto testuale del footer non includa la dicitura
     * "socio unico", indipendentemente dalla combinazione di lettere
     * maiuscole e minuscole.</p>
     *
     * <p>Il contenuto del footer viene registrato nei log.
     * Il test fallisce se la dicitura è presente.</p>
     *
     * @return uno {@link Stream} di {@link DynamicTest} contenente
     *         lo scenario di verifica del footer della notifica PG
     */
    @TestFactory
    Stream<DynamicTest> captionOnlySpaPGFooter() {
        return captionOnlySpaPGScenarios();
    }

    private Stream<DynamicTest> captionOnlySpaPGScenarios() {
        String token = "";
        return webContractValidator.asRecipient(Recipient.PETRARCA)
                .on(NotificationPage.class,token)
                .tests(Stream.of(new WebScenario<>(
                        "Check assenza dicitura 'Socio Unico'",
                        page -> {
                            log.info("Contenuto letto: \n{}", page.footer().caption().read());
                        },
                        page -> {
                            Assertions.assertFalse(page.footer().caption().read().toLowerCase().contains("socio unico"));
                        }
                )));
    }
}
