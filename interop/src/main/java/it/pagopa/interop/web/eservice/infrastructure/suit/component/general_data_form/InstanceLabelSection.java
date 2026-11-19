package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[contains(@class, 'MuiPaper-root') and .//h2[normalize-space(.)='Parola che identifica l’e-service']]")
public interface InstanceLabelSection extends Component {

    String TITLE = "Parola che identifica l’e-service";
    String DESCRIPTION = "Aggiungi una parola che aiuta a identificare il tuo e-service dagli altri creati dallo stesso template: è utile per avere un nome unico ed evitare duplicati.";
    String FIELD_LABEL = "Parola identificativa dell’e-service";
    String HELPER_TEXT = "Max 12 caratteri";
    String CATALOG_PREVIEW_LABEL = "Come comparirà nel catalogo";

    @XPath(".//h2")
    Label title();

    @XPath(".//h2/following::p[1]")
    Label description();

    @XPath(".//label[@for='instanceLabel']")
    Label instanceLabelFieldLabel();

    @XPath(".//*[@id='instanceLabel']")
    TextField instanceLabelInput();

    @XPath(".//*[@id='instanceLabel-infoLabel']")
    Label instanceLabelHelperText();

    @XPath(".//p[normalize-space(.)='" + CATALOG_PREVIEW_LABEL + "']")
    Label catalogPreviewLabel();

    @XPath(".//p[normalize-space(.)='" + CATALOG_PREVIEW_LABEL + "']/following-sibling::p[1]")
    Label catalogPreviewValue();
}

