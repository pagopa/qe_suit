package it.pagopa.interop.ui.domain.page.eservice_creation.step.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.ui.domain.component.Alert;
import it.pagopa.interop.ui.domain.component.Checkbox;
import it.pagopa.interop.ui.domain.component.InterfaceComponent;
import it.pagopa.interop.ui.domain.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface AsyncComponent extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//h2/following::p[1]")
    Readable<String> subtitle();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Questi campi non saranno più modificabili dopo la pubblicazione della prima versione dell’e-service.')]")
    Alert alert();

    @XPath("//*[@id=\"asyncExchangeProperties.responseTime\"]")
    TextField responseTime();

    @XPath("//*[@id=\"asyncExchangeProperties.resourceAvailableTime\"]")
    TextField resourceAvailableTime();

    @XPath("//*[@id=\"asyncExchangeProperties.maxResultSet\"]")
    TextField maxResultSet();

    @XPath("//label[.//span[text()='Richiedi conferma di ricezione']]//span[contains(@class, 'MuiCheckbox-root')]")
    Checkbox confirmation();

    @XPath("//label[.//span[text()='Consenti download a blocchi']]//span[contains(@class, 'MuiCheckbox-root')]")
    Checkbox bulk();

    InterfaceComponent callbackInterface();


    default String getResponseTimeInputHelperText(){
        return responseTime().getHelperText("asyncExchangeProperties.responseTime-infoLabel");
    }

    default String getResponseTimeInputErrorText(){
        return responseTime().getErrorMessage("asyncExchangeProperties.responseTime-error");
    }

    default String getResourceAvailableTimeInputHelperText(){
        return resourceAvailableTime().getHelperText("asyncExchangeProperties.resourceAvailableTime-infoLabel");
    }

    default String getResourceAvailableTimeInputErrorText(){
        return resourceAvailableTime().getErrorMessage("asyncExchangeProperties.resourceAvailableTime-error");
    }

    default String getMaxResultSetInputHelperText(){
        return maxResultSet().getHelperText("asyncExchangeProperties.maxResultSet-infoLabel");
    }

    default String getMaxResultSetInputErrorText(){
        return maxResultSet().getErrorMessage("asyncExchangeProperties.maxResultSet-error");
    }

    default String getConfirmationInputHelperText(){
        return confirmation().getHelperText("asyncExchangeProperties.confirmation-infoLabel");
    }

    default String getBulkInputHelperText(){
        return bulk().getHelperText("asyncExchangeProperties.bulk-infoLabel");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(title().read())
                    .isEqualTo("Scambi asincroni e massivi");

            softly.assertThat(subtitle().read())
                    .isEqualTo("Gli scambi asincroni e massivi richiedono un’interfaccia di callback che i fruitori dovranno implementare per ricevere le tue risposte. Carica il file OpenAPI con la descrizione dell’API. Scopri di più sui scambi asincroni e massivi.");

            softly.assertThat(alert().isWarning())
                    .isTrue();

            softly.assertThat(alert().message().read())
                    .contains("Questi campi non saranno più modificabili dopo la pubblicazione della prima versione dell’e-service.");

            softly.assertThat(getResponseTimeInputHelperText())
                    .isEqualTo("Indica in quanto tempo il tuo e-service restituisce una risposta dopo la chiamata dei fruitori.");

            softly.assertThat(getResourceAvailableTimeInputHelperText())
                    .isEqualTo("Indica per quanto tempo il dato è scaricabile prima di essere cancellato.");

            softly.assertThat(getMaxResultSetInputHelperText())
                    .isEqualTo("Indica quanti elementi può contenere al massimo ogni risposta.");

            softly.assertThat(getConfirmationInputHelperText())
                    .isEqualTo("Il fruitore dovrà inviare una notifica dopo aver scaricato il file.");

            softly.assertThat(getBulkInputHelperText())
                    .isEqualTo("Il fruitore può scaricare file di grandi dimensioni in parti separate.");
        });
    }
}
