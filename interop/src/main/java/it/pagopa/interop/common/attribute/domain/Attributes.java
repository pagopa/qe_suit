package it.pagopa.interop.common.attribute.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Attributes {
    @Singular("certified")
    List<Attribute> certified;

    @Singular("declared")
    List<Attribute> declared;

    @Singular("verified")
    List<Attribute> verified;
}
