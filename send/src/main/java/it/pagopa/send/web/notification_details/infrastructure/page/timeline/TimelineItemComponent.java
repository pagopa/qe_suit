package it.pagopa.send.web.notification_details.infrastructure.page.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

/**
 * Singola card della timeline (una voce di stato notifica), qualunque sia la sua forma: titolo
 * sempre presente; elenco destinatari e gruppi espandibili ("Invio in corso") presenti solo
 * quando la card li prevede. {@link #recipients()}/{@link #groups()} restituiscono liste vuote
 * quando non applicabili, così lo stesso tipo copre qualunque combinazione senza bisogno di una
 * classe dedicata per ciascuna forma.
 */
@XPath(".//li[contains(@class,'MuiTimelineItem-root')]")
public interface TimelineItemComponent extends Component {

    @XPath(".//div[contains(@class,'MuiStack-root')][1]/span[last()]")
    Readable<String> title();

    @XPath(".//p[@data-testid='timeline-group-recipient']")
    List<Readable<String>> recipients();

    @XPath(".//div[@data-testid='timeline-group']")
    List<TimelineGroupComponent> groups();

    /**
     * Presenza di una frase limitata al testo di questa sola card, non dell'intera pagina.
     */
    default boolean containsPhrase(String phrase) {
        return get().map(element -> element.getText().contains(phrase)).orElse(false);
    }

    @XPath(".//div[@data-testid='timeline-group']")
    interface TimelineGroupComponent extends Component {

        @XPath("preceding-sibling::p[@data-testid='timeline-group-recipient'][1]")
        Readable<String> recipientName();

        @XPath(".//button[@data-testid='timeline-group-header']/span[1]/span[1]")
        Readable<String> label();

        @XPath(".//button[@data-testid='timeline-group-header']")
        HeaderButton toggleHeader();

        @XPath(".//button[@data-testid='timeline-group-header']")
        interface HeaderButton extends Component, Clickable {
            @Override
            default void assertLoaded() {
            }
        }

        default boolean isExpanded() {
            return toggleHeader().get()
                    .map(we -> "true".equals(we.getAttributes().get("aria-expanded")))
                    .orElse(false);
        }

        @Override
        default void assertLoaded() {
        }
    }

    @Override
    default void assertLoaded() {
    }
}
