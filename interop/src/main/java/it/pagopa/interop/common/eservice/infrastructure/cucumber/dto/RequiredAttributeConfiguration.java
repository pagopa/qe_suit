package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequiredAttributeConfiguration {
    @JsonAlias("tipo")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    @Pattern(regexp = "certificato|dichiarato|verificato|discreto")
    private String kind;

    @JsonAlias("gruppo")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    @Min(1)
    private Integer group;

    @JsonAlias("attributo")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    @Pattern(regexp = "un attributo posseduto da PagoPA|un attributo non posseduto da PagoPA"
            + "|un attributo non posseduto e uno posseduto da PagoPA")
    private String attribute;
}
