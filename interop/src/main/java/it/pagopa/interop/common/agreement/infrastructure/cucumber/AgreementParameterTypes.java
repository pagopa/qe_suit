package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.agreement.domain.AgreementState;

public class AgreementParameterTypes {

    @ParameterType("attiva|sospesa|bozza|archiviata")
    public AgreementState agreementState(String value) {
        return switch (value.toLowerCase()) {
            case "attiva" -> AgreementState.ACTIVE;
            case "sospesa" -> AgreementState.SUSPENDED;
            case "bozza" -> AgreementState.DRAFT;
            case "archiviata" -> AgreementState.ARCHIVED;
            default -> throw new IllegalArgumentException("Unknown agreement state: " + value);
        };
    }
}
