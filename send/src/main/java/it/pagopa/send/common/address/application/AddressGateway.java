package it.pagopa.send.common.address.application;

import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

/**
 * Recapiti dell'utente dello scenario (domicilio digitale SERCQ e indirizzo di cortesia): ogni implementazione sa
 * come gestirli sul proprio canale.
 */
public interface AddressGateway extends Plugin<Channel> {
    void enableSercqDigitalDomicile(String email);

    void disableSercqDigitalDomicile();

    void removeCourtesyAddress();
}
