package it.pagopa.interop.common.eservice.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EServiceCatalogPaginationExpectation {
    int pageNumber;
    int resultCount;
}
