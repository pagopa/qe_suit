package it.pagopa.interop.common.delegation.domain;

import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Delegation implements Identifiable {
    UUID id;
}
