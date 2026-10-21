package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class EServiceCatalogUseCase {
    private final EServiceCatalogGateway gateway;

    public EServiceCatalog search(SearchEServiceCatalogCommand command) {
        validatePagination(command);
        return requirePage(gateway.search(command), command.getOffset(), command.getLimit());
    }

    public void shouldContainInOrder(EServiceCatalog result, List<EService> expected) {
        assertThat(expected).as("E-services expected in catalog result order").isNotEmpty();
        assertThat(result.getResults())
                .as("Catalog results at offset %s", result.getOffset())
                .extracting(EServiceCatalog.Entry::getId)
                .containsExactlyElementsOf(expected.stream().map(EService::getId).toList());
    }

    public void shouldHavePagination(
            List<EServiceCatalog> pages, List<Integer> expectedResultCounts
    ) {
        assertThat(expectedResultCounts).as("Expected catalog pages").isNotEmpty();
        assertThat(pages).hasSameSizeAs(expectedResultCounts);
        assertThat(expectedResultCounts.stream().mapToLong(Integer::longValue).sum())
                .as("Declared catalog total count")
                .isEqualTo(pages.get(0).getTotalCount());
        for (int index = 0; index < pages.size(); index++) {
            EServiceCatalog page = pages.get(index);
            assertThat(page.getResults())
                    .as("Results on catalog page %s", index + 1)
                    .hasSize(expectedResultCounts.get(index));
            assertThat(page.getTotalCount()).isEqualTo(pages.get(0).getTotalCount());
        }
    }

    public void shouldHaveNoDuplicateEServices(List<EServiceCatalog> pages) {
        List<UUID> ids = pages.stream()
                .flatMap(page -> page.getResults().stream())
                .map(EServiceCatalog.Entry::getId)
                .toList();
        assertThat(ids).as("E-service IDs across catalog pages").doesNotHaveDuplicates();
    }

    public void shouldContainExactly(List<EServiceCatalog> pages, List<EService> expected) {
        shouldHaveNoDuplicateEServices(pages);
        List<UUID> actualIds = pages.stream()
                .flatMap(page -> page.getResults().stream())
                .map(EServiceCatalog.Entry::getId)
                .toList();
        assertThat(actualIds)
                .as("All catalog result IDs across pages")
                .containsExactlyInAnyOrderElementsOf(expected.stream().map(EService::getId).toList());
    }

    public void shouldAppearExactlyOnce(List<EServiceCatalog> pages, EService expected) {
        long occurrences = pages.stream()
                .flatMap(page -> page.getResults().stream())
                .filter(entry -> expected.getId().equals(entry.getId()))
                .count();
        assertThat(occurrences)
                .as("Occurrences of e-service %s across catalog pages", expected.getId())
                .isEqualTo(1);
    }

    public void shouldBeEmptyWithTotalCount(EServiceCatalog result, int totalCount) {
        assertThat(result.getResults()).as("Catalog page at offset %s", result.getOffset()).isEmpty();
        assertThat(result.getTotalCount()).isEqualTo(totalCount);
    }

    public void shouldContain(EServiceCatalog result, List<EService> expected) {
        assertThat(expected).as("E-services expected in catalog results").isNotEmpty();
        assertThat(result.getResults())
                .as("Catalog results at offset %s", result.getOffset())
                .extracting(EServiceCatalog.Entry::getId)
                .containsAll(expected.stream().map(EService::getId).toList());
    }

    public void shouldNotContain(EServiceCatalog result, List<EService> excluded) {
        assertThat(excluded).as("E-services excluded from catalog results").isNotEmpty();
        assertThat(result.getResults())
                .as("Catalog results at offset %s", result.getOffset())
                .extracting(EServiceCatalog.Entry::getId)
                .doesNotContainAnyElementsOf(excluded.stream().map(EService::getId).toList());
    }

    EServiceCatalog requirePage(EServiceCatalog page, int expectedOffset, int expectedLimit) {
        requirePageMetadata(page, expectedOffset, expectedLimit);
        int expectedResults = expectedResultCount(page, expectedOffset, expectedLimit);
        if (page.getResults().size() != expectedResults) {
            throw new IllegalStateException("Catalog returned " + page.getResults().size()
                    + " results at offset " + expectedOffset + "; expected " + expectedResults);
        }
        return page;
    }

    void validatePagination(SearchEServiceCatalogCommand command) {
        if (command.getOffset() == null || command.getOffset() < 0
                || command.getLimit() == null || command.getLimit() < 1) {
            throw new IllegalArgumentException("Non-negative catalog offset and positive limit are required");
        }
    }

    int expectedResultCount(EServiceCatalog page, int offset, int limit) {
        return (int) Math.max(0L, Math.min((long) limit, (long) page.getTotalCount() - offset));
    }

    int pageCount(SearchEServiceCatalogCommand command, EServiceCatalog firstPage) {
        long remaining = Math.max(0L, (long) firstPage.getTotalCount() - command.getOffset());
        return (int) Math.max(1L, (remaining + command.getLimit() - 1) / command.getLimit());
    }

    EServiceCatalog requirePageMetadata(EServiceCatalog page, int expectedOffset, int expectedLimit) {
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
        return page;
    }
}
