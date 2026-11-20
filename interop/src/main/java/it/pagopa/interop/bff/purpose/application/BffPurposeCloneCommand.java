package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.PurposeCloneCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeCloneSeed;
import lombok.Getter;

import java.util.UUID;

public class BffPurposeCloneCommand implements PurposeCloneCommand {

    @Getter
    private final PurposeCloneSeed bffClonePayload;

    public BffPurposeCloneCommand() {
        this.bffClonePayload = new PurposeCloneSeed();
    }

    public BffPurposeCloneCommand(PurposeCloneSeed bffClonePayload) {
        this.bffClonePayload = bffClonePayload;
    }

    @Override
    public PurposeCloneCommand eserviceId(UUID eserviceId) {
        bffClonePayload.setEserviceId(eserviceId);
        return this;
    }

    @Override
    public UUID getEserviceId() {
        return bffClonePayload.getEserviceId();
    }
}
