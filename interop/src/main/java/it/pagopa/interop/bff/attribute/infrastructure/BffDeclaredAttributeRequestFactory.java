package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class BffDeclaredAttributeRequestFactory {

    public AttributeSeed creationRequest() {
        return Instancio.of(AttributeSeed.class)
                .generate(field(AttributeSeed::getName), gen -> gen.string().prefix("declared-attr-").length(24))
                .generate(field(AttributeSeed::getDescription), gen -> gen.string().prefix("description-").length(32))
                .create();
    }
}

