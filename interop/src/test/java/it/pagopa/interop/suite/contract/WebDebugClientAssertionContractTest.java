package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitSupportConfig;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@ActiveProfiles({"qa", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitSupportConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WebDebugClientAssertionContractTest {

    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;
    private final CurrentUserSession currentUserSession;

    public WebDebugClientAssertionContractTest(
            @Qualifier("junitWebPresentationGateway")
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider,
            CurrentUserSession currentUserSession
    ) {
        this.webPresentationGatewayProvider = webPresentationGatewayProvider;
        this.currentUserSession = currentUserSession;
    }

    @TestFactory
    Stream<DynamicTest> shouldValidateDebugClientAssertionForm() {
        return validationCases()
                .map(testCase ->
                        DynamicTest.dynamicTest(
                                testCase.name(),
                                () -> executeValidationCase(testCase)
                        )
                );
    }

    private Stream<ValidationCase> validationCases() {
        return Stream.of(

                new ValidationCase(
                        "client assertion vuota",
                        page -> page.clientAssertionInput().fill(" "),
                        DebugClientAssertionPage::getClientAssertionErrorMessage,
                        "Inserisci un JWT valido."
                ),

                new ValidationCase(
                        "client assertion non valida",
                        page -> page.clientAssertionInput()
                                .fill("invalid client assertion"),
                        DebugClientAssertionPage::getClientAssertionErrorMessage,
                        "Inserisci un JWT valido."
                ),

                new ValidationCase(
                        "client id vuoto",
                        page -> page.clientIdInput().fill(" "),
                        DebugClientAssertionPage::getClientIdErrorMessage,
                        "Inserisci un UUID valido."
                )

        );
    }

    private void executeValidationCase(ValidationCase testCase) {
        currentUserSession.set(
                User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                Tenant.COMUNE_DI_MILANO
        );

        WebPresentationGateway gateway =
                webPresentationGatewayProvider.getObject();

        try {
            DebugClientAssertionPage page =
                    gateway.bind(DebugClientAssertionPage.class);

            page.navigateTo();
            page.assertLoaded();

            testCase.fill().accept(page);

            page.submitButton().click();

            Assertions.assertThat(
                    testCase.errorExtractor().apply(page)
            ).isEqualTo(testCase.expectedError());

        } finally {
            gateway.close();
        }
    }

    private record ValidationCase(
            String name,
            Consumer<DebugClientAssertionPage> fill,
            Function<DebugClientAssertionPage, String> errorExtractor,
            String expectedError
    ) {
    }
}