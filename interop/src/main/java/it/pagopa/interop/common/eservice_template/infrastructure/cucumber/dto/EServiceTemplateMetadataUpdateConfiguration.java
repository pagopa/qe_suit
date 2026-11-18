package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplateMetadataUpdateConfiguration {
    @JsonAlias("campo")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotBlank
    @Pattern(
            regexp = "^(nome|descrizione|intendedTarget|mode|technology|"
                    + "isSignalHubEnabled|personalData|asyncExchange)$",
            message = "Campo metadato e-service template non supportato"
    )
    private String field;

    @JsonAlias("valore")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    private String value;
}
