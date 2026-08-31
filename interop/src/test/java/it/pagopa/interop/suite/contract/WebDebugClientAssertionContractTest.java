package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitSupportConfig;
import it.pagopa.interop.common.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.interop.common.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
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

@ActiveProfiles({"qa", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitSupportConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebDebugClientAssertionContractTest {

    private final WebContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateDebugClientAssertionForm() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(DebugClientAssertionPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<DebugClientAssertionPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "client assertion vuota",
                        page -> {
                            page.clientAssertionInput().fill(" ");
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(
                                page.getClientAssertionErrorMessage()
                        ).isEqualTo("Inserisci un JWT valido.")
                ),

                new WebScenario<>(
                        "client assertion non valida",
                        page -> {
                            page.clientAssertionInput()
                                    .fill("invalid client assertion");
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(
                                page.getClientAssertionErrorMessage()
                        ).isEqualTo("Inserisci un JWT valido.")
                ),

                new WebScenario<>(
                        "client id vuoto",
                        page -> {
                            page.clientIdInput().fill(" ");
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(
                                page.getClientIdErrorMessage()
                        ).isEqualTo("Inserisci un UUID valido.")
                )
        );
    }
}