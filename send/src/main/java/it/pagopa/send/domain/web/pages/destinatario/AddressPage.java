package it.pagopa.send.domain.web.pages.destinatario;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.AbstractPage;
import it.frontend.e2e.framework.web.domain.Component;

public interface AddressPage extends AbstractPage {
    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div[1]/div[2]/button")
    Clickable addLegalAddressButton();

    @XPath("//*[@id=\"emailContactSection\"]/div[1]/div[2]/div/button")
    Clickable disableCourtesyAddress();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div[1]/div[1]/div[2]/div/button[2]")
    Clickable disableDigitalDomicile();

    interface RemoveDigitalDomicilePopup extends Component {

        @XPath("//*[@id=\"buttonAnnulla\"]")
        Clickable disableDigitalDomicile();

        @XPath("//*[@id=\"buttonConferma\"]")
        Clickable discard();

        default void confirmRemoveDigitalDomicile() {
            disableDigitalDomicile().click();
        }

        default void discardCourtesyAddress() {
            discard().click();
        }
    }



    interface RemoveCourtesyAddressPopup extends Component {

        @XPath("//*[@id=\"buttonConferma\"]")
        Clickable confirm();

        @XPath("//*[@id=\"buttonAnnulla\"]")
        Clickable discard();

        default void confirmRemoveCourtesyAddress() {
            confirm().click();
        }

        default void discardCourtesyAddress() {
            discard().click();
        }
    }

    RemoveCourtesyAddressPopup removeCourtesyAddressPopup();

    RemoveDigitalDomicilePopup removeDigitalDomicilePopup();

    default void addLegalAddress() {
        addLegalAddressButton().click();
    }

    default void removeCourtesyAddress() {
        try {
            disableCourtesyAddress().click();
            removeCourtesyAddressPopup().confirmRemoveCourtesyAddress();
        } catch (Exception e) {

        }
    }

    default void removeDigitalDomicile() {
        try {
            disableDigitalDomicile().click();
            removeDigitalDomicilePopup().confirmRemoveDigitalDomicile();
        } catch (Exception e) {

        }
    }
}
