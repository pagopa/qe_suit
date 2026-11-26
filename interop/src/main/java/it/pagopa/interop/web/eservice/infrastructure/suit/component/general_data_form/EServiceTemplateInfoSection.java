package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard.Field;

@XPath(".//*[contains(@class, 'MuiPaper-root') and .//h2[normalize-space(.)='Informazioni sul template']]")
public interface EServiceTemplateInfoSection extends Component {

    String TITLE = "Informazioni sul template";
    String DESCRIPTION = "Queste informazioni sono state precompilate da chi ha creato il template e non sono modificabili.";
    String TEMPLATE_NAME_LABEL = "Nome del template di e-service";
    String INTENDED_TARGET_LABEL = "A chi è rivolto questo template?";
    String ALLOW_WHAT_LABEL = "Cosa permette di fare questo template?";

    @XPath(".//h2")
    Label title();

    @XPath(".//h2/following::p[1]")
    Label description();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + TEMPLATE_NAME_LABEL + "']]")
    Field templateName();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + INTENDED_TARGET_LABEL + "']]")
    Field intendedTarget();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + ALLOW_WHAT_LABEL + "']]")
    Field allowWhat();
}

