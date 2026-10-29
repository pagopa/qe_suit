package it.pagopa.send.common.infrastructure;

import it.pagopa.infrastructure.contract.http.HttpAuthorizationStages;
import it.pagopa.infrastructure.contract.http.HttpContractIdentity;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SendHttpContractValidatorTest {

    @Test
    @SuppressWarnings("unchecked")
    void usersAreTranslatedToIdentitiesThatSetTheCurrentUserSession() {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        CurrentUserSession session = mock(CurrentUserSession.class);
        HttpAuthorizationStages.ApiCallsStage stage = mock(HttpAuthorizationStages.ApiCallsStage.class);
        when(delegate.as(any(Stream.class))).thenReturn(stage);
        User first = mock(User.class);
        User second = mock(User.class);
        when(first.getUsername()).thenReturn("first.user");
        when(second.getUsername()).thenReturn("second.user");
        SendHttpContractValidator validator = new SendHttpContractValidator(delegate, session);

        assertEquals(stage, validator.as(List.of(first, second)));

        ArgumentCaptor<Stream<HttpContractIdentity>> captor = ArgumentCaptor.forClass(Stream.class);
        verify(delegate).as(captor.capture());
        verifyNoInteractions(session);
        List<HttpContractIdentity> identities = captor.getValue().toList();
        assertEquals(List.of("first.user", "second.user"), identities.stream().map(HttpContractIdentity::name).toList());
        identities.get(1).authentication().authenticate();
        verify(session).setUser(second);
    }

    @Test
    void nullReferencesAreRejected() {
        SendHttpContractValidator validator = new SendHttpContractValidator(mock(HttpContractValidator.class), mock(CurrentUserSession.class));

        assertThrows(NullPointerException.class, () -> validator.as((List<User>) null));
        assertThrows(NullPointerException.class, () -> validator.as((Stream<User>) null));
        assertThrows(NullPointerException.class, () -> new SendHttpContractValidator(null, mock(CurrentUserSession.class)));
    }
}
