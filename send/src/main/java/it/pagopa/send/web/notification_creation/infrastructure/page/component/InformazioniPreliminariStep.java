package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.notification_creation.application.NotificationData;

import java.util.List;
import java.util.function.Predicate;

/**
 * Passo del wizard "Invia una nuova notifica" usato dagli step Cucumber ({@code CreateNotificationPage}).
 */
public interface InformazioniPreliminariStep extends Component {
    @XPath(".//*[@id=\"subject\"]")
    Writable<String> subject();

    @XPath(".//input[@id='abstract']")
    Writable<String> description();

    @XPath(".//input[@id='paProtocolNumber']")
    Writable<String> protocolNumber();

    @XPath(".//input[@id='taxonomyCode']")
    Writable<String> taxonomyCode();

    // Group combobox — clicks to open the dropdown
    @XPath(".//*[@id='group']")
    Clickable openGroupDropdown();

    // "Gruppo *" quando l'ente ha dei gruppi e il gruppo è obbligatorio, altrimenti "Gruppo"
    @XPath(".//label[@id='group-label']")
    Readable<String> groupLabel();

    // voci della tendina aperta: il menu è fuori dal form
    @XPath("//*[@role='option']")
    List<Button> groupOptions();

    default void fillFields(NotificationData data) {
        //selectLanguageItalian().click();
        subject().write(data.getSubject());
        protocolNumber().write(data.getProtocolNumber());
        taxonomyCode().write(data.getTaxonomyCode());
        //selectModel890().click();
        // il gruppo indicato dai dati, altrimenti il primo disponibile se l'ente ha dei gruppi e quindi è obbligatorio
        if (data.getGroup() != null) {
            selectGroup(option -> data.getGroup().equals(option.read()));
        } else if (groupLabel().read().endsWith("*")) {
            selectGroup(option -> true);
        }
    }

    default void selectGroup(Predicate<Button> option) {
        openGroupDropdown().click();
        groupOptions().stream()
                .filter(option)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Gruppo non presente nella tendina"))
                .click();
    }

    default XPathSelector groupOptionSelector(String groupName) {
        return XPathSelector.of(
                "//*[@role='option'][normalize-space()='" + groupName + "']"
        );
    }
}
