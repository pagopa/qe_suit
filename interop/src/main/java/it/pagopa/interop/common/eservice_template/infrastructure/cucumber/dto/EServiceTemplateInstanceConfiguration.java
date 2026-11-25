package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.dto.ConfigurableText;
import it.pagopa.interop.common.kernel.dto.ConfigurableTextSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplateInstanceConfiguration {
    @NotBlank
    private String alias;

    @NotBlank
    private String template;

    @Valid
    @ConfigurableTextSize(max = 12)
    @JsonAlias("suffisso")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    private ConfigurableText instanceLabel;

    @JsonAlias("erogatore")
    @JsonSetter(nulls = Nulls.FAIL)
    private Tenant producer;
}
