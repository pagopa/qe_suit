package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface PurposeDetailsGeneralInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='E-service associato']")
    Label eServiceLabel();

    @XPath(".//a[contains(@href,'/catalogo-e-service/')]")
    Link eServiceLink();

    @XPath(".//p[normalize-space()='Erogatore']")
    Label providerLabel();

    @XPath(".//p[normalize-space()='Erogatore']/ancestor::div[1]/following-sibling::div//span")
    Label providerValue();

    @XPath(".//p[normalize-space()='Descrizione della finalità']")
    Label purposeDescriptionLabel();

    @XPath(".//p[normalize-space()='Descrizione della finalità']/ancestor::div[1]/following-sibling::div//span")
    Label purposeDescriptionValue();

    @XPath(".//*[@data-testid='download-risk-analysis-document-button']")
    Button downloadRiskAnalysisButton();

    @XPath(".//a[normalize-space()='Vai a richiesta di fruizione']")
    Link goToAgreementLink();
}