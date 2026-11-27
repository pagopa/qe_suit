package it.pagopa.interop.web.purpose.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps.AssignmentStep;
import it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps.GeneralInfoStep;
import it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps.RiskAnalysisStep;
import it.pagopa.utils.async.PollingUtils;

@Url(" ${interop.web.base-url}/fruizione/finalita/${purposeId}/modifica")
public interface ConsumerPurposeEditPage extends Page {

    @XPath(".//a[contains(@class, 'MuiButton-root') and normalize-space()='Torna alle finalità']")
    Link headerBackToPurposesLink();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1[contains(@class, 'MuiTypography-h4') and normalize-space()='Modifica finalità']")
    Label pageTitle();

    @XPath(".//p[contains(@class, 'MuiTypography-body1') and normalize-space()='*Campi obbligatori']")
    Label mandatoryFieldsNote();

    Stepper stepper();

    GeneralInfoStep generalInfoStep();

    AssignmentStep assignmentStep();

    RiskAnalysisStep riskAnalysisStep();

    @XPath(".//form/div[contains(@class, 'MuiStack-root')]//a[normalize-space()='Torna alle finalità']")
    Button backToPurposesBtn();

    @XPath(".//button[@type='submit' and normalize-space()='Salva bozza e prosegui' and .//*[@data-testid='SaveIcon']]")
    Button saveDraftAndContinueButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> {
                    String title = pageTitle().read();
                    return title != null && !title.isBlank();
                },
                isLoaded -> isLoaded,
                "Purpose edit page did not load within the expected time"
        );
    }
}