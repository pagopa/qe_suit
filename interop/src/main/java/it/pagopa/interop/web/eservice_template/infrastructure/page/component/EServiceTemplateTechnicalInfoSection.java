package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface EServiceTemplateTechnicalInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//h2/following::p[1]")
    Label sectionDescription();

    @XPath(".//p[normalize-space()='Tecnologia']")
    Label technologyLabel();

    @XPath(".//p[normalize-space()='Tecnologia']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label technologyValue();

    @XPath(".//p[normalize-space()='Tipologia e-service']")
    Label modeLabel();

    @XPath(".//p[normalize-space()='Tipologia e-service']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label modeValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Voucher']")
    Label voucherHeading();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Voucher']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editVoucherButton();

    @XPath(".//p[normalize-space()='Durata del voucher']")
    Label voucherLifespanLabel();

    @XPath(".//p[normalize-space()='Durata del voucher']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label voucherLifespanValue();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Documentazione']")
    Label documentationHeading();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Documentazione']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editDocumentationButton();

    @XPath(".//p[normalize-space()='Elenco documenti']")
    Label documentsListLabel();

    @XPath(".//div[normalize-space()='Elenco documenti']/parent::div/div[2]//button")
    Button apiSpecLink();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Link utili']")
    Label usefulLinksHeading();

    @XPath(".//a[normalize-space()='Come implemento e gestisco un template di e-service?']")
    Link implementationGuideLink();

    @XPath(".//a[normalize-space()='Come faccio ad aggiornare una istanza di e-service?']")
    Link updateInstanceGuideLink();

    @XPath(".//a[normalize-space()='URL .well-known']")
    Link wellKnownLink();
}

