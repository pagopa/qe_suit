package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceCatalogPageExpectation {
    @JsonAlias("pagina")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    @Min(1)
    private Integer page;

    @JsonAlias("risultati")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull
    @Min(0)
    private Integer results;
}
