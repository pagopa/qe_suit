package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.purpose_template.infrastructure.suite.page.ConsumerPurposeTemplateCatalogPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposeTemplateCatalogPageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifies that the consumer purpose template catalog page shows the expected
     * breadcrumbs, title, description, and filter labels for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> pageMustBeCorrectlyLoaded() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeTemplateCatalogPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Consumer purpose template catalog page shows the expected content and filters",
                                purposeTemplateCatalogPage -> {
                                },
                                purposeTemplateCatalogPage -> {
                                    String path = purposeTemplateCatalogPage.breadcrumbs().getPath();
                                    Assertions.assertThat(path).as("The path in the breadcrumbs must be Fruizione/Template finalità").isEqualTo("Fruizione/Template finalità");

                                    String pageTitle = purposeTemplateCatalogPage.getTitle();
                                    Assertions.assertThat(pageTitle).as("The page title must be Compilazione agevolata delle finalità").isEqualTo("Compilazione agevolata delle finalità");

                                    String pageDescription = purposeTemplateCatalogPage.getDescription();
                                    Assertions.assertThat(pageDescription).as("The page description must be corrected").isEqualTo("I template delle finalità sono modelli condivisi tra tutti gli aderenti. Dopo che la tua richiesta di fruizione viene accettata, puoi creare una nuova finalità a partire da uno di questi modelli, che contiene una parte dell’analisi del rischio precompilata.");

                                    purposeTemplateCatalogPage.nameFilter().label().readAndAssert("Cerca per nome");
                                    purposeTemplateCatalogPage.creatorFilter().label().readAndAssert("Ente realizzatore");
                                    purposeTemplateCatalogPage.eServiceFilter().label().readAndAssert("Cerca per e-service");
                                    purposeTemplateCatalogPage.targetTenantKindFilter().label().readAndAssert("Ente destinatario");
                                }
                        )
                ));
    }

}
