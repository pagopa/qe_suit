package it.pagopa.interop.web.eservice.infrastructure.page.component.consumer_purpose_base;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposePreliminaryInformationSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeTemplateSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

public interface ConsumerPurposeCreateBase {
    Breadcrumbs breadcrumbs();

    @XPath(".//a[contains(.,'Torna alle finalità')]")
    Button backToPurposesButton();

    @XPath(".//h1")
    it.frontend.e2e.framework.web.capability.core.Readable<String> pageTitle();

    @XPath(".//p[normalize-space()='*Campi obbligatori']")
    Label mandatoryFieldsLabel();

    @XPath(".//section[.//h2[normalize-space()='Informazioni preliminari']]")
    PurposePreliminaryInformationSection preliminaryInformationSection();

    @XPath(".//section[.//h2[normalize-space()='Compilazione agevolata della finalità']]")
    PurposeTemplateSection purposeTemplateSection();

    @XPath(".//button[normalize-space()='Crea bozza']")
    Button createDraftButton();

}
