package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPageExpectation;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(config = TestMapperConfig.class)
public interface EServiceCatalogPaginationExpectationMapper {
    default List<EServiceCatalogPaginationExpectation> toExpectations(
            List<EServiceCatalogPageExpectation> source
    ) {
        if (source.isEmpty()) {
            throw new IllegalArgumentException("At least one catalog page expectation is required");
        }
        Set<Integer> pages = new HashSet<>();
        for (int i = 0; i < source.size(); i++) {
            int page = source.get(i).getPage();
            if (!pages.add(page)) {
                throw new IllegalArgumentException("Duplicate catalog page expectation: " + page);
            }
            if (page != i + 1) {
                throw new IllegalArgumentException("Catalog page expectations must be consecutive starting at 1");
            }
        }
        return source.stream().map(this::toExpectation).toList();
    }

    @Mapping(target = "pageNumber", source = "page")
    @Mapping(target = "resultCount", source = "results")
    EServiceCatalogPaginationExpectation toExpectation(EServiceCatalogPageExpectation source);
}
