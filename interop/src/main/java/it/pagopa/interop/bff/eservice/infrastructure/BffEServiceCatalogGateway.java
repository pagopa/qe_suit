package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogGateway;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffEServiceCatalogGateway implements EServiceCatalogGateway {
    private final BffEServiceCatalogRestClient restClient;
    private final BffEServiceCatalogMapper mapper;

    @Override
    public EServiceCatalog search(SearchEServiceCatalogCommand command) {
        if (!(command instanceof BffSearchEServiceCatalogCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffSearchEServiceCatalogCommand");
        }
        if (bffCommand.getBffPayload().getOffset() == null || bffCommand.getBffPayload().getLimit() == null) {
            throw new IllegalArgumentException("Catalog offset and limit are required");
        }
        return restClient.queryEServicesCatalog(bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toCatalog)
                .get();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
