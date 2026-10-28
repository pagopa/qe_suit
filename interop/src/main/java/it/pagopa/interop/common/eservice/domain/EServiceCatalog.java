package it.pagopa.interop.common.eservice.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class EServiceCatalog {
    List<Entry> results;
    int offset;
    int limit;
    int totalCount;

    @Builder
    public EServiceCatalog(List<Entry> results, int offset, int limit, int totalCount) {
        this.results = List.copyOf(results);
        this.offset = offset;
        this.limit = limit;
        this.totalCount = totalCount;
    }

    @Value
    @Builder
    public static class Entry {
        UUID id;
        String name;
        String description;
        UUID producerId;
        Boolean isMine;
        UUID descriptorId;
        String version;
        EServiceDescriptorState state;
        Boolean hasUnreadNotifications;
    }
}
