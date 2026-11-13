package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

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
public class EServiceMetadataUpdateConfiguration {
    @JsonAlias("campo")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotBlank
    @Pattern(
            regexp = "^(nome|descrizione|technology|mode|signalHubEnabled|"
                    + "consumerDelegable|clientAccessDelegable|handlePersonalData|asyncExchange)$",
            message = "Campo metadato e-service non supportato"
    )
    private String field;

    @JsonAlias("valore")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    private String value;
}
