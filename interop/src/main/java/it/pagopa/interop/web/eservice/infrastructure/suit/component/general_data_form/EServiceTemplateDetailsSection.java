package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard.Field;

@XPath(".//*[contains(@class, 'MuiPaper-root') and .//h2[normalize-space(.)=\"Dettagli dell'e-service\"]]")
public interface EServiceTemplateDetailsSection extends Component {

    String TITLE = "Dettagli dell'e-service";
    String DESCRIPTION = "Queste informazioni non sono modificabili.";
    String DATA_EXCHANGE_LABEL = "Scambio dati";
    String API_TECHNOLOGY_LABEL = "Tecnologia API";
    String MODE_LABEL = "L’e-service eroga o riceve dati?";
    String PERSONAL_DATA_LABEL = "Eroga dati personali";

    @XPath(".//h2")
    Label title();

    @XPath(".//h2/following::p[1]")
    Label description();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + DATA_EXCHANGE_LABEL + "']]")
    Field dataExchange();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + API_TECHNOLOGY_LABEL + "']]")
    Field apiTechnology();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + MODE_LABEL + "']]")
    Field mode();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + PERSONAL_DATA_LABEL + "']]")
    Field personalData();
}

