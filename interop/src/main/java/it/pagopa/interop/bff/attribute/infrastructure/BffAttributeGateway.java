package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceMapper;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffEServiceRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final EntityStore entityStore;
    private final BffEServiceMapper mapper;

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    @Override
    public void removeCertifiedAttributeThresholdFromDraftEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeCertifiedAttributeThresholdFromPublishedEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void certifiedAttributeThresholdRemoved() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
