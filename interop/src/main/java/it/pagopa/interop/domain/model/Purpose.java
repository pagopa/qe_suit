package it.pagopa.interop.domain.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class Purpose extends AbstractModel{
    @Delegate
    private final it.pagopa.interop.generated.openapi.clients.bff.model.Purpose embeddedModel;

    @Override
    public String getUniqueIdentifier() {
        return getId().toString();
    }
}
