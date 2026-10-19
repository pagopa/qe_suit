package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;


@XPath(".//form")
public interface GeneralDataForm extends Component {

    AboutEServiceSection aboutEServiceSection();

    EServiceDetailsSection eServiceDetailsSection();

    DelegationsSection delegationsSection();

    SignalHubSection signalHubSection();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    default GeneralDataForm setEServiceName(String name) {
        aboutEServiceSection().eServiceNameInput().fill(name);
        return this;
    }

    default GeneralDataForm setEServiceDescription(String description) {
        aboutEServiceSection().eServiceDescriptionInput().fill(description);
        return this;
    }

    default GeneralDataForm setUsePersonalData(boolean usePersonalData) {
        switch (getEServiceMode()) {
            case DELIVER -> eServiceDetailsSection().personalData().selectLike(usePersonalData ? "Eroga dati personali" : "Non eroga dati personali");
            case RECEIVE -> eServiceDetailsSection().personalData().selectLike(usePersonalData ? "Riceve dati personali" : "Non riceve dati personali");
        }
        return this;
    }

    default GeneralDataForm setAsyncExchange(Boolean isAsync) {
        if(isAsync == null) return this;

        if (isAsync) eServiceDetailsSection().asyncExchange().selectLike("Asincrono");
        else eServiceDetailsSection().asyncExchange().selectLike("Sincrono");

        return this;
    }

    default GeneralDataForm setTechnology(EServiceTechnology eserviceTechnology) {
        eServiceDetailsSection().technology().selectLike(eserviceTechnology.getValue());
        return this;
    }

    default GeneralDataForm setMode(EServiceMode eserviceMode) {
        if (eserviceMode == null) return this;
        switch (eserviceMode) {
            case DELIVER -> eServiceDetailsSection().mode().selectLike("Eroga");
            case RECEIVE -> eServiceDetailsSection().mode().selectLike("Riceve");
        }
        return this;
    }

    default String getEServiceName() {
        return aboutEServiceSection().eServiceNameInput().read();
    }

    default String getEServiceDescription() {
        return aboutEServiceSection().eServiceDescriptionInput().read();
    }

    default boolean getUsePersonalData(){
        return eServiceDetailsSection().personalData().getSelected().startsWith("Eroga") || eServiceDetailsSection().personalData().getSelected().startsWith("Riceve");
    }

    default boolean getAsyncExchange(){
        String val = eServiceDetailsSection().asyncExchange().getSelected();
        return val != null && !val.isBlank() && val.contains("Asincrono");
    }

    default EServiceTechnology getEServiceTechnology() {
        return EServiceTechnology.valueOf(eServiceDetailsSection().technology().getSelected().toUpperCase());
    }

    default EServiceMode getEServiceMode() {
        return eServiceDetailsSection().mode().getSelected().contains("Eroga") ? EServiceMode.DELIVER : EServiceMode.RECEIVE;
    }

    default String getNameHelperText() {
        return aboutEServiceSection().eServiceNameInput().getHelperText("name-infoLabel");
    }

    default String getNameErrorText() {
        return aboutEServiceSection().eServiceNameInput().getErrorMessage("name-error");
    }

    default String getDescriptionHelperText() {
        return aboutEServiceSection().eServiceDescriptionInput().getHelperText("description-infoLabel");
    }

    default String getDescriptionErrorText() {
        return aboutEServiceSection().eServiceDescriptionInput().getErrorMessage("description-error");
    }

    default GeneralDataForm saveDraft(){
        saveDraftButton().click();
        return this;
    }
}
