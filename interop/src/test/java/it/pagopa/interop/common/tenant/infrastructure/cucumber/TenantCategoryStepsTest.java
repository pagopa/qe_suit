package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.infrastructure.context.CucumberUserSession;
import it.pagopa.interop.common.tenant.application.TenantCategoryUseCase;
import it.pagopa.interop.common.tenant.application.command.CheckTenantCategoryCommand;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.TenantCategoryExpectation;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.mapper.TenantCategoryExpectationMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantCategoryStepsTest {
    private static final ValidatorFactory VALIDATORS = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper transformer = new DataTableMapper(VALIDATORS.getValidator());
    private final TenantCategoryExpectationMapper mapper = Mappers.getMapper(TenantCategoryExpectationMapper.class);
    private final TenantCategoryUseCase useCase = mock(TenantCategoryUseCase.class);
    private final AuthSession session = mock(AuthSession.class);
    private final TenantCategorySteps steps = new TenantCategorySteps(mapper, useCase, session);

    @AfterEach
    void clearContext() {
        DataTableMapper.clearGherkinKeys();
    }

    @AfterAll
    static void closeValidators() {
        VALIDATORS.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sì", "si", "no"})
    void convertsFeatureRowAndDelegatesAfterAuthentication(String membership) {
        var dto = transform(Map.of(
                "ente", "Comune di Milano", "categoria", "COMUNI", "appartenenza", membership));

        var snapshot = mock(AuthSession.Snapshot.class);
        when(session.snapshot()).thenReturn(snapshot);
        steps.checkCategories(List.of(dto));

        var ordered = inOrder(session, useCase, snapshot);
        ordered.verify(session).snapshot();
        ordered.verify(session).set(Tenant.PAGO_PA);
        ordered.verify(useCase).check(new CheckTenantCategoryCommand(
                TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId()),
                TenantCategory.COMUNI, !membership.equals("no")));
        ordered.verify(snapshot).close();
    }

    @Test
    void restoresPreviousSessionOrEmptySessionAfterSuccessAndFailure() {
        for (boolean initiallySet : List.of(false, true)) {
            for (boolean fails : List.of(false, true)) {
                var actualSession = new CucumberUserSession();
                if (initiallySet) actualSession.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
                var actualUseCase = mock(TenantCategoryUseCase.class);
                var actualSteps = new TenantCategorySteps(mapper, actualUseCase, actualSession);
                var dto = transform(Map.of(
                        "ente", "Comune di Milano", "categoria", "COMUNI", "appartenenza", "sì"));
                doAnswer(invocation -> {
                    assertThat(actualSession.getTenant()).isEqualTo(Tenant.PAGO_PA);
                    assertThat(actualSession.getRole()).isEqualTo(UserRole.ADMIN);
                    if (fails) throw new IllegalStateException("category check failed");
                    return null;
                }).when(actualUseCase).check(any());
                if (fails) {
                    assertThatThrownBy(() -> actualSteps.checkCategories(List.of(dto)))
                            .isInstanceOf(IllegalStateException.class).hasMessage("category check failed");
                } else {
                    actualSteps.checkCategories(List.of(dto));
                }
                if (initiallySet) {
                    assertThat(actualSession.getActor())
                            .isEqualTo(User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.API));
                    assertThat(actualSession.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
                    assertThat(actualSession.getRole()).isEqualTo(UserRole.API);
                } else {
                    assertThatThrownBy(actualSession::getActor).isInstanceOf(IllegalStateException.class);
                    assertThatThrownBy(actualSession::getRole).isInstanceOf(IllegalStateException.class);
                    assertThatThrownBy(actualSession::getTenant).isInstanceOf(IllegalStateException.class);
                }
            }
        }
    }

    @Test
    void restoresSnapshotWhenTemporaryAuthenticationFails() {
        var dto = transform(Map.of(
                "ente", "Comune di Milano", "categoria", "COMUNI", "appartenenza", "sì"));
        var snapshot = mock(AuthSession.Snapshot.class);
        when(session.snapshot()).thenReturn(snapshot);
        doThrow(new IllegalStateException("authentication failed")).when(session).set(Tenant.PAGO_PA);
        assertThatThrownBy(() -> steps.checkCategories(List.of(dto)))
                .isInstanceOf(IllegalStateException.class).hasMessage("authentication failed");
        verify(snapshot).close();
        verifyNoInteractions(useCase);
    }

    @Test
    void restorationFailureIsSuppressedWhenCategoryCheckFails() {
        var dto = transform(Map.of(
                "ente", "Comune di Milano", "categoria", "COMUNI", "appartenenza", "sì"));
        var snapshot = mock(AuthSession.Snapshot.class);
        var checkFailure = new IllegalStateException("check failed");
        var restoreFailure = new IllegalStateException("restore failed");
        when(session.snapshot()).thenReturn(snapshot);
        doThrow(checkFailure).when(useCase).check(any());
        doThrow(restoreFailure).when(snapshot).close();
        assertThatThrownBy(() -> steps.checkCategories(List.of(dto)))
                .isSameAs(checkFailure)
                .satisfies(failure -> assertThat(failure.getSuppressed()).containsExactly(restoreFailure));
    }

    @Test
    void rejectsMissingInvalidAndUnknownValues() {
        assertThatThrownBy(() -> transform(Map.of("ente", "Comune di Milano", "categoria", "COMUNI")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of(
                "ente", "Comune di Milano", "categoria", "COMUNI", "appartenenza", "forse")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> transform(Map.of(
                "ente", "Comune di Milano", "categoria", "CAT-1", "appartenenza", "sì")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> transform(Map.of(
                "ente", "Unknown", "categoria", "COMUNI", "appartenenza", "sì")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsEmptyTableBeforeAnyInteraction() {
        assertThatThrownBy(() -> steps.checkCategories(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(session, useCase);
    }

    private TenantCategoryExpectation transform(Map<String, String> row) {
        return (TenantCategoryExpectation) transformer.transform(row, TenantCategoryExpectation.class);
    }

}
