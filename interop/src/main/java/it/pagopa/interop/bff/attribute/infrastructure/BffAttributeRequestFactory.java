package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffAttributeRequestFactory {

    public AttributeSeed creationRequest() {
        return Instancio.of(AttributeSeed.class)
                .set(field(AttributeSeed::getName), randomAlphanumericName("name", 24))
                .set(field(AttributeSeed::getDescription), randomAlphanumericName("description", 32))
                .create();
    }
}

