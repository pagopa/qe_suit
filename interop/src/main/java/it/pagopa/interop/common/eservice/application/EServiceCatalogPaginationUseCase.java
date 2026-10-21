package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class EServiceCatalogPaginationUseCase {
    private final EServiceCatalogUseCase catalog;
    private final EServiceCatalogGateway gateway;
    private final EServiceRequestFactory requestFactory;

    public List<EServiceCatalog> searchPages(SearchEServiceCatalogCommand command) {
        return searchPages(command, catalog.search(command));
    }

    public List<EServiceCatalog> searchPages(
            SearchEServiceCatalogCommand command, EServiceCatalog firstPage
    ) {
        catalog.validatePagination(command);
        catalog.requirePage(firstPage, command.getOffset(), command.getLimit());
        return searchPages(command, firstPage, catalog.pageCount(command, firstPage));
    }

    public List<EServiceCatalog> searchPages(
            SearchEServiceCatalogCommand command, EServiceCatalog firstPage, int pageCount
    ) {
        catalog.validatePagination(command);
        if (pageCount < 1) {
            throw new IllegalArgumentException("At least one catalog page is required");
        }
        Integer requestedOffset = command.getOffset();
        Integer requestedLimit = command.getLimit();
        catalog.requirePage(firstPage, requestedOffset, requestedLimit);
        int declaredPageCount = catalog.pageCount(command, firstPage);
        if (pageCount != declaredPageCount) {
            throw new IllegalStateException("Expected " + pageCount + " catalog pages but the first page declares "
                    + declaredPageCount + " pages for total " + firstPage.getTotalCount());
        }
        int totalCount = firstPage.getTotalCount();
        List<EServiceCatalog> pages = new ArrayList<>();
        pages.add(firstPage);

        for (long pageIndex = 1; pageIndex < pageCount; pageIndex++) {
            long nextOffset = (long) requestedOffset + pageIndex * requestedLimit;
            if (nextOffset > Integer.MAX_VALUE) {
                throw new IllegalStateException("Catalog pagination offset exceeds the supported range");
            }
            SearchEServiceCatalogCommand pageCommand =
                    requestFactory.searchEServiceCatalogCommandForOffset(command, (int) nextOffset);
            EServiceCatalog page = catalog.search(pageCommand);
            if (page.getTotalCount() != totalCount) {
                throw new IllegalStateException("Catalog total count changed between pages: "
                        + totalCount + " -> " + page.getTotalCount() + " at offset " + nextOffset);
            }
            pages.add(page);
        }
        return List.copyOf(pages);
    }

    public void shouldContainInRelativeOrder(
            SearchEServiceCatalogCommand command, EServiceCatalog firstPage, List<EService> expected
    ) {
        assertThat(expected).as("E-services expected in relative catalog order").isNotEmpty();
        List<UUID> expectedIds = expected.stream().map(EService::getId).toList();
        assertThat(expectedIds).as("Expected relative order IDs").doesNotHaveDuplicates();
        catalog.validatePagination(command);
        String firstAnomaly;
        try {
            scanRelativeOrder(command, firstPage, expectedIds);
            return;
        } catch (InconsistentScan anomaly) {
            firstAnomaly = anomaly.getMessage();
        }
        // Offset pagination has no snapshot: retry an anomalous traversal once from a fresh first page.
        try {
            scanRelativeOrder(command, gateway.search(command), expectedIds);
        } catch (InconsistentScan anomaly) {
            throw new IllegalStateException("Inconclusive catalog relative-order scan after 2 attempts; "
                    + "offset pagination cannot guarantee a snapshot. First attempt: " + firstAnomaly
                    + ". Fresh attempt: " + anomaly.getMessage(), anomaly);
        }
    }

    private void scanRelativeOrder(
            SearchEServiceCatalogCommand command, EServiceCatalog firstPage, List<UUID> expectedIds
    ) {
        int offset = command.getOffset();
        int limit = command.getLimit();
        EServiceCatalog page = catalog.requirePageMetadata(firstPage, offset, limit);
        int initialTotal = page.getTotalCount();
        Set<UUID> seen = new HashSet<>();
        List<UUID> observed = new ArrayList<>();
        Set<UUID> expectedSet = new HashSet<>(expectedIds);
        boolean totalChanged = false;
        while (true) {
            totalChanged |= page.getTotalCount() != initialTotal;
            int expectedCount = catalog.expectedResultCount(page, offset, limit);
            if (page.getResults().size() != expectedCount) {
                throw new InconsistentScan("Inconsistent result count at offset " + offset
                        + ", total=" + page.getTotalCount());
            }
            for (EServiceCatalog.Entry entry : page.getResults()) {
                if (!seen.add(entry.getId())) {
                    throw new InconsistentScan("Repeated e-service " + entry.getId() + " at offset " + offset);
                }
                if (expectedSet.contains(entry.getId())) {
                    observed.add(entry.getId());
                }
            }
            if (observed.size() == expectedIds.size()) {
                if (totalChanged && !observed.equals(expectedIds)) {
                    throw new InconsistentScan("Unexpected relative order with changing totals at offset " + offset
                            + "; observed=" + observed + ", expected=" + expectedIds);
                }
                assertThat(observed).as("Relative e-service order across catalog pages")
                        .containsExactlyElementsOf(expectedIds);
                return;
            }
            long nextOffset = (long) offset + limit;
            // Freeze the attempt's horizon at its observed initial total, never chase a growing catalog forever.
            if (nextOffset >= initialTotal || nextOffset >= page.getTotalCount()) {
                List<UUID> missing = expectedIds.stream().filter(id -> !seen.contains(id)).toList();
                throw new InconsistentScan("Missing expected IDs " + missing + " after offset " + offset
                        + "; initial total=" + initialTotal + ", latest total=" + page.getTotalCount()
                        + ", total changed=" + totalChanged);
            }
            if (nextOffset > Integer.MAX_VALUE) {
                throw new IllegalStateException("Catalog pagination offset exceeds the supported range");
            }
            offset = (int) nextOffset;
            page = catalog.requirePageMetadata(gateway.search(
                    requestFactory.searchEServiceCatalogCommandForOffset(command, offset)), offset, limit);
        }
    }

    private static class InconsistentScan extends RuntimeException {
        private InconsistentScan(String message) {
            super(message);
        }
    }
}
