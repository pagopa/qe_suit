package it.pagopa.interop.domain.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientAssertion extends AbstractModel {
    private final String clientAssertion;

    @Override
    public String getUniqueIdentifier() {
        return clientAssertion;
    }
}
