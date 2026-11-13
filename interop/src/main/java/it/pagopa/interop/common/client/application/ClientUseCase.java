package it.pagopa.interop.common.client.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.kernel.security.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class ClientUseCase {

    private final ClientGateway clientGateway;
    private final ClientCommandFactory clientCommandFactory;
    private final ClientAssertionGateway clientAssertionGateway;

    public AccessToken generateVoucher(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        if (client.getKind() == null) {
            throw new IllegalArgumentException("Tipologia non specificata per il client " + client.getId());
        }
        if (client.getLastKey() == null) {
            throw new IllegalArgumentException("Nessuna chiave disponibile per il client " + client.getId());
        }

        if (client.getKind() == ClientKind.CONSUMER
                && (purpose == null || purpose.getLastActiveVersion() == null)) {
            throw new IllegalArgumentException("Finalita attiva richiesta per il client " + client.getId());
        }

        AccessToken voucher = clientAssertionGateway.generateVoucher(client,
                client.getKind() == ClientKind.API ? null : purpose, null);
        assertThat(voucher).as("Voucher generato per il client %s", client.getId()).isNotNull();
        assertThat(voucher.jwt())
                .as("Token del voucher generato per il client %s", client.getId())
                .isNotBlank();
        return voucher;
    }

    public Client createClient(Consumer<ClientCreationCommand> creationCommand) {
        ClientCreationCommand command = clientCommandFactory.creationCommand();
        creationCommand.accept(command);
        return clientGateway.createClient(command);
    }

    public Client linkPurpose(Client client, Purpose purpose) {
        return clientGateway.addPurpose(client, purpose);
    }

    public Client updateMetadata(Client client, String name, String description) {
        return clientGateway.updateMetadata(client, name, description);
    }
}
