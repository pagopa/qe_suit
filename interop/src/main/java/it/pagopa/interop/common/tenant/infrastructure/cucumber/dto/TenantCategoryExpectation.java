package it.pagopa.interop.common.tenant.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.common.kernel.domain.Tenant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantCategoryExpectation {
    @JsonAlias("ente")
    @NotNull
    private Tenant tenant;

    @JsonAlias("categoria")
    @NotNull
    private TenantCategory category;

    @JsonAlias("appartenenza")
    @NotNull
    @Pattern(regexp = "sì|si|no")
    private String membership;
}
