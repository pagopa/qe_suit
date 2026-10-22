package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceCatalogGateway extends Plugin<Channel> {
    EServiceCatalog search(SearchEServiceCatalogCommand command);

    EServiceCatalog awaitPresence(
            SearchEServiceCatalogCommand command,
            EServiceCatalogPresenceCommand expected
    );
}
