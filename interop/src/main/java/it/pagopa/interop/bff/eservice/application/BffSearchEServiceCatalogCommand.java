package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import lombok.Getter;

@Getter
public class BffSearchEServiceCatalogCommand implements SearchEServiceCatalogCommand {
    private final CatalogFilterPayload bffPayload = new CatalogFilterPayload();

    @Override
    public SearchEServiceCatalogCommand keyword(String keyword) {
        bffPayload.setKeyword(keyword);
        return this;
    }

    @Override
    public SearchEServiceCatalogCommand onlyActiveEservices(Boolean onlyActiveEservices) {
        bffPayload.setOnlyActiveEservices(onlyActiveEservices);
        return this;
    }

    @Override
    public SearchEServiceCatalogCommand offset(Integer offset) {
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("Catalog offset must be at least 0");
        }
        bffPayload.setOffset(offset);
        return this;
    }

    @Override
    public SearchEServiceCatalogCommand limit(Integer limit) {
        if (limit == null || limit < 1 || limit > 200) {
            throw new IllegalArgumentException("Catalog limit must be between 1 and 200");
        }
        bffPayload.setLimit(limit);
        return this;
    }
}
