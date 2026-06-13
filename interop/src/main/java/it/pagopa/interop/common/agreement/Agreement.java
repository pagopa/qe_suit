package it.pagopa.interop.common.agreement;

import it.pagopa.interop.common.template.TestModel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class Agreement implements TestModel {
    @Delegate
    private final it.pagopa.interop.generated.openapi.clients.bff.model.Agreement model;
}
