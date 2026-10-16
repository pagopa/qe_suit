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

@ActiveProfiles({"test", "junit"})
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
