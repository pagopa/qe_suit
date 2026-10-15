package it.pagopa.send.web.notification_details.infrastructure.page.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

/**
 * Container stabile della timeline di stato notifica: ancorato al suo {@code data-testid}, non a
 * un XPath assoluto legato a struttura/posizione della pagina. Al suo interno possono comparire
 * diverse combinazioni di card ({@link TimelineItemComponent}), anche 0..N a seconda della history
 * della notifica corrente: {@link #items()} le espone tutte senza assumere che una particolare
 * combinazione sia sempre presente.
 */
@XPath("//*[@data-testid='NotificationEventsTimeline']")
public interface TimelineComponent extends Component {

    @XPath(".//li[contains(@class,'MuiTimelineItem-root')]")
    List<TimelineItemComponent> items();

    /**
     * Recupera la card per titolo esatto (es. "Invio in corso", "Depositata"), così da poter
     * limitare le verifiche successive al suo solo contenuto.
     */
    default Optional<TimelineItemComponent> itemTitled(String title) {
        return items().stream()
                .filter(item -> title.equals(item.title().read()))
                .findFirst();
    }

    @Override
    default void assertLoaded() {
        Assertions.assertThat(items()).as("Nessun elemento nella timeline").isNotEmpty();
    }
}
