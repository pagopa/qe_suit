package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.infrastructure.security.crypto.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;

import java.util.UUID;

public interface ClientJourney<SELF extends ClientJourney<SELF>> extends JourneyModule {
    SELF createClientConsumer(Key keyToAssociate, UserRole... usersToAssociate);

    default SELF createClientConsumer(UserRole... usersToAssociate) {
        return createClientConsumer(null, usersToAssociate);
    }

    SELF createApiClient(Key keyToAssociate, UserRole... usersToAssociate);

    default SELF createApiClient(UserRole... usersToAssociate) {
        return createApiClient(null, usersToAssociate);
    }

    SELF linkPurposeToClient(UUID... purposeIds);
}
