package it.pagopa.interop.common.eservice.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ArchivingSchedule {
    Instant archivableOn;
    Instant startedAt;
    ArchivingScope scope;
}


