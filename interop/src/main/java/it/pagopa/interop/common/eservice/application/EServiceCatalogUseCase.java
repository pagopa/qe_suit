package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceCatalogUseCase {
    private final EServiceCatalogGateway gateway;

    public EServiceCatalog search(SearchEServiceCatalogCommand command) {
        return gateway.search(command);
    }
}
