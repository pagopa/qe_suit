package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface AgreementDetailsGeneralInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='Richiesta per l’e-service']")
    Label eServiceLabel();

    @XPath(".//a[contains(@href,'/catalogo-e-service/')]")
    Label eServiceLink();

    @XPath(".//p[normalize-space()='Erogatore']")
    Label providerLabel();

    @XPath(".//p[normalize-space()='Erogatore']/ancestor::div[1]/following-sibling::div//span")
    Label providerValue();

    @XPath(".//p[normalize-space()='Fruitore']")
    Label consumerLabel();

    @XPath(".//p[normalize-space()='Fruitore']/ancestor::div[1]/following-sibling::div//span")
    Label consumerValue();

    @XPath(".//button[@data-testid='download-agreement-document-button']")
    Button downloadAgreementButton();

    @XPath(".//button[@data-testid='download-agreement-document-button']//span[last()]")
    Label downloadAgreementButtonLabel();

    @XPath(".//button[.//span[normalize-space()='Visualizza attributi certificati']]")
    Button certifiedAttributesButton();

    @XPath(".//button[.//span[normalize-space()='Visualizza contatti erogatore']]")
    Button providerContactsButton();
}