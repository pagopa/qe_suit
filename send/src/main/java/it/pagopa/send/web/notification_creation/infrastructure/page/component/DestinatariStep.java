package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.notification_creation.application.NotificationData;

/**
 * Passo del wizard "Invia una nuova notifica" usato dagli step Cucumber ({@code CreateNotificationPage}).
 */
public interface DestinatariStep extends Component {
    // Recipient type
    @XPath(".//*[@id='recipient-pf']")
    Clickable selectNaturalPerson();

    @XPath(".//*[@id='recipient-pg']")
    Clickable selectLegalPerson();

    // Main fields
    @XPath(".//input[@id='recipients[0].taxId']")
    Writable<String> taxId();

    @XPath(".//input[@id='recipients[0].firstName']")
    Writable<String> firstName();

    @XPath(".//input[@id='recipients[0].lastName']")
    Writable<String> lastName();

    // Address lookup
    @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][1]")
    Clickable selectNationalRegistry();

    @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][2]")
    Clickable selectManualAddress();

    // PEC — optional
    @XPath(".//input[@id='recipients[0].digitalDomicile']")
    Writable<String> pecAddress();

    default void fillFields(NotificationData data) {
        selectNaturalPerson().click();
        taxId().write(data.getTaxId());
        firstName().write(data.getFirstName());
        lastName().write(data.getLastName());
        selectNationalRegistry().click();
        if (data.getPec() != null && !data.getPec().isEmpty()) {
            pecAddress().write(data.getPec());
        }
    }
}
