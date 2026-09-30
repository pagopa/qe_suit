package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffAgreementRestClient extends RestClient {

    private final AgreementsApi agreementsApi;

    public BffAgreementRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.agreementsApi = apiClient.agreements();
    }

    public TestChain<CreatedResource> create(@Nonnull AgreementPayload payload) {
        return execute(
                () -> agreementsApi.createAgreement().body(payload).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Agreement> read(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.getAgreementById().agreementIdPath(agreementId).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<Agreement> submit(@Nonnull UUID agreementId, @Nonnull AgreementSubmissionPayload payload) {
        return execute(
                () -> agreementsApi.submitAgreement().agreementIdPath(agreementId).body(payload).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<Agreement> activate(@Nonnull UUID agreementId, @Nullable UUID delegationId) {
        var payload = new DelegationRef().delegationId(delegationId);

        return execute(
                () -> agreementsApi.approveAgreement().agreementIdPath(agreementId).body(payload).execute(Function.identity()),
                Agreement.class
        );
    }
}
