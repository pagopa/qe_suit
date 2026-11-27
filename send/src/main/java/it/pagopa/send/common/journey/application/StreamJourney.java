package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.user.domain.User;

/**
 * Precondizioni sugli stream di eventi di avanzamento notifiche (pn-stream) dell'ente mittente.
 * <p>
 * Lo spec ammette al massimo 10 stream attivi per PA: gli stream creati dai test sono riconoscibili
 * (titolo di test o tracciati alla creazione) e vengono rimossi prima di crearne di nuovi.
 */
public interface StreamJourney<SELF extends StreamJourney<SELF>> extends JourneyModule {

    /**
     * Rimuove gli stream di test dell'ente e ne crea uno nuovo; lo stream resta in {@code EntityStore}
     * ({@code get(EventStream.class)}).
     */
    SELF withFreshStream(User owner);

    /**
     * Rimuove tutti gli stream di test dell'ente.
     */
    void purgeTestStreams(User owner);
}
