package it.pagopa.send.bff.delegation.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.common.delegation.application.DelegationCreationCommand;
import it.pagopa.send.common.delegation.application.DelegationGateway;
import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.delegation.domain.DelegationStatus;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementazione BFF di {@link DelegationGateway}: le chiamate usano il session token dell'utente che fa l'operazione.
 * Solo questa classe (e il co-locato {@link BffDelegationMapper}) conosce i DTO OpenAPI del BFF.
 */
@Service
@RequiredArgsConstructor
public class BffDelegationGateway implements DelegationGateway {

    private static final int CREATED = 201;
    private static final int NO_CONTENT = 204;

    private final BffDelegationRestClient restClient;
    private final BffDelegationMapper mapper;
    private final DynamicUserTokenProvider tokenProvider;
    private final EntityStore entityStore;

    @Override
    public Optional<Delegation> findDelegation(User delegator, User delegate) {
        Optional<Delegation> delegation = restClient.searchByDelegate(
                        sessionToken(delegate),
                        mapper.toSearchRequest(delegator, List.of(DelegationStatus.ACTIVE, DelegationStatus.PENDING)))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get()
                .getResultsPage().stream()
                .map(mapper::toDomain)
                .findFirst();
        delegation.ifPresent(entityStore::upsert);
        return delegation;
    }

    // la risposta è vuota: l'id della delega si legge poi con findDelegation
    @Override
    public void createDelegation(User delegator, User delegate, DelegationCreationCommand command) {
        restClient.create(sessionToken(delegator), mapper.toNewMandateRequest(delegate, command))
                .withoutPolling()
                .assertStatusCode(CREATED);
    }

    @Override
    public void acceptDelegation(User delegate, Delegation delegation, String verificationCode) {
        restClient.accept(sessionToken(delegate), delegation.getMandateId(), mapper.toAcceptRequest(verificationCode))
                .withoutPolling()
                .assertStatusCode(NO_CONTENT);
        entityStore.upsert(delegation.toBuilder().status(DelegationStatus.ACTIVE).build());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    private String sessionToken(User user) {
        return tokenProvider.requireToken(user.getUsername());
    }
}
