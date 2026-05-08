package it.pagopa.interop.domain.services.agreement.impl;

import it.pagopa.interop.domain.context.AgreementContext;
import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.services.agreement.AgreementService;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgreementDataPreparationService implements AgreementService {

    private final AgreementsApi agreementsApi;
    private final AgreementContext context;

    @Override
    public Agreement createEserviceAgreement(Eservice eservice) {
        AgreementPayload request = buildAgreementPayload(eservice, null);
        UUID agreementId = agreementsApi.createAgreement(request).getId();

        return getAgreement(agreementId);
    }

    @Override
    public Agreement createEserviceAgreement(Eservice eservice, UUID delegationId) {
        AgreementPayload request = buildAgreementPayload(eservice, delegationId);
        UUID agreementId = agreementsApi.createAgreement(request).getId();

        return getAgreement(agreementId);
    }

    @Override
    public Agreement getAgreement(UUID agreementId) {
        Agreement agreement = PollingUtils.pollUntil(
                () -> new Agreement(agreementsApi.getAgreementById(agreementId)),
                resp -> resp.getId().equals(agreementId),
                Duration.ofSeconds(15),
                Duration.ofSeconds(1)
        );

        context.upsert(agreement);
        return agreement;
    }

    @Override
    public Agreement publishAgreement(Agreement agreement) {
        UUID agreementId = agreement.getId();
        agreementsApi.activateAgreement(agreementId, null);

        Agreement activatedAgreement = PollingUtils.pollUntil(
                () -> new Agreement(agreementsApi.getAgreementById(agreementId)),
                resp -> resp.getId().equals(agreementId) && resp.getState() == AgreementState.ACTIVE,
                Duration.ofSeconds(15),
                Duration.ofSeconds(1)
        );

        context.upsert(activatedAgreement);
        return activatedAgreement;
    }

    private AgreementPayload buildAgreementPayload(Eservice eservice, UUID delegationId) {
        UUID eserviceId = eservice.getEserviceId();
        UUID lastDescriptorId = eservice.getLastDescriptorId();

        AgreementPayload request = new AgreementPayload();
        request.setEserviceId(eserviceId);
        request.setDescriptorId(lastDescriptorId);
        request.setDelegationId(delegationId);

        return request;
    }
}
