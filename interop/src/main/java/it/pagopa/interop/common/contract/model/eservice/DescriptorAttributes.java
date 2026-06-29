package it.pagopa.interop.common.contract.model.eservice;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DescriptorAttributes {
    @Singular("certified")
    List<AttributeRef> certified;

    @Singular("declared")
    List<AttributeRef> declared;

    @Singular("verified")
    List<AttributeRef> verified;
}
