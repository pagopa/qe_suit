package it.pagopa.send.domain.web.pages.mittente.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

/**
 * Voce "semplice" della timeline di stato notifica: titolo + un unico paragrafo descrittivo
 * con data, senza suddivisione per destinatario (a differenza di {@link GiacenzaTimelineComponent}
 * e {@link InvioInCorsoComponent}). L'ancora esclude esplicitamente i blocchi che contengono i
 * marker di raggruppamento per destinatario, così lo stesso tipo si applica a qualunque voce con
 * questa forma (es. "Perfezionata per decorrenza termini", "Depositata"), senza dover conoscere
 * il titolo a priori.
 */
@XPath(".//div[contains(@class,'MuiPaper-root')][not(.//p[@data-testid='timeline-group-recipient']) and not(.//div[@data-testid='timeline-group'])]")
public interface NotificationTimelineStatusComponent extends Component {

    @XPath(".//div[contains(@class,'MuiStack-root')][1]/span[last()]")
    Readable<String> title();

    @XPath(".//p[1]")
    Readable<String> message();

    @Override
    default void assertLoaded() {
    }
}
