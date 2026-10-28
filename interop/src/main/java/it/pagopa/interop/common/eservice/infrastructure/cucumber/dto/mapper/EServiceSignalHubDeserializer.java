package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;

import java.io.IOException;

public class EServiceSignalHubDeserializer extends JsonDeserializer<ConfiguredBoolean> {
    @Override
    public ConfiguredBoolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return switch (parser.getText()) {
            case "attivo" -> ConfiguredBoolean.ENABLED;
            case "non attivo" -> ConfiguredBoolean.DISABLED;
            case "non impostato" -> ConfiguredBoolean.UNSET;
            default -> throw new IllegalArgumentException("Valore Signal Hub non supportato: " + parser.getText());
        };
    }
}
