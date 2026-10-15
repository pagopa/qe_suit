package it.pagopa.interop.web.eservice.infrastructure.page.detail;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
/**
 * Facilitated compilation (purpose templates) section.
 */
public interface EServicePurposeTemplatesLocators {
    String PURPOSE_TEMPLATES_SECTION = ".//section[./div[1]//h2[normalize-space()='Compilazione agevolata della finalità']]";
    // ----------------------------------------------- Facilitated compilation
    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]//h2")
    Label purposeTemplatesTitle();
    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]/p")
    Label purposeTemplatesDescription();
    @XPath(PURPOSE_TEMPLATES_SECTION + "//button[normalize-space()='Visualizza i template collegati']")
    Button viewLinkedPurposeTemplatesButton();
}