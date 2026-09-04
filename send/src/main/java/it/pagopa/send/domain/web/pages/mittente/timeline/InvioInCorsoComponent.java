package it.pagopa.send.domain.web.pages.mittente.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@XPath(".//span[normalize-space()='Invio in corso']/ancestor::div[contains(@class,'MuiPaper-root')][1]")
public interface InvioInCorsoComponent extends Component {

    @XPath(".//span[normalize-space()='Invio in corso']")
    Readable<String> title();

    @XPath(".//p[@data-testid='timeline-group-recipient']")
    List<Readable<String>> recipients();

    @XPath(".//div[@data-testid='timeline-group']")
    List<TimelineGroupComponent> groups();

    @XPath(".//div[@data-testid='timeline-group']")
    interface TimelineGroupComponent extends Component {

        @XPath("preceding-sibling::p[@data-testid='timeline-group-recipient'][1]")
        Readable<String> recipientName();

        @XPath(".//button[@data-testid='timeline-group-header']/span[1]/span[1]")
        Readable<String> label();

        @XPath(".//ul[@data-testid='timeline-group-body']/li[@data-testid='timeline-event']/p")
        List<Readable<String>> eventTexts();

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

        /**
         * Espande il gruppo se collassato (il DOM degli eventi non esiste finché non è
         * espanso) e ne legge i testi.
         */
        default List<String> readEventTexts() {
            if (!isExpanded()) {
                toggleHeader().click();
            }
            return eventTexts().stream().map(Readable::read).toList();
        }

        @Override
        default void assertLoaded() {
        }
    }

    /**
     * Tutti i gruppi presenti per un destinatario, quali che siano (0..N): i gruppi (PEC,
     * Messaggi di cortesia, ecc.) sono mostrati o meno a seconda della risposta API del run
     * corrente, quindi non si assume alcuna presenza fissa. Nelle notifiche a destinatario
     * singolo il marker {@code <p data-testid="timeline-group-recipient">} non viene
     * renderizzato affatto: in quel caso ({@link #recipients()} vuoto) tutti i gruppi
     * appartengono all'unico destinatario e vengono restituiti senza interrogare
     * {@code recipientName()} su ciascun gruppo, che altrimenti andrebbe in timeout cercando
     * un marker che non esiste.
     */
    default List<TimelineGroupComponent> groupsFor(String recipientName) {
        if (recipients().isEmpty()) {
            return groups();
        }
        return groups().stream()
                .filter(g -> recipientName.equals(g.recipientName().read()))
                .toList();
    }

    /**
     * Un gruppo specifico per destinatario+etichetta, se presente nella risposta API di quel run.
     */
    default Optional<TimelineGroupComponent> groupFor(String recipientName, String label) {
        return groupFor(recipientName, l -> l.equalsIgnoreCase(label));
    }

    /**
     * Variante per notifiche a destinatario singolo, dove non c'è un nome da filtrare.
     */
    default Optional<TimelineGroupComponent> groupFor(String label) {
        return groupFor(l -> l.equalsIgnoreCase(label));
    }

    /**
     * Variante per etichette non note a priori perché contengono una parte dinamica (es.
     * "Raccomandata 890 · &lt;hash&gt;"): {@code labelMatcher} permette di matchare solo la
     * parte stabile, ad es. {@code l -> l.startsWith("Raccomandata 890")}.
     */
    default Optional<TimelineGroupComponent> groupFor(String recipientName, Predicate<String> labelMatcher) {
        return groupsFor(recipientName).stream()
                .filter(g -> labelMatcher.test(g.label().read()))
                .findFirst();
    }

    default Optional<TimelineGroupComponent> groupFor(Predicate<String> labelMatcher) {
        return groups().stream()
                .filter(g -> labelMatcher.test(g.label().read()))
                .findFirst();
    }

    default boolean hasGroup(String recipientName, String label) {
        return groupFor(recipientName, label).isPresent();
    }

    default boolean hasGroup(String label) {
        return groupFor(label).isPresent();
    }

    default void verificyRecipients(List<String> expectedRecipients) {
        List<String> actualRecipients = recipients().stream()
                .map(Readable::read)
                .toList();

        if (!actualRecipients.equals(expectedRecipients)) {
            throw new AssertionError("Expected recipients: " + expectedRecipients + ", but found: " + actualRecipients);
        }
    }

    @Override
    default void assertLoaded() {
    }
}
