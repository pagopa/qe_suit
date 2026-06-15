package it.pagopa.interop.common.contract.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class Purpose implements TestModel {
    @Delegate
    private final it.pagopa.interop.generated.openapi.clients.bff.model.Purpose embeddedModel;
}
