package it.pagopa.interop.common.attribute.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


@Value
@Builder(toBuilder = true)
@Jacksonized
public class AttributeCertifiedDiscreteConfig {
    Integer threshold;
    AttributeCertifiedDiscreteComparator comparator;
}
