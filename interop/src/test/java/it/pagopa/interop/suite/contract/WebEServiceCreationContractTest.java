package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * QA-18085 visual contract for the initial E-service creation form.
 * Checks the rendered title, required field guidance and initial radio state.
 * Layout, colours, fonts, screenshots and business submission are excluded.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceCreationContractTest {
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> initialEServiceCreationVisualContract() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceCreationPage.class)
                .tests(Stream.of(
                        new WebScenario<>("Visual - creation form headings and guidance", page -> {},
                                WebEServiceCreationContractTest::assertFormContent),
                        new WebScenario<>("Visual - creation form initial selections", page -> {},
                                WebEServiceCreationContractTest::assertInitialSelections)
                ));
    }

    private static void assertFormContent(EServiceCreationPage page) {
        page.assertLoaded();
        Assertions.assertThat(page.title().read()).isEqualTo("Crea e-service");
        Assertions.assertThat(page.generalDataStep().getNameHelperText())
                .isEqualTo("Min 5 caratteri, max 60 caratteri");
        Assertions.assertThat(page.generalDataStep().getDescriptionHelperText())
                .isEqualTo("Descrivi quali dati il fruitore deve fornire e quali dati l’e-service restituisce. Min 10 caratteri, max 400 caratteri");
        Assertions.assertThat(page.saveDraftButton().get(FindPolicy.PRESENT).orElseThrow().getText())
                .isEqualTo("Salva bozza e prosegui");
    }

    private static void assertInitialSelections(EServiceCreationPage page) {
        var generalData = page.generalDataStep();
        Assertions.assertThat(generalData.getAsyncExchange()).as("Exchange mode").isFalse();
        Assertions.assertThat(generalData.getTechnology()).hasToString("REST");
        Assertions.assertThat(generalData.getMode()).hasToString("DELIVER");
        Assertions.assertThat(generalData.getPersonalData()).as("Personal data option").isNull();
    }
}
