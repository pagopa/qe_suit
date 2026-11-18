package it.pagopa.interop.common.purpose.application;

import java.util.UUID;

public interface PurposeCloneCommand {
    PurposeCloneCommand eserviceId(UUID eserviceId);

    UUID getEserviceId();
}
