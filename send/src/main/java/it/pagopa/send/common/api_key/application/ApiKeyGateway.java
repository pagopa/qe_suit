package it.pagopa.send.common.api_key.application;

import it.pagopa.send.common.api_key.domain.ApiKey;
import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

/**
 * API Key dell'ente mittente: ogni implementazione sa come gestirle sul proprio canale.
 */
public interface ApiKeyGateway extends Plugin<Channel> {
    ApiKey createApiKey(String name);
}
