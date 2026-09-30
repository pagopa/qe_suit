package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientCreationCommand;
import it.pagopa.interop.common.client.application.ClientCommandFactory;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.stereotype.Service;

@Service
public class BffClientCommandFactory implements ClientCommandFactory {
    @Override
    public ClientCreationCommand creationCommand() {
        return new BffClientCreationCommand();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
