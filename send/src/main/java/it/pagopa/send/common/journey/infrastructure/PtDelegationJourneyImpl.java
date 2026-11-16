package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.send.common.journey.application.PtDelegationJourney;
import it.pagopa.send.common.partner_tecnologico.application.PtDelegationUseCase;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Composer fluente per le deleghe degli enti ai Partner Tecnologici, sul modello di {@link DelegationJourneyImpl}:
 * bean singleton senza stato proprio.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PtDelegationJourneyImpl implements PtDelegationJourney<PtDelegationJourneyImpl> {

    private final PtDelegationUseCase ptDelegationUseCase;

    @Override
    public PtDelegationJourneyImpl withPtDelegation(User delegator, User partner) {
        if (!ptDelegationUseCase.hasInstitutions(partner)) {
            ptDelegationUseCase.createDelegation(delegator, partner);
            log.info("Delega di {} creata verso il PT {}", delegator.getOrganization(), partner.getOrganization());
        }
        return this;
    }
}
