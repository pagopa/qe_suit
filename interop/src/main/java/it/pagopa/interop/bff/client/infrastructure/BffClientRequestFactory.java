package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
public class BffClientRequestFactory {

    private static final UUID DEFAULT_MEMBER_ID = User.S_MATTIA.getUserId();

    public ClientSeed creationRequest() {
        return Instancio.of(ClientSeed.class)
                .generate(field(ClientSeed::getName), gen -> gen.string().prefix("client-").length(24))
                .generate(field(ClientSeed::getDescription), gen -> gen.string().prefix("description-").length(32))
                .set(field(ClientSeed::getMembers), List.of(DEFAULT_MEMBER_ID))
                .create();
    }

    public KeySeed keyCreationRequest() {
        BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
        command.randomClientConsumerKey();
        return command.getKeySeed();
    }
}

