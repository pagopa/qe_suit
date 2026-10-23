package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.binder.WebBinder;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCreationPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPurposeTemplateContractTest {

    private final PurposeTemplateCreationPage purposeTemplateCreationPage;
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> createPurposeTemplate() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(PurposeTemplateCatalogPage.class)
                .tests(createPurposeTemplateScenarios());
    }

    private Stream<WebScenario<PurposeTemplateCatalogPage>> createPurposeTemplateScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "creating purpose template with personal data treatment enabled",
                        page -> page.openCreationForm().setPersonalData(true).confirmCreation(),
                        this::assertLandedOnCreatedTemplate
                ),
                new WebScenario<>(
                        "creating purpose template with personal data treatment disabled",
                        page -> page.openCreationForm().setPersonalData(false).confirmCreation(),
                        this::assertLandedOnCreatedTemplate
                )
        );
    }

    private void assertLandedOnCreatedTemplate(PurposeTemplateCatalogPage page) {
        Assertions.assertThat(page.pageTitle().read())
                .as("Page title is not blank")
                .isNotBlank();

        PurposeTemplateCreationPage purposeTemplateCreationPage = new WebBinder().bind(PurposeTemplateCreationPage.class);
        purposeTemplateCreationPage.assertLoaded();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(purposeTemplateCreationPage.nameField().read())
                    .as("Purpose template name starts with 'Template finalità'")
                    .startsWith("Template finalità");
            softly.assertThat(purposeTemplateCreationPage.descriptionField().read())
                    .as("Purpose template description is readable")
                    .isNotNull();
            softly.assertThat(purposeTemplateCreationPage.freeOfChargeSwitch().get())
                    .as("Free-of-charge switch is present")
                    .isPresent();
            softly.assertThat(purposeTemplateCreationPage.freeOfChargeSwitch().isChecked())
                    .as("Free-of-charge switch is checked by default")
                    .isTrue();
            softly.assertThat(purposeTemplateCreationPage.freeOfChargeExplanationField()
                    .flatMap(field -> field.get(FindPolicy.PRESENT)))
                    .as("Free-of-charge explanation field is present when switch is on")
                    .isPresent();
            // cambia lo stato dello switch
            purposeTemplateCreationPage.freeOfChargeSwitch().click();
            softly.assertThat(purposeTemplateCreationPage.freeOfChargeSwitch().isChecked())
                    .as("Free-of-charge switch is unchecked after click on it")
                    .isFalse();
            softly.assertThat(purposeTemplateCreationPage.freeOfChargeExplanationField()
                            .flatMap(field -> field.get(FindPolicy.PRESENT)))
                    .as("Free-of-charge explanation field is not present when switch is disabled")
                    .isNotPresent();
        });
    }
}
