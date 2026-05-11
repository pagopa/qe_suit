package it.pagopa.interop.domain.services.purpose;

import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;

import java.util.UUID;
import java.util.function.Consumer;

public interface PurposeService {
    Purpose createEservicePurpose(Eservice eservice);
    Purpose createEservicePurpose(Eservice eservice, Consumer<PurposeSeed> overrides);
    Purpose createEservicePurposeWithState(Eservice eservice, PurposeVersionState targetState);
    Purpose createEservicePurposeWithState(Eservice eservice, PurposeVersionState targetState, Consumer<PurposeSeed> overrides);
    Purpose getPurpose(UUID purposeId);
}
