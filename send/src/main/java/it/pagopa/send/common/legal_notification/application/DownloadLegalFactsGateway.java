package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.List;

/**
 * Scarica le attestazioni opponibili a terzi della notifica indicata (presa in carico, avvenuta notifica, mancato
 * recapito, ...) e restituisce i nomi dei file ottenuti.
 */
public interface DownloadLegalFactsGateway extends Plugin<Channel> {
    List<String> downloadLegalFacts(String iun);
}
