package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.UserAlias;

/**
 * Utenza richiesta non configurata per l'ambiente attivo: la voce manca nel file delle utenze dell'ambiente oppure è
 * ancora un segnaposto {@code REPLACE_ME}.
 */
public class MissingUserConfigurationException extends RuntimeException {

    public MissingUserConfigurationException(UserAlias alias, String environment) {
        super("Utenza " + alias + " non configurata per l'ambiente '" + environment + "': aggiungere la chiave '"
                + alias.key() + "' in application-users-" + environment + ".yaml");
    }
}
