package it.pagopa.send.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractIdentity;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SendHttpContractValidatorTest {

    private final HttpContractValidator delegate = mock(HttpContractValidator.class);
    private final CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
    private final UserProvider userProvider = mock(UserProvider.class);

    private final SendHttpContractValidator validator =
            new SendHttpContractValidator(delegate, currentUserSession, userProvider);

    @Test
    void asListDelegatesToAuthorizationWithIdentitiesNamedAfterTheAlias() {
        HttpContractStages.AuthorizationApiCallsStage stage =
                mock(HttpContractStages.AuthorizationApiCallsStage.class);
        when(delegate.authorization(any())).thenReturn(stage);

        assertNotNull(validator.as(List.of(UserAlias.USER_1, UserAlias.COMUNE_2)));

        ArgumentCaptor<List<HttpContractIdentity>> captor = ArgumentCaptor.captor();
        verify(delegate).authorization(captor.capture());

        List<HttpContractIdentity> identities = captor.getValue();
        assertEquals(List.of("USER_1", "COMUNE_2"), identities.stream().map(HttpContractIdentity::name).toList());
    }

    @Test
    void asStreamIsMaterialisedOnceAndNeverReconsumed() {
        HttpContractStages.AuthorizationApiCallsStage stage =
                mock(HttpContractStages.AuthorizationApiCallsStage.class);
        when(delegate.authorization(any())).thenReturn(stage);

        Stream<UserAlias> aliases = Stream.of(UserAlias.USER_1, UserAlias.IMPRESA_1);
        assertNotNull(validator.as(aliases));

        ArgumentCaptor<List<HttpContractIdentity>> captor = ArgumentCaptor.captor();
        verify(delegate).authorization(captor.capture());
        assertEquals(2, captor.getValue().size());

        // Lo stream e' stato consumato una sola volta: un secondo consumo deve fallire qui, non
        // dentro al validator.
        assertThrows(IllegalStateException.class, aliases::toList);
    }

    /**
     * La risoluzione dell'utenza deve avvenire nell'esecuzione del caso, non alla dichiarazione:
     * altrimenti una configurazione mancante farebbe fallire la generazione dell'intera factory.
     */
    @Test
    void userIsResolvedOnlyWhenTheAuthenticationRuns() {
        HttpContractStages.AuthorizationApiCallsStage stage =
                mock(HttpContractStages.AuthorizationApiCallsStage.class);
        when(delegate.authorization(any())).thenReturn(stage);

        validator.as(List.of(UserAlias.USER_1));

        verifyNoInteractions(userProvider);
        verify(currentUserSession, never()).setUser(any());

        ArgumentCaptor<List<HttpContractIdentity>> captor = ArgumentCaptor.captor();
        verify(delegate).authorization(captor.capture());

        User resolved = mock(User.class);
        when(userProvider.resolve(UserAlias.USER_1)).thenReturn(resolved);

        captor.getValue().get(0).authentication().authenticate();

        verify(userProvider).resolve(UserAlias.USER_1);
        verify(currentUserSession).setUser(resolved);
    }

    @Test
    void nullArgumentsAreRejected() {
        assertThrows(NullPointerException.class, () -> validator.as((List<UserAlias>) null));
        assertThrows(NullPointerException.class, () -> validator.as((Stream<UserAlias>) null));
    }
}
