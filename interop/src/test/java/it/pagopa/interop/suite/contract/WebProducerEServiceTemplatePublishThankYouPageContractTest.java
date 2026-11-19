package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplatePublishThankYouPage;
import it.pagopa.interop.web.eservice_template.infrastructure.page.ProducerEServiceTemplateEditPage;
import it.pagopa.interop.web.eservice_template.infrastructure.page.ProducerEServiceTemplateSummaryPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
                "channel.web.headless=false"
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Web E-Service Template Publish Thank You Page Contract Test")
public class WebProducerEServiceTemplatePublishThankYouPageContractTest {

    private static final Tenant TENANT = Tenant.COMUNE_DI_MILANO;
    private static final String EXPECTED_CONFIRM_DIALOG_DESCRIPTION =
            "Una volta pubblicata, questa versione sarà disponibile nel catalogo dei template degli e-service e non potrà più essere cancellata. Potrai comunque sospenderla o renderla obsoleta quando pubblicherai una nuova versione.";
    private static final String EXPECTED_THANK_YOU_TITLE = "Hai pubblicato un template e-service";
    private static final String EXPECTED_THANK_YOU_DESCRIPTION =
            "Ora è disponibile nel catalogo template e-service e potrà essere usato dagli altri enti per creare i propri e-service.";
    private static final String EXPECTED_GO_TO_TEMPLATE_LABEL = "Vai al template";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final EServiceTemplateVersionUseCase eServiceTemplateVersionUseCase;

    @TestFactory
    Stream<DynamicTest> publishThankYouPageContract() {
        EServiceTemplate eServiceTemplate = prepareDraftTemplate();
        String templateId = eServiceTemplate.getId().toString();
        String versionId = eServiceTemplate.getLatestVersion().getId().toString();
        String expectedFeedbackPath = "/erogazione/template-eservice/" + templateId + "/" + versionId + "/feedback";

        return webContractValidator
                .as(User.getTenantAdmin(TENANT), TENANT)
                .on(ProducerEServiceTemplateEditPage.class, templateId, versionId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verifica della pagina di feedback dopo la pubblicazione di un template e-service",
                                editPage -> {
                                    editPage.proceedToSummary();

                                    ProducerEServiceTemplateSummaryPage summaryPage = editPage.summaryPage();
                                    summaryPage.assertLoaded();
                                    summaryPage.publishButton().click();

                                    var confirmDialog = summaryPage.publishConfirmDialog();
                                    confirmDialog.assertLoaded();
                                    Assertions.assertThat(confirmDialog.description().read())
                                            .as("Testo della modale di conferma pubblicazione")
                                            .contains(EXPECTED_CONFIRM_DIALOG_DESCRIPTION);
                                    confirmDialog.publishButton().click();

                                    ProducerEServiceTemplatePublishThankYouPage thankYouPage =
                                            summaryPage.publishThankYouPage();
                                    PollingUtils.pollUntil(
                                            thankYouPage::currentUrl,
                                            url -> url.endsWith(expectedFeedbackPath),
                                            "Redirect alla pagina di feedback post-pubblicazione non completato"
                                    );
                                    thankYouPage.assertLoaded();
                                },
                                editPage -> {
                                    ProducerEServiceTemplatePublishThankYouPage thankYouPage =
                                            editPage.summaryPage().publishThankYouPage();
                                    assertSoftly(softly -> {
                                        softly.assertThat(thankYouPage.currentUrl())
                                                .as("URL della pagina di feedback")
                                                .endsWith(expectedFeedbackPath);
                                        softly.assertThat(thankYouPage.checkIcon().get(Gettable.FindPolicy.VISIBLE))
                                                .as("TY-02 - Icona Check visibile")
                                                .isPresent();
                                        softly.assertThat(thankYouPage.title().read())
                                                .as("TY-03 - Titolo pagina")
                                                .isEqualTo(EXPECTED_THANK_YOU_TITLE);
                                        softly.assertThat(thankYouPage.description().read())
                                                .as("TY-04 - Descrizione pagina")
                                                .isEqualTo(EXPECTED_THANK_YOU_DESCRIPTION);
                                        softly.assertThat(thankYouPage.goToTemplateButton()
                                                        .get(Gettable.FindPolicy.CLICKABLE))
                                                .as("TY-05 - Pulsante Vai al template cliccabile")
                                                .isPresent();
                                        softly.assertThat(thankYouPage.goToTemplateButton().read())
                                                .as("TY-05 - Testo pulsante Vai al template")
                                                .isEqualTo(EXPECTED_GO_TO_TEMPLATE_LABEL);
                                    });
                                }
                        )
                ));
    }

    private EServiceTemplate prepareDraftTemplate() {
        EServiceTemplate eServiceTemplate = interopJourney
                .withProducer(TENANT, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.DRAFT)
                .get(EServiceTemplate.class);
        eServiceTemplateVersionUseCase.prepareVersionForPublishing(
                eServiceTemplate,
                eServiceTemplate.getLatestVersion()
        );
        return eServiceTemplate;
    }
}

