package it.pagopa.interop.web.eservice_template.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.infrastructure.suit.component.TextField;

/**
 * Step 1 del wizard di creazione Template E-service: dati generali.
 * Mirror minimale di {@link it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard},
 * limitato ai soli campi effettivamente presenti nel DOM del flusso Template
 * (nessun {@code asyncExchange}/{@code mode}: non presenti in questo form).
 */
public interface TemplateGeneralDataWizard extends Component {

    @XPath(".//*[@id=\"name\"]")
    TextField name();

    @XPath(".//*[@id=\"intendedTarget\"]")
    TextField intendedTarget();

    @XPath(".//*[@id=\"description\"]")
    TextField description();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='REST'] and .//*[text()='SOAP']]")
    RadioGroup technology();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga dati personali'] and .//*[text()='Non eroga dati personali']]")
    RadioGroup personalData();

    default TemplateGeneralDataWizard setName(String templateName) {
        name().writeAndAssert(templateName);
        return this;
    }

    default TemplateGeneralDataWizard setIntendedTarget(String intendedTargetValue) {
        intendedTarget().writeAndAssert(intendedTargetValue);
        return this;
    }

    default TemplateGeneralDataWizard setDescription(String templateDescription) {
        description().writeAndAssert(templateDescription);
        return this;
    }

    default TemplateGeneralDataWizard setTechnology(EServiceTechnology templateTechnology) {
        technology().selectLike(templateTechnology.getValue());
        return this;
    }

    default TemplateGeneralDataWizard setPersonalData(Boolean processingPersonalData) {
        if (processingPersonalData == null) return this;

        if (processingPersonalData) personalData().selectLike("Eroga");
        else personalData().selectLike("Non eroga");
        return this;
    }

    @Override
    default void assertLoaded() {
    }
}

