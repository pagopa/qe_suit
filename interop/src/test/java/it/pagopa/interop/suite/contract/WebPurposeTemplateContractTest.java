package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCreationPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {
                "channel.web.browser=chrome",
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPurposeTemplateContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> createPurposeTemplate() {
        var purposeTemplateId = interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.COMUNE_DI_MILANO)
                .createPurposeTemplate(Tenant.COMUNE_DI_MILANO, PurposeTemplateState.DRAFT)
                .get(PurposeTemplate.class)
                .getId();

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(PurposeTemplateCreationPage.class, purposeTemplateId.toString())
                .tests(createPurposeTemplateScenarios());
    }

    private Stream<WebScenario<PurposeTemplateCreationPage>> createPurposeTemplateScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "purpose template form displays its main fields",
                        page -> {
                        },
                        (page) -> SoftAssertions.assertSoftly(softly -> {
                            softly.assertThat(page.nameField().read())
                                    .as("Purpose template name is not blank")
                                    .isNotBlank();
                            softly.assertThat(page.descriptionField().read())
                                    .as("Purpose template description is readable")
                                    .isNotNull();
                            softly.assertThat(page.freeOfChargeSwitch().get())
                                    .as("Free-of-charge switch is present")
                                    .isPresent();
                        })
                ),
                new WebScenario<>(
                        "enabling the free-of-charge switch shows the explanation field",
                        page -> {
                            if (!page.freeOfChargeSwitch().isChecked()) page.freeOfChargeSwitch().click();
                        },
                        (page) -> {
                            SoftAssertions.assertSoftly(softly -> {
                                softly.assertThat(page.freeOfChargeSwitch().isChecked())
                                        .as("Free-of-charge switch is checked")
                                        .isTrue();
                                softly.assertThat(page.freeOfChargeExplanationField().exists())
                                        .as("Free-of-charge explanation field is present when switch is on")
                                        .isTrue();
                            });
                        }
                ),
                new WebScenario<>(
                        "disabling the free-of-charge switch hides the explanation field",
                        page -> {
                            if (page.freeOfChargeSwitch().isChecked()) page.freeOfChargeSwitch().click();
                        },
                        (page) -> SoftAssertions.assertSoftly(softly -> {
                            softly.assertThat(page.freeOfChargeSwitch().isChecked())
                                    .as("Free-of-charge switch is not checked")
                                    .isFalse();
                            softly.assertThat(page.freeOfChargeExplanationField().exists())
                                    .as("Free-of-charge explanation field is absent when switch is off")
                                    .isFalse();
                        })
                )
        );
    }
}
