package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface EServiceCatalogGateway extends Plugin<Channel> {
    EServiceCatalog search(SearchEServiceCatalogCommand command);

    /**
     * Polls with a fresh traversal from the command offset on every attempt and returns the real pages
     * of the first attempt containing all expected IDs; the traversal may stop before the last page.
     */
    List<EServiceCatalog> awaitPresence(
            SearchEServiceCatalogCommand command,
            EServiceCatalogPresenceCommand expected
    );
}
