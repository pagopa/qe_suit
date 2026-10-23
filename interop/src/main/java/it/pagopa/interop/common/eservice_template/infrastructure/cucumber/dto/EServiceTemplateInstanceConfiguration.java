package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplateInstanceConfiguration {
    @NotBlank
    private String alias;

    @NotBlank
    private String template;

    @JsonAlias("suffisso")
    @JsonSetter(nulls = Nulls.FAIL)
    @Pattern(regexp = "(?s).*\\S.*")
    @Size(max = 12)
    private String instanceLabel;
}
