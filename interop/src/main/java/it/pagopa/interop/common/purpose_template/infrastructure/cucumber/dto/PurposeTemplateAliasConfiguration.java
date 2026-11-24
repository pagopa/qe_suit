package it.pagopa.interop.common.purpose_template.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurposeTemplateAliasConfiguration {
    @JsonSetter(nulls = Nulls.FAIL)
    @NotBlank
    private String alias;
}
