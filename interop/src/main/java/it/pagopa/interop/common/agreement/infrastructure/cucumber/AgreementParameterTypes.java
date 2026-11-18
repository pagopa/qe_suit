package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("attiva|sospesa|bozza|archiviata|in attesa di approvazione")
    public AgreementState agreementState(String value) {
        return switch (value.toLowerCase()) {
            case "attiva" -> AgreementState.ACTIVE;
            case "sospesa" -> AgreementState.SUSPENDED;
            case "bozza" -> AgreementState.DRAFT;
            case "archiviata" -> AgreementState.ARCHIVED;
            case "in attesa di approvazione" -> AgreementState.PENDING;
            default -> throw new IllegalArgumentException("Unknown agreement state: " + value);
        };
    }

    @ParameterType("richiesta di fruizione \"([^\"]+)\"")
    public Agreement agreementByAlias(String alias) {
        return entityStore.getByAliasOrThrow(alias, Agreement.class);
    }
}
