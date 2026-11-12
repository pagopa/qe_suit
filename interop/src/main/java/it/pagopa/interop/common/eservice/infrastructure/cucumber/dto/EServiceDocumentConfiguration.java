package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceDocumentConfiguration {
    @JsonAlias("nome")
    @NotBlank
    private String name;

    @NotBlank
    private String fixture;
}
