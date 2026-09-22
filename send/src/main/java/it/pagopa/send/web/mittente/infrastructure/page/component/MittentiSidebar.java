package it.pagopa.send.web.mittente.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

public interface MittentiSidebar extends Component {
    @XPath("//*[@id=\"side-item-Notifica con SEND\"]")
    Button notificaConSend();
    @XPath("//*[@id=\"side-item-Notifiche\"]")
    Button notifiche();
    @XPath("//*[@id=\"side-item-Statistiche\"]")
    Button statistiche();
    @XPath("//*[@id=\"side-item-Comunica con SEND\"]")
    Button comunicaConSend();
    @XPath("//*[@id=\"side-item-Campagne\"]")
    Button campagne();
    @XPath("(//ul/div/div/span)[2]")
    Button apiKey();
    @XPath("(//ul/div/div/span)[4]")
    Button platformStatus();

    default void goToSection(String section) {
        switch (section) {
            case "Notifiche" -> notifiche().click();
            case "API Key" -> apiKey().click();
            case "Statistics" -> statistiche().click();
            case "Platform status" -> platformStatus().click();
            default -> throw new IllegalArgumentException("Unknown section: " + section);
        }
    }

}
