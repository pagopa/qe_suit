package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.DelegationsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateDetailsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateInfoSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.InstanceLabelSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.SignalHubSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/erogazione/e-service/template-eservice/${eServiceTemplateId}/crea")
public interface EServiceCreateFromTemplatePage extends Page {

    String PAGE_TITLE = "Crea e-service da template";
    String BTN_BACK_ESERVICE = "Torna agli e-service";
    String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";

    @XPath("//a[contains(., '" + BTN_BACK_ESERVICE + "')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label title();

    @XPath(".//h1/following-sibling::p[1] | .//h1/parent::div[not(./p)]")
    Label templateDescription();

    @XPath(".//h1/parent::div//a[1]")
    Link templateLink();

    @XPath(".//*[@data-testid='desktopWizardStepper']")
    Stepper upsertStepper();

    EServiceTemplateInfoSection templateInfoSection();

    EServiceTemplateDetailsSection templateDetailsSection();

    DelegationsSection delegationsSection();

    SignalHubSection signalHubSection();

    InstanceLabelSection instanceLabelSection();

    @XPath(".//form//button[@type='submit' and contains(., '" + SUBMIT_BTN_TEXT + "')]")
    Button saveDraftButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> isVisibleWithText(title(), PAGE_TITLE)
                        && isVisibleWithText(templateInfoSection().title(), EServiceTemplateInfoSection.TITLE)
                        && isVisibleWithText(templateDetailsSection().title(), EServiceTemplateDetailsSection.TITLE)
                        && isVisibleWithText(delegationsSection().title(), "Deleghe")
                        && isVisibleWithText(signalHubSection().title(), "Signal Hub: servizio di notifica della variazione dei dati")
                        && isVisibleWithText(instanceLabelSection().title(), InstanceLabelSection.TITLE),
                Boolean::booleanValue,
                "La pagina Crea e-service da template e le sue 5 sezioni non sono state caricate correttamente"
        );
    }

    private static boolean isVisibleWithText(Label label, String expectedText) {
        return label.get(Gettable.FindPolicy.VISIBLE)
                .map(el -> expectedText.equals(el.getText().trim()))
                .orElse(false);
    }
}

