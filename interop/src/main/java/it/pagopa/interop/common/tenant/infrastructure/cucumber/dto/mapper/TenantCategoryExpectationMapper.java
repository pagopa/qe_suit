package it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.command.CheckTenantCategoryCommand;
import it.pagopa.interop.common.tenant.infrastructure.cucumber.dto.TenantCategoryExpectation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = TestMapperConfig.class)
public interface TenantCategoryExpectationMapper {
    List<CheckTenantCategoryCommand> toCommands(List<TenantCategoryExpectation> source);

    default CheckTenantCategoryCommand toCommand(TenantCategoryExpectation source) {
        boolean expected = switch (source.getMembership()) {
            case "sì", "si" -> true;
            case "no" -> false;
            default -> throw new IllegalArgumentException("Unsupported category membership: " + source.getMembership());
        };
        return new CheckTenantCategoryCommand(
                TenantRef.of(source.getTenant().getOrganizationId()), source.getCategory(), expected);
    }
}
