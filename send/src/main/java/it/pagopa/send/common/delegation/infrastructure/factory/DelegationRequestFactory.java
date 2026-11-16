package it.pagopa.send.common.delegation.infrastructure.factory;

import it.pagopa.send.common.delegation.application.DelegationCreationCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Dati di default per creare una delega: un codice di verifica fisso, così chi accetta la delega in un giro successivo
 * lo conosce, e una validità di un anno, così la delega resta disponibile per i giri successivi.
 */
@Component
public class DelegationRequestFactory {

    private static final String VERIFICATION_CODE = "24680";

    public DelegationCreationCommand defaultCreation() {
        return new DelegationCreationCommand(VERIFICATION_CODE, LocalDate.now().plusYears(1));
    }
}
