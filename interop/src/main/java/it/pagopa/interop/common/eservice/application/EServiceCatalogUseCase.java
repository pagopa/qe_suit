package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class EServiceCatalogUseCase {
    private final EServiceCatalogGateway gateway;

    public EServiceCatalog search(SearchEServiceCatalogCommand command) {
        return gateway.search(command);
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
}
