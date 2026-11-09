package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceCatalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/**
 * One fresh offset traversal of catalog pages. Its horizon is frozen at the first page's declared total,
 * so a growing catalog is never chased; totals that change and repeated IDs are recorded as anomalies.
 */
public final class EServiceCatalogTraversal {
    private final int startOffset;
    private final int limit;
    private final List<EServiceCatalog> pages = new ArrayList<>();
    private final Set<UUID> seen = new HashSet<>();
    private final List<String> anomalies = new ArrayList<>();
    private int horizon;

    public EServiceCatalogTraversal(int startOffset, int limit) {
        if (startOffset < 0 || limit < 1) {
            throw new IllegalArgumentException("Non-negative catalog offset and positive limit are required");
        }
        this.startOffset = startOffset;
        this.limit = limit;
    }

    /** Records the next page and returns the offset of the following page, or empty at the horizon. */
    public OptionalInt accept(EServiceCatalog page) {
        long offset = (long) startOffset + (long) pages.size() * limit;
        if (page.getOffset() != offset || page.getLimit() != limit) {
            throw new IllegalArgumentException("Catalog page offset/limit " + page.getOffset() + "/"
                    + page.getLimit() + " does not continue the traversal at " + offset + "/" + limit);
        }
        if (pages.isEmpty()) {
            horizon = page.getTotalCount();
        } else if (page.getTotalCount() != horizon) {
            anomalies.add("total changed from " + horizon + " to " + page.getTotalCount() + " at offset " + offset);
        }
        for (EServiceCatalog.Entry entry : page.getResults()) {
            if (!seen.add(entry.getId())) {
                anomalies.add("repeated e-service " + entry.getId() + " at offset " + offset);
            }
        }
        pages.add(page);
        long next = offset + limit;
        if (next >= horizon || next >= page.getTotalCount()) {
            return OptionalInt.empty();
        }
        if (next > Integer.MAX_VALUE) {
            throw new IllegalStateException("Catalog pagination offset exceeds the supported range");
        }
        return OptionalInt.of((int) next);
    }

    public List<EServiceCatalog> pages() {
        return List.copyOf(pages);
    }

    public List<UUID> ids() {
        return pages.stream().flatMap(page -> page.getResults().stream()).map(EServiceCatalog.Entry::getId).toList();
    }

    public List<Integer> offsets() {
        return pages.stream().map(EServiceCatalog::getOffset).toList();
    }

    public int horizon() {
        return horizon;
    }

    public List<String> anomalies() {
        return List.copyOf(anomalies);
    }

    public boolean isAnomalous() {
        return !anomalies.isEmpty();
    }
}
