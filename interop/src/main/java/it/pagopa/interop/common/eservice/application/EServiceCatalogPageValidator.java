package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceCatalog;

public final class EServiceCatalogPageValidator {
    private EServiceCatalogPageValidator() {
    }

    public static EServiceCatalog requirePage(EServiceCatalog page, int expectedOffset, int expectedLimit) {
        requirePageMetadata(page, expectedOffset, expectedLimit);
        int expectedResults = (int) Math.max(
                0L,
                Math.min((long) expectedLimit, (long) page.getTotalCount() - expectedOffset)
        );
        if (page.getResults().size() != expectedResults) {
            throw new IllegalStateException("Catalog returned " + page.getResults().size()
                    + " results at offset " + expectedOffset + "; expected " + expectedResults);
        }
        if (page.getResults().stream().anyMatch(entry -> entry == null || entry.getId() == null)) {
            throw new IllegalStateException("Catalog returned an entry without an e-service ID");
        }
        return page;
    }

    public static EServiceCatalog requirePageMetadata(
            EServiceCatalog page, int expectedOffset, int expectedLimit
    ) {
        if (page == null) {
            throw new IllegalStateException("Catalog gateway returned no search result");
        }
        if (page.getOffset() != expectedOffset) {
            throw new IllegalStateException("Catalog returned offset " + page.getOffset()
                    + " instead of " + expectedOffset);
        }
        if (page.getLimit() != expectedLimit) {
            throw new IllegalStateException("Catalog returned limit " + page.getLimit()
                    + " instead of " + expectedLimit);
        }
        if (page.getTotalCount() < 0) {
            throw new IllegalStateException("Catalog returned a negative total count");
        }
        if (page.getResults() == null) {
            throw new IllegalStateException("Catalog returned no results collection");
        }
        return page;
    }
}
