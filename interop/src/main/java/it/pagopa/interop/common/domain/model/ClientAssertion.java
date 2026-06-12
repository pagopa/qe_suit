package it.pagopa.interop.common.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ClientAssertion implements TestModel {
    private final String clientAssertion;
    private final UUID id = UUID.randomUUID();

    @Override
    public UUID getId() {
        return id;
    }
}
