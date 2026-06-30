package it.pagopa.interop.bff.service;

import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.contract.model.agreement.Agreement;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.common.utils.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgreementService {

    private final AgreementsApi agreementsApi;
    private final ScenarioContext context;

    public Agreement createAgreement(EService eservice) {
        return createAgreement(eservice, null);
    }

    public Agreement createAgreement(EService eservice, UUID delegationId) {
        AgreementPayload request = buildAgreementPayload(eservice, Optional.ofNullable(delegationId));
        UUID agreementId = agreementsApi.createAgreement(request).getId();
        return getAgreement(agreementId);
    }

    public Agreement getAgreement(UUID agreementId) {
        Agreement agreement = pollAgreement(
                () -> null,
                a -> a.getId().equals(agreementId)
        );
        context.upsert(agreement);
        return agreement;
    }

    public Agreement submitAgreement(Agreement agreement) {
        ResponseEntity<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement> submitted =
                PollingUtils.pollUntil(
                        () -> agreementsApi.submitAgreementWithHttpInfo(agreement.getId(), new AgreementSubmissionPayload()),
                        resp -> resp != null && resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null
                                && resp.getBody().getState() == AgreementState.ACTIVE,
                        Duration.ofSeconds(20),
                        Duration.ofSeconds(2)
                );

        if (submitted.getBody() == null) {
            throw new IllegalStateException("submitAgreement returned 2xx but empty body for agreement " + agreement.getId());
        }

        Agreement result =  null;
        context.upsert(result);
        return result;
    }

    public Agreement publishAgreement(Agreement agreement) {
        UUID agreementId = agreement.getId();
        agreementsApi.activateAgreement(agreementId, null);

        Agreement activatedAgreement = pollAgreement(
                () -> null,
                a -> a.getId().equals(agreementId) && a.getState().name().equals(AgreementState.ACTIVE.name())
        );
        context.upsert(activatedAgreement);
        return activatedAgreement;
    }

    private Agreement pollAgreement(Supplier<Agreement> supplier, java.util.function.Predicate<Agreement> predicate) {
        return PollingUtils.pollUntil(
                supplier,
                predicate,
                Duration.ofSeconds(15),
                Duration.ofSeconds(1)
        );
    }

    private AgreementPayload buildAgreementPayload(EService eservice, Optional<UUID> delegationId) {
        AgreementPayload request = new AgreementPayload();
        request.setEserviceId(eservice.getId());
        request.setDescriptorId(eservice.getDescriptors().get(eservice.getDescriptors().size() - 1).getId());
        request.setDelegationId(delegationId.orElse(null));
        return request;
    }
}