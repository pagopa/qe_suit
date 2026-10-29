package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form.AdditionalInfoForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_card.*;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.GeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form.TechnicalSpecsForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form.ThresholdAndAttributeForm;

import static org.assertj.core.api.Assertions.assertThat;


@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreatePage extends Page {
    String BACK_TO_ESERVICE = "Torna agli e-service";
    String BREADCRUMB_EROGAZIONE = "Erogazione";
    String BREADCRUMB_MY_ESERVICES = "I miei e-service";
    String BREADCRUMB_CURRENT = "Crea e-service";
    String PAGE_TITLE = "Crea e-service";
    String GENERAL_INFO = "Informazioni generali";

    String CARD_INFO_TITLE = "Informazioni sull’e-service";
    String CARD_INFO_DESCRIPTION = "Per scegliere nomi chiari e rendere gli e-service facili da trovare, consulta le linee guida";
    String CARD_INFO_NAME_LABEL = "Nome dell’e-service";
    String CARD_INFO_DESCRIPTION_LABEL = "Descrizione dell’e-service";
    String CARD_INFO_NAME_HELPER = "Min 5 caratteri, max 60 caratteri";
    String CARD_INFO_DESC_HELPER = "Descrivi quali dati il fruitore deve fornire e quali dati l’e-service restituisce. Min 10 caratteri, max 400 caratteri";

    String CARD_DETAILS_TITLE = "Dettagli dell'e-service";
    String CARD_DETAILS_ALERT = "Questi dati non saranno più modificabili dopo la pubblicazione della prima versione dell’e-service.";
    String CARD_DETAILS_EXCHANGE_MODE_QUESTION = "In che modo vuoi scambiare i dati dell’e-service?";
    String CARD_DETAILS_TECHNOLOGY_QUESTION = "Con quale tecnologia è costruita la tua API?";
    String CARD_DETAILS_DATA_FLOW_QUESTION = "L’e-service eroga o riceve dati?";
    String CARD_DETAILS_PERSONAL_DATA_QUESTION = "L’e-service eroga dati personali?";
    String CARD_DETAILS_SYNC_LABEL = "Sincrono (standard)";
    String CARD_DETAILS_ASYNC_LABEL = "Asincrono / massivo (in differita)";
    String CARD_DETAILS_REST_LABEL = "REST";
    String CARD_DETAILS_SOAP_LABEL = "SOAP";
    String CARD_DETAILS_DELIVER_LABEL = "Eroga";
    String CARD_DETAILS_RECEIVE_LABEL = "Riceve";
    String CARD_DETAILS_PERSONAL_DATA_YES_LABEL = "Eroga dati personali";
    String CARD_DETAILS_PERSONAL_DATA_NO_LABEL = "Non eroga dati personali";

    String CARD_DELEGATIONS_TITLE = "Deleghe";
    String CARD_DELEGATIONS_DESCRIPTION = "Consulta la documentazione relativa al funzionamento delle deleghe";
    String CARD_DELEGATIONS_CONSUMER_DELEGABLE_TITLE = "L’e-service può essere fruito tramite delega?";
    String CARD_DELEGATIONS_CLIENT_ACCESS_DELEGABLE_TITLE = "Autorizzo l’accesso ai dati in delega";
    String CARD_DELEGATIONS_CONSUMER_DELEGABLE_LABEL = "Autorizzo che i fruitori dell’e-service possano delegare altri enti ad iscriversi per conto proprio";
    String CARD_DELEGATIONS_CLIENT_ACCESS_DELEGABLE_LABEL = "Autorizzo che i delegati possano associare i propri client alle finalità che hanno creato in qualità di delegati alla fruizione. Consulta la documentazione per approfondire";

    String CARD_SIGNAL_HUB_TITLE = "Signal Hub: servizio di notifica della variazione dei dati";
    String CARD_SIGNAL_HUB_DESCRIPTION = "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati.";
    String CARD_SIGNAL_HUB_DESIGNATION_DESCRIPTION = "Attivando la funzionalità designi PagoPa S.p.A. responsabile del trattamento dei dati facendo proprio il documento di designazione di PagoPa S.p.A. come responsabile del trattamento dei dati personali per \"Signal Hub\".";
    String CARD_SIGNAL_HUB_LABEL = "Notificherò su Signal Hub le variazioni dei dati";

    String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";

    @XPath(".//h1")
    Label title();

    @XPath("//a[contains(@class, 'MuiButton-root') and contains(., '" + BACK_TO_ESERVICE + "')]")
    Button backButton();

    @XPath("//nav[contains(@class, 'MuiBreadcrumbs-root')]//a[normalize-space()='" + BREADCRUMB_EROGAZIONE + "']")
    Link breadcrumbErogazione();

    @XPath("//nav[contains(@class, 'MuiBreadcrumbs-root')]//a[normalize-space()='" + BREADCRUMB_MY_ESERVICES + "']")
    Link breadcrumbMyEServices();

    @XPath("//nav[contains(@class, 'MuiBreadcrumbs-root')]//li[contains(@class, 'MuiBreadcrumbs-li')]//span[normalize-space()='" + BREADCRUMB_CURRENT + "']")
    Label breadcrumbCurrentPage();

    @XPath("//h2[normalize-space()='" + CARD_INFO_TITLE + "']/ancestor::div[contains(@class, 'MuiPaper-root')][1]")
    InformationCard infoCard();

    @XPath("//h2[contains(translate(., '’', \"'\"), \"" + CARD_DETAILS_TITLE + "\")]/ancestor::section[contains(@class, 'MuiPaper-root')][1]")
    DetailsCard detailsCard();

    @XPath("//h2[contains(., '" + CARD_DELEGATIONS_TITLE + "')]/ancestor::div[contains(@class, 'MuiPaper-root')][1]")
    DelegationsCard delegationsCard();

    @XPath("//h2[contains(., '" + CARD_SIGNAL_HUB_TITLE + "')]/ancestor::div[contains(@class, 'MuiPaper-root')][1]")
    SignalHubCard signalHubCard();

    @XPath("//button[@type='submit' and contains(., '" + SUBMIT_BTN_TEXT + "')]")
    Button saveDraftAndProceedButton();

    GeneralDataForm generalDataForm();

    ThresholdAndAttributeForm thresholdAndAttributeForm();

    TechnicalSpecsForm technicalSpecsForm();

    AdditionalInfoForm additionalInfoForm();

    @XPath("//*[@data-testid='desktopWizardStepper']")
    Stepper upsertStepper();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Crea e-service");
        assertThat(upsertStepper().get()).as("The stepper must be presents").isPresent();
    }
}
