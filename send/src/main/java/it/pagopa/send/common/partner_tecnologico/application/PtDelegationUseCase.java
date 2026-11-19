package it.pagopa.send.common.partner_tecnologico.application;

import it.pagopa.send.common.partner_tecnologico.domain.PtInstitution;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Casi d'uso per le deleghe degli enti ai Partner Tecnologici: leggere gli enti deleganti e creare una delega. */
@Service
@RequiredArgsConstructor
public class PtDelegationUseCase {

    private final PtDelegationGateway ptDelegationGateway;

    public List<PtInstitution> findInstitutions(User partner, int limit) {
        return ptDelegationGateway.findInstitutions(partner, limit);
    }

    public boolean hasInstitutions(User partner) {
        return !ptDelegationGateway.findInstitutions(partner, 1).isEmpty();
    }

    /** Crea la delega e attende che arrivi a SEND. */
    public void createDelegation(User delegator, User partner) {
        ptDelegationGateway.createDelegation(delegator, partner);
        ptDelegationGateway.awaitInstitutions(partner);
    }
}
