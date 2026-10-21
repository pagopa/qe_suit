package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceRequestFactory extends Plugin<Channel> {
    EServiceCreationCommand defaultCreationEServiceCommand();

    UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand();

    SearchEServiceCatalogCommand defaultSearchEServiceCatalogCommand();

    SearchEServiceCatalogCommand searchEServiceCatalogCommandForOffset(
            SearchEServiceCatalogCommand original, Integer offset);

    default UpdateEServiceDescriptorCommand prepareDescriptorForPublicationCommand() {
        return defaultUpdateDescriptorCommand();
    }
}
