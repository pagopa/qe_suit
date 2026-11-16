package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;

import java.io.IOException;

public class EServiceAsyncExchangeDeserializer extends JsonDeserializer<ConfiguredBoolean> {
    @Override
    public ConfiguredBoolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return switch (parser.getText()) {
            case "sì", "si" -> ConfiguredBoolean.ENABLED;
            case "no" -> ConfiguredBoolean.DISABLED;
            case "non impostato" -> ConfiguredBoolean.UNSET;
            default -> throw new IllegalArgumentException("Scambio asincrono non supportato: " + parser.getText());
        };
    }
}
