package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitSupportConfig;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import it.pagopa.interop.web.infrastructure.config.JUnitWebSuitConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitSupportConfig.class,
        JUnitWebSuitConfig.class
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
    Stream<DynamicTest> shouldErrorWhenClientAssertionIsNotValid() {

        record TestCase(String clientAssertion, String expectedError) {}

        return Stream.of(
                new TestCase(" ", "Inserisci un JWT valido."),
                new TestCase("invalid client assertion", "Inserisci un JWT valido.")
        ).map(testCase ->
                DynamicTest.dynamicTest(
                        "clientAssertion='%s' -> error='%s'"
                                .formatted(
                                        testCase.clientAssertion(),
                                        testCase.expectedError()
                                ),
                        () -> {
                            DebugClientAssertionPage page =
                                    createDebugClientAssertionPage();

                            page.clientAssertionInput()
                                    .fill(testCase.clientAssertion());

                            page.submitButton()
                                    .click();

                            Assertions.assertThat(
                                    page.getClientAssertionErrorMessage()
                            ).isEqualTo(testCase.expectedError());
                        }
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldErrorWhenClientIdIsNotValid() {

        record TestCase(String clientId, String expectedError) {}

        return Stream.of(
                new TestCase(" ", "Inserisci un UUID valido.")
        ).map(testCase ->
                DynamicTest.dynamicTest(
                        "clientId='%s' -> error='%s'"
                                .formatted(
                                        testCase.clientId(),
                                        testCase.expectedError()
                                ),
                        () -> {
                            DebugClientAssertionPage page =
                                    createDebugClientAssertionPage();

                            page.clientIdInput()
                                    .fill(testCase.clientId());

                            page.submitButton()
                                    .click();

                            Assertions.assertThat(
                                    page.getClientIdErrorMessage()
                            ).isEqualTo(testCase.expectedError());
                        }
                )
        );
    }

    private DebugClientAssertionPage createDebugClientAssertionPage() {
        currentUserSession.set(
                User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                Tenant.COMUNE_DI_MILANO
        );

        WebPresentationGateway webPresentationGateway =
                webPresentationGatewayProvider.getObject();

        DebugClientAssertionPage page =
                webPresentationGateway.bind(DebugClientAssertionPage.class);

        page.navigateTo();
        page.assertLoaded();

        return page;
    }
}