package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/modifica")
public interface ProducerEServiceTemplateEditPage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    Stepper wizardStepper();

    @XPath(".//button[normalize-space()='Salva bozza e prosegui']")
    Button saveDraftAndContinueButton();

    @XPath(".//button[normalize-space()='Vai al riepilogo']")
    Button goToSummaryButton();

    ProducerEServiceTemplateSummaryPage summaryPage();

    default void proceedToSummary() {
        saveDraftAndContinueButton().click();
        waitForActiveStep(1, "Step 2 (Soglie e attributi) did not become active");

        saveDraftAndContinueButton().click();
        waitForActiveStep(2, "Step 3 (Specifiche tecniche) did not become active");

        saveDraftAndContinueButton().click();
        PollingUtils.pollUntil(
                () -> goToSummaryButton().get(Gettable.FindPolicy.CLICKABLE).isPresent(),
                Boolean::booleanValue,
                "Step 4 (Informazioni aggiuntive) did not show 'Vai al riepilogo'"
        );

        goToSummaryButton().click();
    }

    private void waitForActiveStep(int stepIndex, String errorMessage) {
        PollingUtils.pollUntil(
                () -> {
                    var steps = wizardStepper().steps();
                    if (steps.size() <= stepIndex) {
                        return false;
                    }
                    var step = steps.get(stepIndex);
                    return step.isActive() || step.isActiveOnLabel();
                },
                Boolean::booleanValue,
                errorMessage
        );
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Producer E-Service Template Edit Page is not loaded yet. Title is blank"
        );
        PollingUtils.pollUntil(
                () -> saveDraftAndContinueButton().get(Gettable.FindPolicy.CLICKABLE).isPresent(),
                Boolean::booleanValue,
                "Producer E-Service Template Edit Page 'Salva bozza e prosegui' button is not clickable"
        );
    }
}

