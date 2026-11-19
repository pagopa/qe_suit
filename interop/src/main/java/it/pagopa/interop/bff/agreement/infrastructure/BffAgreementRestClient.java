package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
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

    public TestChain<Agreements> activeProducerAgreements(UUID eServiceId, int offset, int limit) {
        return execute(() -> agreementsApi.getProducerAgreements()
                        .eservicesIdsQuery(eServiceId).statesQuery(AgreementState.ACTIVE.getValue())
                        .offsetQuery(offset).limitQuery(limit).execute(Function.identity()),
                Agreements.class);
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

    public TestChain<Agreement> suspend(@Nonnull UUID agreementId, @Nullable UUID delegationId) {
        var payload = new DelegationRef().delegationId(delegationId);
        return execute(
                () -> agreementsApi.suspendAgreement().agreementIdPath(agreementId)
                        .body(payload).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<Void> archive(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.archiveAgreement().agreementIdPath(agreementId).reqSpec(reqSpec -> reqSpec.setContentType("application/json")).execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Agreement> unsuspend(@Nonnull UUID agreementId, @Nullable UUID delegationId) {
        var payload = new DelegationRef().delegationId(delegationId);
        return execute(
                () -> agreementsApi.unsuspendAgreement().agreementIdPath(agreementId)
                        .body(payload).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<File> addConsumerDocument(
            @Nonnull UUID agreementId, @Nonnull String name, @Nonnull String prettyName, @Nonnull File document
    ) {
        return execute(
                () -> agreementsApi.addAgreementConsumerDocument().agreementIdPath(agreementId)
                        .nameForm(name).prettyNameForm(prettyName)
                        .reqSpec(reqSpec -> reqSpec.addMultiPart("doc", document, "application/octet-stream"))
                        .execute(Function.identity()),
                File.class
        );
    }

    public TestChain<Void> removeConsumerDocument(@Nonnull UUID agreementId, @Nonnull UUID documentId) {
        return execute(
                () -> agreementsApi.removeAgreementConsumerDocument()
                        .agreementIdPath(agreementId)
                        .documentIdPath(documentId)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> cloneAgreement(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.cloneAgreement().agreementIdPath(agreementId).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Agreement> reject(@Nonnull UUID agreementId, @Nonnull AgreementRejectionPayload payload) {
        return execute(
                () -> agreementsApi.rejectAgreement().agreementIdPath(agreementId).body(payload).execute(Function.identity()),
                Agreement.class
        );
    }

    public TestChain<File> downloadSignedDocument(@Nonnull UUID agreementId) {
        return execute(
                () -> agreementsApi.getSignedAgreementContract().agreementIdPath(agreementId).execute(Function.identity()),
                File.class
        );
    }

    public TestChain<File> getAgreementConsumerDocument(@Nonnull UUID agreementId, @Nonnull UUID documentId) {
        return execute(
                () -> agreementsApi.getAgreementConsumerDocument().agreementIdPath(agreementId).documentIdPath(documentId).execute(Function.identity()),
                File.class
        );
    }

    public TestChain<Void> deleteAgreementConsumerDocument(@Nonnull UUID agreementId, @Nonnull UUID documentId) {
        return execute(
                () -> agreementsApi.removeAgreementConsumerDocument().agreementIdPath(agreementId).documentIdPath(documentId).execute(Function.identity()),
                Void.class
        );
    }

}
