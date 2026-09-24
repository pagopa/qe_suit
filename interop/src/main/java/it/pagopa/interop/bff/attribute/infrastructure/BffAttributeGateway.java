package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.bff.attribute.application.BffAttributeCreationCommand;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffAttributeRestClient restClient;
    private final BffAttributeMapper mapper;

    @Override
    public Attribute createDeclaredAttribute(AttributeCreationCommand command) {
        if (!(command instanceof BffAttributeCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffAttributeCreationCommand");

        return restClient.createDeclaredAttribute(bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toDomain)
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

