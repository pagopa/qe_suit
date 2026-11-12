package it.pagopa.interop.common.attribute.infrastructure.cucumber.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeAliasConfiguration {
    @NotNull
    @Pattern(regexp = "(CERT|DECL|VER|DISC)-[A-Za-z0-9_-]+")
    private String alias;
}
