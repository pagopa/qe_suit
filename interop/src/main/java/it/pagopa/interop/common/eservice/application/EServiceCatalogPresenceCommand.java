package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
public final class EServiceCatalogPresenceCommand {
    private final List<UUID> expectedIds;

    public EServiceCatalogPresenceCommand(List<UUID> expectedIds) {
        this.expectedIds = List.copyOf(expectedIds);
        if (this.expectedIds.isEmpty()) {
            throw new IllegalArgumentException("At least one expected e-service ID is required");
        }
    }

    public List<UUID> expectedIds() {
        return expectedIds;
    }

    public List<UUID> missingIds(EServiceCatalog page) {
        List<UUID> actualIds = page.getResults().stream()
                .map(EServiceCatalog.Entry::getId)
                .toList();
        return expectedIds.stream()
                .distinct()
                .filter(expectedId -> !actualIds.contains(expectedId))
                .toList();
    }

    public boolean isSatisfiedBy(EServiceCatalog page) {
        return missingIds(page).isEmpty();
    }
}
