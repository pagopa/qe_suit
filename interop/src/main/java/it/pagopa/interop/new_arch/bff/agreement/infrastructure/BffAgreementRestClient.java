package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.infrastructure.template.RestGateway;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffAgreementRestClient extends RestGateway {

    private final AgreementsApi agreementsApi;

    public BffAgreementRestClient(ApiClient apiClient) {
        this.agreementsApi = apiClient.agreements();
    }

    public TestChain<CreatedResource, Agreement> create(@Nonnull AgreementPayload payload) {
        return execute(
                () -> agreementsApi.createAgreement().body(payload).execute(Function.identity()),
                CreatedResource.class,
                Agreement.class
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> read(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.getAgreementById().agreementIdPath(agreementId).execute(Function.identity()),
                it.pagopa.interop.generated.openapi.clients.bff.model.Agreement.class,
                Agreement.class
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> submit(@Nonnull UUID agreementId, @Nonnull AgreementSubmissionPayload payload) {
        return execute(
                () -> agreementsApi.submitAgreement().agreementIdPath(agreementId).body(payload).execute(Function.identity()),
                it.pagopa.interop.generated.openapi.clients.bff.model.Agreement.class,
                Agreement.class
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> activate(@Nonnull UUID agreementId, @Nullable UUID delegationId) {
        var payload = new DelegationRef().delegationId(delegationId);

        return execute(
                () -> agreementsApi.activateAgreement().agreementIdPath(agreementId).body(payload).execute(Function.identity()),
                it.pagopa.interop.generated.openapi.clients.bff.model.Agreement.class,
                Agreement.class
        );
    }
}
