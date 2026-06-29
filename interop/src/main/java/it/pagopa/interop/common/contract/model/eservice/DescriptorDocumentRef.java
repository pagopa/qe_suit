package it.pagopa.interop.common.contract.model.eservice;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DescriptorDocumentRef {
    UUID id;
    String name;
    String contentType;
    String prettyName;
    String checksum;
}


