package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.*;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceRequestFactory extends Plugin<Channel> {
    EServiceCreationCommand defaultCreationEServiceCommand();

    EServiceUpdateCommand defaultUpdateEServiceCommand();

    UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand();

    EServiceTemplateInstanceUpdateDescriptorCommand defaultUpdateDescriptorInstanceCommand(EServiceDescriptorState currentState);

    SearchEServiceCatalogCommand defaultSearchEServiceCatalogCommand();

    SearchEServiceCatalogCommand searchEServiceCatalogCommandForOffset(
            SearchEServiceCatalogCommand original, Integer offset);

    default UpdateEServiceDescriptorCommand prepareDescriptorForPublicationCommand() {
        return defaultUpdateDescriptorCommand();
    }
}
