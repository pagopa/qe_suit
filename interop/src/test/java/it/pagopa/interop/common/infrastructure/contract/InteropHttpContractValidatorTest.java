package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.*;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InteropHttpContractValidatorTest {

    @Test
    void asUserAndRoleReturnApiCallSelectionStageAndResolveRole() throws Exception {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        CurrentM2MSession currentM2MSession = mock(CurrentM2MSession.class);
        HttpContractStages.ApiCallStage apiCallStage = mock(HttpContractStages.ApiCallStage.class);
        when(delegate.apiCall(any(), any())).thenReturn(apiCallStage);
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, currentUserSession, currentM2MSession);

        User expectedUser = User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
       
        HttpContractStages.ApiCallSelectionStage userStage = validator.as(Tenant.COMUNE_DI_MILANO, expectedUser);
        assertNotNull(userStage.apiCall(() -> new Object()));

        ArgumentCaptor<HttpContractAuthentication> authCaptor = ArgumentCaptor.forClass(HttpContractAuthentication.class);
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        reset(delegate, currentUserSession, currentM2MSession);

        validator.as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN).apiCall(() -> new Object());
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        verify(currentUserSession).set(expectedUser, Tenant.COMUNE_DI_MILANO);
        assertThrows(NoSuchMethodException.class, () -> InteropHttpContractValidator.class.getMethod("apiCall", Supplier.class));
    }

    @Test
    void asTenantAndM2MRoleReturnApiCallSelectionStageAndResolveRole() throws Exception {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        CurrentM2MSession currentM2MSession = mock(CurrentM2MSession.class);
        HttpContractStages.ApiCallStage apiCallStage = mock(HttpContractStages.ApiCallStage.class);
        when(delegate.apiCall(any(), any())).thenReturn(apiCallStage);
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, currentUserSession, currentM2MSession);

        HttpContractStages.ApiCallSelectionStage userStage = validator.as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN);
        assertNotNull(userStage.apiCall(() -> new Object()));

        ArgumentCaptor<HttpContractAuthentication> authCaptor = ArgumentCaptor.forClass(HttpContractAuthentication.class);
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        reset(delegate, currentUserSession, currentM2MSession);

        validator.as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN).apiCall(() -> new Object());
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        verify(currentM2MSession).set(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN);
        assertThrows(NoSuchMethodException.class, () -> InteropHttpContractValidator.class.getMethod("apiCall", Supplier.class));
    }

    @Test
    void authorizationIdentitiesForSameTenantDelegateTheMatrixToTheEngine() {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        CurrentM2MSession currentM2MSession = mock(CurrentM2MSession.class);
        HttpAuthorizationStages.ApiCallsStage stage = mock(HttpAuthorizationStages.ApiCallsStage.class);
        when(delegate.as(any(Stream.class))).thenReturn(stage);
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, currentUserSession, currentM2MSession);
        User admin = User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        User api = User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.API);

        assertEquals(stage, validator.as(Tenant.COMUNE_DI_MILANO, List.of(admin, api)));
        assertEquals(stage, validator.as(Tenant.COMUNE_DI_MILANO, Stream.of(admin, api)));

        ArgumentCaptor<Stream<HttpContractIdentity>> captor = ArgumentCaptor.forClass(Stream.class);
        verify(delegate, org.mockito.Mockito.times(2)).as(captor.capture());
        verifyNoInteractions(currentUserSession, currentM2MSession);
        List<HttpContractIdentity> identities = captor.getAllValues().get(0).toList();
        assertEquals(List.of("Comune di Milano / ADMIN", "Comune di Milano / API"), identities.stream().map(HttpContractIdentity::name).toList());
        identities.get(1).authentication().authenticate();
        verify(currentUserSession).set(api, Tenant.COMUNE_DI_MILANO);
    }

    @Test
    void authorizationIdentitiesWithDifferentTenantsSupportBffAndM2MAuthentication() {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        CurrentM2MSession currentM2MSession = mock(CurrentM2MSession.class);
        when(delegate.as(any(Stream.class))).thenReturn(mock(HttpAuthorizationStages.ApiCallsStage.class));
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, currentUserSession, currentM2MSession);

        validator.as(List.of(
                InteropContractIdentity.bff(Tenant.AGID, UserRole.REVIEWER),
                InteropContractIdentity.bff(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN),
                InteropContractIdentity.m2m(Tenant.PAGO_PA, M2MRole.M2M)
        ));

        ArgumentCaptor<Stream<HttpContractIdentity>> captor = ArgumentCaptor.forClass(Stream.class);
        verify(delegate).as(captor.capture());
        List<HttpContractIdentity> identities = captor.getValue().toList();
        assertEquals(
                List.of("AgID / REVIEWER", "Comune di Milano / ADMIN", "PagoPA / M2M"),
                identities.stream().map(HttpContractIdentity::name).toList()
        );
        identities.get(0).authentication().authenticate();
        identities.get(2).authentication().authenticate();
        verify(currentUserSession).set(User.getTenantUser(Tenant.AGID, UserRole.REVIEWER), Tenant.AGID);
        verify(currentM2MSession).set(Tenant.PAGO_PA, M2MRole.M2M);
    }

    @Test
    void authorizationIdentityRequiresExactlyOneAuthenticationKind() {
        assertThrows(NullPointerException.class, () -> InteropContractIdentity.bff(null, UserRole.ADMIN));
        assertThrows(IllegalArgumentException.class, () -> new InteropContractIdentity("x", Tenant.AGID, null, null));
        assertThrows(NullPointerException.class, () -> new InteropHttpContractValidator(mock(HttpContractValidator.class), mock(CurrentUserSession.class), mock(CurrentM2MSession.class))
                .as(Tenant.AGID, (List<User>) null));
    }
}
