package it.pagopa.interop.common.infrastructure.cucumber.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import it.pagopa.interop.common.kernel.domain.Tenant;

import java.io.IOException;

public class ProducerDeserializer extends JsonDeserializer<Tenant> {
    @Override
    public Tenant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Tenant.fromAlias(parser.getText());
    }
}
