package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Document {
    UUID id;
    String name;
    String contentType;
    String prettyName;
    String checksum;
}


