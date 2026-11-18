package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;

@XPath(".//form")
public interface EServiceTemplateGeneralDataForm extends Component {

    String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";

    AboutEServiceTemplateSection aboutEServiceTemplateSection();

    EServiceDetailsSection eServiceDetailsSection();

    EServiceTemplateSignalHubSection signalHubSection();

    @XPath(".//button[normalize-space(.)='" + SUBMIT_BTN_TEXT + "']")
    Button saveDraftButton();

    default EServiceTemplateGeneralDataForm setTemplateName(String name) {
        aboutEServiceTemplateSection().nameInput().fill(name);
        return this;
    }

    default EServiceTemplateGeneralDataForm setIntendedTarget(String intendedTarget) {
        aboutEServiceTemplateSection().intendedTargetInput().fill(intendedTarget);
        return this;
    }

    default EServiceTemplateGeneralDataForm setDescription(String description) {
        aboutEServiceTemplateSection().descriptionInput().fill(description);
        return this;
    }

    default EServiceTemplateGeneralDataForm setAsyncExchange(Boolean isAsync) {
        if (isAsync == null) return this;
        if (isAsync) {
            eServiceDetailsSection().asyncExchange().selectLike("Asincrono");
        } else {
            eServiceDetailsSection().asyncExchange().selectLike("Sincrono");
        }
        return this;
    }

    default EServiceTemplateGeneralDataForm setTechnology(EServiceTechnology eserviceTechnology) {
        eServiceDetailsSection().technology().selectLike(eserviceTechnology.getValue());
        return this;
    }

    default EServiceTemplateGeneralDataForm setMode(EServiceMode eserviceMode) {
        if (eserviceMode == null) return this;
        switch (eserviceMode) {
            case DELIVER -> eServiceDetailsSection().mode().selectLike("Eroga");
            case RECEIVE -> eServiceDetailsSection().mode().selectLike("Riceve");
        }
        return this;
    }

    default EServiceTemplateGeneralDataForm setUsePersonalData(boolean usePersonalData) {
        switch (getEServiceMode()) {
            case DELIVER -> eServiceDetailsSection().personalData()
                    .selectLike(usePersonalData ? "Eroga dati personali" : "Non eroga dati personali");
            case RECEIVE -> eServiceDetailsSection().personalData()
                    .selectLike(usePersonalData ? "Riceve dati personali" : "Non riceve dati personali");
        }
        return this;
    }

    default EServiceMode getEServiceMode() {
        return eServiceDetailsSection().mode().getSelected().contains("Eroga")
                ? EServiceMode.DELIVER
                : EServiceMode.RECEIVE;
    }

    default String getNameErrorText() {
        return aboutEServiceTemplateSection().nameInput().getErrorMessage("name-error");
    }

    default String getIntendedTargetErrorText() {
        return aboutEServiceTemplateSection().intendedTargetInput().getErrorMessage("intendedTarget-error");
    }

    default String getDescriptionErrorText() {
        return aboutEServiceTemplateSection().descriptionInput().getErrorMessage("description-error");
    }

    default EServiceTemplateGeneralDataForm saveDraft() {
        saveDraftButton().click();
        return this;
    }
}

