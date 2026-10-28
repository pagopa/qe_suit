package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantCategoryUseCase;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.TenantCategoryExpectation;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.mapper.TenantCategoryExpectationMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TenantCategorySteps {
    private final TenantCategoryExpectationMapper mapper;
    private final TenantCategoryUseCase categories;
    private final CurrentUserSession session;

    @Given("gli enti appartenenti alle categorie indicate secondo i seguenti dati:")
    public void checkCategories(List<TenantCategoryExpectation> expectations) {
        var commands = mapper.toCommands(expectations);
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("At least one tenant category expectation is required");
        }
        session.set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        commands.forEach(categories::check);
    }
}
