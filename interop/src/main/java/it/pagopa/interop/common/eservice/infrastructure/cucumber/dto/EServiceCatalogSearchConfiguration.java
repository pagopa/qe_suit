package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceCatalogSearchConfiguration {
    @JsonAlias("filtro")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotBlank
    @Pattern(
            regexp = "solo e-service attivi|a partire dal risultato|risultati per pagina|modalità|scambio asincrono|Signal Hub|erogatore|categoria dell'erogatore",
            message = "Filtro catalogo non supportato"
    )
    private String filter;

    @JsonAlias("valore")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotBlank
    private String value;
}
