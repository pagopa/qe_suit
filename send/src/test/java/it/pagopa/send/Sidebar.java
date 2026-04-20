package it.pagopa.send;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

public interface Sidebar extends Component {

    @XPath("//*[@id=\"menu-item(notifications)\"]/span")
    Clickable notifications();

    @XPath("//*[@id=\"menu-item(api key)\"]/span")
    Clickable apiKeys();

    @XPath("//*[@id=\"menu-item(statistics)\"]/span")
    Clickable statistics();

    @XPath("//*[@id=\"menu-item(platform status)\"]/span")
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
