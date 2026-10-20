package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceAsyncExchangeDeserializer;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceSignalHubDeserializer;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class EServicePreparationConfiguration {
    @JsonSetter(nulls = Nulls.FAIL)
    @Pattern(regexp = "(?s).*\\S.*", message = "L'alias non può essere vuoto")
    private String alias;

    @JsonAlias("erogatore")
    @JsonSetter(nulls = Nulls.FAIL)
    @NotNull(message = "L'erogatore è obbligatorio")
    private Tenant producer;

    @JsonAlias("nome")
    @JsonSetter(nulls = Nulls.FAIL)
    @Pattern(regexp = "(?s).*\\S.*", message = "Il nome non può essere vuoto")
    private String name;

    @JsonAlias("descrizione")
    private String description;

    @JsonAlias("modalità")
    @JsonSetter(nulls = Nulls.FAIL)
    private EServiceMode mode;

    @JsonAlias("Signal Hub")
    @JsonDeserialize(using = EServiceSignalHubDeserializer.class)
    @JsonSetter(nulls = Nulls.FAIL)
    private ConfiguredBoolean signalHub = ConfiguredBoolean.DEFAULT;

    @JsonAlias("scambio asincrono")
    @JsonDeserialize(using = EServiceAsyncExchangeDeserializer.class)
    @JsonSetter(nulls = Nulls.FAIL)
    private ConfiguredBoolean asyncExchange = ConfiguredBoolean.DEFAULT;

}
