package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPageGeneralInfo extends Component {
    @XPath("./div[1]//h2")
    Label generalInfoTitle();

    @XPath(".//*[normalize-space()='Erogatore' and not(*)]")
    Label producerLabel();

    @XPath(".//p[normalize-space()='Erogatore']/parent::div/following-sibling::div[1]//span")
    Label producerValue();

    @XPath(".//*[normalize-space()='Template in uso' and not(*)]")
    Label templateInUseLabel();

    @XPath(".//*[normalize-space()='Stai vedendo la versione' and not(*)]")
    Label descriptorVersionLabel();

    @XPath(".//div[./div/p[normalize-space()='Stai vedendo la versione']]/div[2]//span")
    Label descriptorVersionValue();

    @XPath(".//*[normalize-space()='Eroga dati personali' and not(*)]")
    Label personalDataLabel();

    @XPath(".//div[./div/p[normalize-space()='Eroga dati personali']]/div[2]//span")
    Label personalDataValue();

    @XPath(".//*[normalize-space()='Scambio dati' and not(*)]")
    Label exchangeTypeLabel();

    @XPath(".//div[./div/p[normalize-space()='Scambio dati']]/div[2]//span")
    Label exchangeTypeValue();

    @XPath(".//*[normalize-space()=\"Descrizione dell'e-service\" and not(*)]")
    Label eserviceDescriptionLabel();

    @XPath(".//div[./div/p[normalize-space()=\"Descrizione dell'e-service\"]]/div[2]//span")
    Label eserviceDescriptionValue();

    @XPath(".//*[normalize-space()='Descrizione della versione' and not(*)]")
    Label descriptorDescriptionLabel();

    @XPath(".//div[./div/p[normalize-space()='Descrizione della versione']]/div[2]//span")
    Label descriptorDescriptionValue();

    @XPath(".//h3[normalize-space()='Fruizione tramite delega']")
    Label delegationSectionTitle();

    @XPath(".//*[normalize-space()='Autorizzazione' and not(*)]")
    Label consumerDelegableLabel();

    @XPath(".//div[./div/p[normalize-space()='Autorizzazione']]/div[2]//span")
    Label consumerDelegableValue();

    @XPath(".//*[normalize-space()='Autorizzazione all’associazione dei client' and not(*)]")
    Label clientAccessDelegableLabel();

    @XPath(".//div[./div/p[normalize-space()=\"Autorizzazione all’associazione dei client\"]]/div[2]//span")
    Label clientAccessDelegableValue();

    // Bottom actions are IconLink (MUI Link, rendered as <button> when component='button')
    @XPath(".//*[(self::a or self::button)][normalize-space()='Vedi i dettagli tecnici dell’e-service']")
    Label showTechnicalDetailsAction();

    @XPath(".//*[(self::a or self::button)][normalize-space()='Vedi i dettagli sullo scambio asincrono']")
    Label showAsyncExchangeDetailsAction();

    @XPath(".//*[(self::a or self::button)][normalize-space()='Visualizza i contatti dell’erogatore']")
    Label showProducerContactsAction();

    // Closed MUI drawers are not mounted: any mounted drawer is an open one
    @XPath("//*[contains(@class, 'MuiDrawer-root')]")
    Label openDrawer();
}
