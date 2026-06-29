package it.pagopa.interop.common.contract.model;

import it.pagopa.interop.common.contract.enums.AttributeKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute implements TestModel {
    private UUID id;
    private String name;
    private String description;
    private AttributeKind kind;
    private Boolean explicitAttributeVerification;

    @Override
    public UUID getId() {
        return id;
    }
}
