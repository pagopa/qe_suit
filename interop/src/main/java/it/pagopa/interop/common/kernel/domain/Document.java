package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
/* NOTE 19/11/2026: al momento usato per documenti già creati, non per documenti da creare */
public class Document implements DocumentMetadata {
    UUID id;
    String name;
    String contentType;
    String prettyName;
    String checksum;
}

