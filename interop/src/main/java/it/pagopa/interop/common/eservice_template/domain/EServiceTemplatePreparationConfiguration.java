package it.pagopa.interop.common.eservice_template.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplatePreparationConfiguration {
    @JsonAlias({"tecnologia"})
    @NotNull
    private EServiceTechnology technology;

    @JsonAlias({"stato"})
    @NotNull
    private EServiceTemplateVersionState state;

    @JsonAlias({"interfaccia", "fixtureInterfaccia"})
    @NotBlank
    private String interfaceFixture;
}
