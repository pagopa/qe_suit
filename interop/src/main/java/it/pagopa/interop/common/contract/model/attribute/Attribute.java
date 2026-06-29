package it.pagopa.interop.common.contract.model.attribute;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Attribute implements TestModel {
    UUID id;
    String code;
    String name;
    String description;
    AttributeKind kind;
    Integer group;
}
