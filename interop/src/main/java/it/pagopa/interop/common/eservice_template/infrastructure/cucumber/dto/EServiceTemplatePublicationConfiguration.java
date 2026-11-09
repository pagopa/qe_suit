package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.Tenant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplatePublicationConfiguration {
    @NotBlank
    private String alias;

    @JsonAlias("nome")
    @NotBlank
    private String name;

    @JsonAlias("tecnologia")
    @NotNull
    private EServiceTechnology technology;

    @JsonAlias("erogatore")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    private Tenant producer = Tenant.COMUNE_DI_MILANO;
}
