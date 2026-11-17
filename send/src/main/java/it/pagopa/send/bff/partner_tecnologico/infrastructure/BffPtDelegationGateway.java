package it.pagopa.send.bff.partner_tecnologico.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.partner_tecnologico.application.PtDelegationGateway;
import it.pagopa.send.common.partner_tecnologico.domain.PtInstitution;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.bff.model.BffPtInstitutionsResponse;
import it.pagopa.send.selfcare.infrastructure.SelfcareDelegationsRestClient;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementazione BFF di {@link PtDelegationGateway}: gli enti deleganti si leggono dal BFF con il session token del
 * PT, la delega si crea con l'API di test di Selfcare, da cui arriva a SEND in modo asincrono.
 * Solo questa classe (e il co-locato {@link BffPtInstitutionsMapper}) conosce i DTO OpenAPI del BFF.
 */
@Service
@RequiredArgsConstructor
public class BffPtDelegationGateway implements PtDelegationGateway {

    private final BffPtInstitutionsRestClient restClient;
    private final BffPtInstitutionsMapper mapper;
    private final SelfcareDelegationsRestClient selfcareDelegationsRestClient;
    private final DynamicUserTokenProvider tokenProvider;

    @Override
    public List<PtInstitution> findInstitutions(User partner, int limit) {
        return institutions(partner, limit, PollingStrategy.UNTIL_SUCCESS);
    }

    @Override
    public void createDelegation(User delegator, User partner) {
        selfcareDelegationsRestClient.createPtDelegation(delegator, partner);
    }

    // la delega appena creata può non essere ancora restituita: la lettura si ripete finché non compare
    @Override
    public void awaitInstitutions(User partner) {
        institutions(partner, 1, response -> response.isSuccess()
                && !response.as(BffPtInstitutionsResponse.class).getItems().isEmpty());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    private List<PtInstitution> institutions(User partner, int limit, PollingStrategy strategy) {
        return restClient.getInstitutions(tokenProvider.requireToken(partner.getSessionTokenKey()), limit)
                .withPolling(strategy)
                .get()
                .getItems().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
