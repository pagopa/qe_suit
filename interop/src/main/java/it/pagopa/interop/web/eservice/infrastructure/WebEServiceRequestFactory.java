package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.*;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.web.eservice.application.WebEServiceCreationCommand;
import org.springframework.stereotype.Component;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@Component
public class WebEServiceRequestFactory implements EServiceRequestFactory {
    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {
        return new WebEServiceCreationCommand()
                .name(randomAlphanumericName("eservice", 15))
                .description("E-service di test.")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isAsync(false)
                .handlePersonalData(false);
    }

    @Override
    public UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand() {
        throw new UnsupportedOperationException("Descriptor command creation is not supported on the WEB channel");
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand defaultUpdateDescriptorInstanceCommand(EServiceDescriptorState state) {
        throw new UnsupportedOperationException("Template instance descriptor command creation is not supported on the WEB channel");
    }

    @Override
    public SearchEServiceCatalogCommand defaultSearchEServiceCatalogCommand() {
        throw new UnsupportedOperationException("Catalog search command creation is not supported on the WEB channel");
    }

    @Override
    public SearchEServiceCatalogCommand searchEServiceCatalogCommandForOffset(SearchEServiceCatalogCommand original, Integer offset) {
        throw new UnsupportedOperationException("Catalog pagination command creation is not supported on the WEB channel");
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}
