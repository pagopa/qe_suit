package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import org.springframework.plugin.core.Plugin;

public interface ClientRequestFactory extends Plugin<Channel> {
    ClientSeed creationRequest();
    KeySeed keyCreationRequest();
}
