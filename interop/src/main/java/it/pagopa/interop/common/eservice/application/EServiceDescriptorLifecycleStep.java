package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;

public record EServiceDescriptorLifecycleStep(Action action, EServiceDescriptorState expectedState) {
    public enum Action {
        PUBLISH,
        SUSPEND,
        ARCHIVE
    }
}
