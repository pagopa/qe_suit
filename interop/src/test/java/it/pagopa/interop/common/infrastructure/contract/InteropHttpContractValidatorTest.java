package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InteropHttpContractValidatorTest {

    @Test
    void asUserAndRoleReturnApiCallSelectionStageAndResolveRole() throws Exception {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        AuthSession authSession = mock(AuthSession.class);
        HttpContractStages.ApiCallStage apiCallStage = mock(HttpContractStages.ApiCallStage.class);
        when(delegate.apiCall(any(), any())).thenReturn(apiCallStage);
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, authSession);

        User expectedUser = User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
       
        HttpContractStages.ApiCallSelectionStage userStage = validator.as(Tenant.COMUNE_DI_MILANO, expectedUser);
        assertNotNull(userStage.apiCall(() -> new Object()));

        ArgumentCaptor<HttpContractAuthentication> authCaptor = ArgumentCaptor.forClass(HttpContractAuthentication.class);
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        reset(delegate, authSession);

        validator.as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN).apiCall(() -> new Object());
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        verify(authSession).set(expectedUser, Tenant.COMUNE_DI_MILANO);
        assertThrows(NoSuchMethodException.class, () -> InteropHttpContractValidator.class.getMethod("apiCall", Supplier.class));
    }

    @Test
    void asTenantAndM2MRoleReturnApiCallSelectionStageAndResolveRole() throws Exception {
        HttpContractValidator delegate = mock(HttpContractValidator.class);
        AuthSession authSession = mock(AuthSession.class);
        HttpContractStages.ApiCallStage apiCallStage = mock(HttpContractStages.ApiCallStage.class);
        when(delegate.apiCall(any(), any())).thenReturn(apiCallStage);
        InteropHttpContractValidator validator = new InteropHttpContractValidator(delegate, authSession);

        HttpContractStages.ApiCallSelectionStage userStage = validator.as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN);
        assertNotNull(userStage.apiCall(() -> new Object()));

        ArgumentCaptor<HttpContractAuthentication> authCaptor = ArgumentCaptor.forClass(HttpContractAuthentication.class);
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        reset(delegate, authSession);

        validator.as(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN).apiCall(() -> new Object());
        verify(delegate).apiCall(authCaptor.capture(), any());
        authCaptor.getValue().authenticate();

        verify(authSession).set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        assertThrows(NoSuchMethodException.class, () -> InteropHttpContractValidator.class.getMethod("apiCall", Supplier.class));
    }
}
