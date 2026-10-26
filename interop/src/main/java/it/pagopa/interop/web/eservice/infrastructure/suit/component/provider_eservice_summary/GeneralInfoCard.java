package it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary;

import it.frontend.e2e.framework.annotation.selector.XPath;

public interface GeneralInfoCard extends EServiceSummaryCard {

    String DESCRIPTION = "Descrizione";
    String DATA_EXCHANGE = "Scambio dati";
    String API_TECHNOLOGY = "Tecnologia API";
    String PERSONAL_DATA = "Eroga dati personali";
    String CONSUMER_DELEGABLE = "L’e-service può essere fruito tramite delega?";
    String CLIENT_ACCESS_DELEGABLE = "Consento l’accesso ai dati in delega?";
    String SIGNAL_HUB = "È attivo il servizio di notifiche sulla variazione dati (Signal Hub)?";

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    GeneralInfoContent content();

    interface GeneralInfoContent extends Content {

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + DESCRIPTION + "']]")
        Field description();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + DATA_EXCHANGE + "']]")
        Field dataExchange();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + API_TECHNOLOGY + "']]")
        Field apiTechnology();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + PERSONAL_DATA + "']]")
        Field personalData();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + CONSUMER_DELEGABLE + "']]")
        Field consumerDelegable();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + CLIENT_ACCESS_DELEGABLE + "']]")
        Field clientAccessDelegable();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + SIGNAL_HUB + "']]")
        Field signalHub();
    }
}
