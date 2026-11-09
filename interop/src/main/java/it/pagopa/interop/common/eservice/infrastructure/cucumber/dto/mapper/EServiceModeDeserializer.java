package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import it.pagopa.interop.common.eservice.domain.EServiceMode;

import java.io.IOException;

public class EServiceModeDeserializer extends JsonDeserializer<EServiceMode> {
    @Override
    public EServiceMode deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return switch (parser.getText()) {
            case "erogazione" -> EServiceMode.DELIVER;
            case "ricezione" -> EServiceMode.RECEIVE;
            default -> throw new IllegalArgumentException("Modalità e-service non supportata: " + parser.getText());
        };
    }
}
