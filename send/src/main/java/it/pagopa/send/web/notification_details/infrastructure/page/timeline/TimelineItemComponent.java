package it.pagopa.send.web.notification_details.infrastructure.page.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
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

    // data da cui lo stato è attivo, esclusa quella degli eventi dei gruppi; "Invio in corso" non la mostra
    @XPath(".//*[@data-testid='dateItem'][not(ancestor::*[@data-testid='timeline-group'])]")
    Component statusDate();

    // "Espandi tutti" / "Comprimi tutti": nel titolo di "Invio in corso" quando la card ha più gruppi
    @XPath(".//button[@data-testid='timeline-expand-all']")
    Component expandAllButton();

    @XPath(".//p[@data-testid='timeline-group-recipient']")
    List<Readable<String>> recipients();

    @XPath(".//div[@data-testid='timeline-group']")
    List<TimelineGroupComponent> groups();

    /**
     * Etichetta dello stato della card: il titolo senza il testo del pulsante "Espandi tutti", che nel portale sta
     * nello stesso elemento del titolo.
     */
    default String statusLabel() {
        String title = title().read();
        return expandAllButton().get(FindPolicy.PRESENT)
                .map(button -> title.replace(button.getText(), ""))
                .orElse(title)
                .strip();
    }

    /** Testo dell'intera card: titolo, descrizione, destinatari ed eventi dei gruppi espansi o meno. */
    default String text() {
        return get().map(element -> element.getText()).orElse("");
    }

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
