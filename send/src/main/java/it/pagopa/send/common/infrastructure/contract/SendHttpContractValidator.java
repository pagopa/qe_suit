package it.pagopa.send.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractIdentity;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Ingresso fluente ai contract test HTTP del modulo send, equivalente di
 * {@code InteropHttpContractValidator}: incrocia le utenze dichiarate con le API indicate in
 * {@code apiCalls(...)}, attendendosi lo stesso status per ogni combinazione.
 */
public class SendHttpContractValidator {

    private final HttpContractValidator delegate;
    private final CurrentUserSession currentUserSession;
    private final UserProvider userProvider;

    public SendHttpContractValidator(
            HttpContractValidator delegate,
            CurrentUserSession currentUserSession,
            UserProvider userProvider
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
        this.userProvider = Objects.requireNonNull(userProvider, "userProvider must not be null");
    }

    public HttpContractStages.AuthorizationApiCallsStage as(List<UserAlias> aliases) {
        Objects.requireNonNull(aliases, "aliases must not be null");

        // Il nome dell'identita' e' l'alias: identifica il test senza esporre username o token.
        // La risoluzione dell'utenza sta dentro la lambda, non fuori: avviene nell'esecuzione del
        // singolo caso, cosi' una configurazione mancante fa fallire quel caso e non la
        // generazione dell'intera factory.
        return delegate.authorization(aliases.stream()
                .map(alias -> new HttpContractIdentity(
                        alias.name(),
                        () -> currentUserSession.setUser(userProvider.resolve(alias))
                ))
                .toList());
    }

    /**
     * Lo stream viene materializzato una sola volta qui: non viene conservato ne' riconsumato.
     */
    public HttpContractStages.AuthorizationApiCallsStage as(Stream<UserAlias> aliases) {
        Objects.requireNonNull(aliases, "aliases must not be null");
        return as(aliases.toList());
    }

    public HttpContractStages.ApiCallSelectionStage as(UserAlias alias) {
        Objects.requireNonNull(alias, "alias must not be null");
        User resolved = userProvider.resolve(alias);
        HttpContractAuthentication authentication = () -> currentUserSession.setUser(resolved);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }

    public HttpContractStages.ApiCallSelectionStage as(User user) {
        Objects.requireNonNull(user, "user must not be null");
        HttpContractAuthentication authentication = () -> currentUserSession.setUser(user);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }
}
