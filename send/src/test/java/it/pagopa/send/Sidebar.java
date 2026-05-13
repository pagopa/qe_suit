package it.pagopa.send;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.steps.login.component.OneTrustBanner;

import java.util.Optional;

public interface Sidebar extends Component {

    @XPath("//*[@id=\"side-item-Notifications\"]")
    Clickable notifications();

    @XPath("//*[@id=\"side-item-API Key\"]")
    Clickable apiKeys();

    @XPath("//*[@id=\"menu-item(statistics)\"]/span")
    Clickable statistics();

    @XPath("//*[@id=\"side-item-Platform status\"]")
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
