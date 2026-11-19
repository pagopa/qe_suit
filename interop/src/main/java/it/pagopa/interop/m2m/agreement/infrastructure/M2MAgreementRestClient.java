package it.pagopa.interop.m2m.agreement.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.AgreementSubmission;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MAgreementRestClient extends RestClient {

    private final AgreementsApi agreementsApi;

    public M2MAgreementRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.agreementsApi = apiClient.agreements();
    }

    public TestChain<Agreement> create(@Nonnull AgreementSeed payload) {
        return execute(
                () -> agreementsApi.createAgreement().body(payload).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<Agreement> submit(@Nonnull UUID agreementId, @Nonnull AgreementSubmission payload) {
        return execute(
                () -> agreementsApi.submitAgreement()
                        .agreementIdPath(agreementId)
                        .body(payload)
                        .execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<Agreement> read(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.getAgreement()
                        .agreementIdPath(agreementId)
                        .execute(Function.identity()),
                Agreement.class
        );
    }
}
