package it.pagopa.interop.suite.contract;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.UUID;
import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
                "channel.web.headless=true",
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPurposeTemplateContractTest {

    private final WebBrowserContractValidator webContractValidator;

    private void run(WebScenario<PurposeTemplateCatalogPage> scenario) throws Throwable {
        DynamicTest dynamicTest = webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(PurposeTemplateCatalogPage.class)
                .tests(Stream.of(scenario))
                .findFirst()
                .orElseThrow();

        dynamicTest.getExecutable().execute();
    }

    private void assertLandedOnCreatedTemplate(PurposeTemplateCatalogPage page) {
        Assertions.assertThat(page.pageTitle().read())
                .as("Page title is not blank")
                .isNotBlank();

        UUID templateId = page.currentTemplateIdFromUrl();
        Assertions.assertThat(templateId)
                .as("Landed on the created purpose template's page")
                .isNotNull();

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
                    .as("Free-of-charge switch is unchecked by default")
                    .isFalse();

            softly.assertThat(purposeTemplateCreationPage.freeOfChargeExplanationField()
                    .flatMap(field -> field.get()))
                    .as("Free-of-charge explanation field is absent when switch is off")
                    .isEmpty();
        });
    }

    @Test
    void shouldCreatePurposeTemplateAndLandOnConfirmationPage() throws Throwable {
        run(new WebScenario<>(
                "creating purpose template with personal data treatment enabled",
                page -> page.openCreationForm().setPersonalData(true).confirmCreation(),
                this::assertLandedOnCreatedTemplate
        ));
    }

    @Test
    void shouldCreatePurposeTemplateWithoutPersonalDataAndLandOnConfirmationPage() throws Throwable {
        run(new WebScenario<>(
                "creating purpose template with personal data treatment disabled",
                page -> page.openCreationForm().setPersonalData(false).confirmCreation(),
                this::assertLandedOnCreatedTemplate
        ));
    }
}

