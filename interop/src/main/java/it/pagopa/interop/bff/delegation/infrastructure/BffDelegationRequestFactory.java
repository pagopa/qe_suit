package it.pagopa.interop.bff.delegation.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import org.springframework.stereotype.Component;

@Component
public class BffDelegationRequestFactory {

    public RejectDelegationPayload rejectionRequest() {
        return new RejectDelegationPayload()
                .rejectionReason("Rifiutata nell'ambito del setup di test");
    }
}
