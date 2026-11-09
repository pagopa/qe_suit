package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
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
    private final CurrentUserSession session = mock(CurrentUserSession.class);
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

        steps.checkCategories(List.of(dto));

        var ordered = inOrder(session, useCase);
        ordered.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        ordered.verify(useCase).check(new CheckTenantCategoryCommand(
                TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId()),
                TenantCategory.COMUNI, !membership.equals("no")));
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
