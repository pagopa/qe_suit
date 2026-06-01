package it.pagopa.interop.common.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClientAssertion extends AbstractModel {
    private final String clientAssertion;

    @Override
    public String getUniqueIdentifier() {
        return clientAssertion;
    }
}
