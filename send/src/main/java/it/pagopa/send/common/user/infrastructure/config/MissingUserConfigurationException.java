package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.UserAlias;

/**
 * Utenza richiesta non configurata per l'ambiente attivo: la voce manca nel file delle utenze dell'ambiente oppure è
 * ancora un segnaposto {@code REPLACE_ME}.
 */
public class MissingUserConfigurationException extends RuntimeException {

    public MissingUserConfigurationException(UserAlias alias) {
        super("Utenza " + alias + " non configurata nel file delle utenze dell'ambiente");
    }
}
