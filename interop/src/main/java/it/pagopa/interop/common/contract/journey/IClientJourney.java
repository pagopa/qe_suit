package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;

import java.util.UUID;

public interface IClientJourney<SELF extends IClientJourney<SELF>> {
    SELF createClient(ClientKind kind);

    SELF createClientAndInclude(ClientKind kind, UserRole... roles);

    SELF linkPurposeToClient();

    SELF linkPurposeToClient(UUID purposeId);

    SELF generateKeyAndLinkToClient();
}
