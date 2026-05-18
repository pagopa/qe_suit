package it.pagopa.send.domain.web.commons.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

public interface Sidebar extends Component {

    @XPath("(//ul/div/div/span)[1]")
    Clickable notifications();

    @XPath("(//ul/div/div/span)[2]")
    Clickable apiKeys();

    @XPath("(//ul/div/div/span)[3]")
    Clickable statistics();

    @XPath("(//ul/div/div/span)[4]")
    Clickable platformStatus();

    default void goToSection(String section) {
        switch (section) {
            case "Notifications" -> notifications().click();
            case "API Key" -> apiKeys().click();
            case "Statistics" -> statistics().click();
            case "Platform status" -> platformStatus().click();
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        }
    }

}
