package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.List;

/**
 * Legge gli avvisi di avvenuta ricezione disponibili per la notifica indicata, uno per destinatario, come li elenca il
 * canale (es. "Avviso di avvenuta ricezione - Mario Rossi (RSSMRA80A01H501U)").
 */
public interface ReadReceiptNoticesGateway extends Plugin<Channel> {
    List<String> readReceiptNotices(String iun);
}
