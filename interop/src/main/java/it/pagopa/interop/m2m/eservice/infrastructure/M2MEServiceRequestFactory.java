package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.m2m.eservice.application.M2MEServiceUpdateCommand;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class M2MEServiceRequestFactory implements EServiceRequestFactory {

    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {
        throw unsupportedOperation();
    }

    @Override
    public EServiceUpdateCommand defaultUpdateEServiceCommand() {
        return new M2MEServiceUpdateCommand();
    }

    @Override
    public UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand() {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand defaultUpdateDescriptorInstanceCommand(
            EServiceDescriptorState currentState
    ) {
        throw unsupportedOperation();
    }

    @Override
    public SearchEServiceCatalogCommand defaultSearchEServiceCatalogCommand() {
        throw unsupportedOperation();
    }

    @Override
    public SearchEServiceCatalogCommand searchEServiceCatalogCommandForOffset(
            SearchEServiceCatalogCommand original,
            Integer offset
    ) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This e-service operation is not supported on M2M v3");
    }
}
