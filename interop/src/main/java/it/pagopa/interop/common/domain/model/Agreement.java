package it.pagopa.interop.common.domain.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class Agreement extends AbstractModel{
    @Delegate
    private final it.pagopa.interop.generated.openapi.clients.bff.model.Agreement model;

    @Override
    public String getUniqueIdentifier() {
       return model.getId().toString();
    }
}
