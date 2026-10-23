package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import lombok.RequiredArgsConstructor;

/**
 * Utenze generiche nelle feature: la chiave dell'alias ({@code User_1}, {@code Impresa_1}, {@code Comune_1}, ...) è
 * risolta nell'utenza dell'ambiente attivo tramite {@link UserProvider}.
 */
@RequiredArgsConstructor
public class UserParameterTypes {

    private final UserProvider userProvider;

    @ParameterType("User_1|User_2|Impresa_1|Impresa_2|Comune_1|Comune_2")
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
