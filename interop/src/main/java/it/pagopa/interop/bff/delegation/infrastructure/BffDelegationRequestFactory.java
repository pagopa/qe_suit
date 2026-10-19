package it.pagopa.interop.bff.delegation.infrastructure;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class BffDelegationRequestFactory {

    public RejectDelegationPayload rejectionRequest() {
        return Instancio.of(RejectDelegationPayload.class)
                .set(field(RejectDelegationPayload::getRejectionReason), randomAlphanumericName("rejection-reason", 32))
                .create();
    }
}

