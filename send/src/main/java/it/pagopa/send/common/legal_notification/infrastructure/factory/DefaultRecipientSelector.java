package it.pagopa.send.common.legal_notification.infrastructure.factory;

import it.pagopa.send.common.legal_notification.domain.RecipientCardinality;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Sceglie i destinatari di una notifica per tipo (persona fisica o giuridica) e cardinalità, dal pool di utenze di test,
 * quando lo scenario non ha bisogno di nominarli: un destinatario è il primo del pool, più destinatari sono tutto il pool.
 */
@Component
@RequiredArgsConstructor
public class DefaultRecipientSelector {

    private static final Map<UserType, List<UserAlias>> POOL = Map.of(
            UserType.PF, List.of(UserAlias.USER_1, UserAlias.USER_2),
            UserType.PG, List.of(UserAlias.IMPRESA_1, UserAlias.IMPRESA_2));

    private final UserProvider userProvider;

    public List<User> select(UserType type, RecipientCardinality cardinality) {
        List<UserAlias> aliases = POOL.get(type);
        if (aliases == null) {
            throw new IllegalArgumentException("Nessun destinatario di test per il tipo " + type);
        }
        int count = cardinality == RecipientCardinality.SINGLE ? 1 : aliases.size();
        List<User> recipients = aliases.stream().limit(count).map(userProvider::resolve).toList();
        if (recipients.stream().map(User::getTaxId).distinct().count() != recipients.size()) {
            throw new IllegalStateException("Il pool " + type + " non contiene destinatari distinti per la cardinalità "
                    + cardinality);
        }
        return recipients;
    }
}
