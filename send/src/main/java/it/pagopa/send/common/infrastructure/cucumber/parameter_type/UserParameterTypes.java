package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import lombok.RequiredArgsConstructor;

/**
 * Utenze generiche nelle feature: la chiave dell'alias ({@code user-1}, {@code impresa-1}, {@code comune-1}, ...) è
 * risolta nell'utenza dell'ambiente attivo tramite {@link UserProvider}.
 */
@RequiredArgsConstructor
public class UserParameterTypes {

    private final UserProvider userProvider;

    @ParameterType("user-1|user-2|impresa-1|impresa-2|comune-1|comune-2")
    public User user(String userKey) {
        return userProvider.resolve(UserAlias.fromKey(userKey));
    }

    @ParameterType("la PG|l'utente")
    public String recipientType(String recipientType) {
        if (recipientType.equals("la PG")) return "PG";
        if (recipientType.equals("l'utente")) return "utente";
        throw new IllegalArgumentException("Tipo utente non valido: " + recipientType);
    }
}
