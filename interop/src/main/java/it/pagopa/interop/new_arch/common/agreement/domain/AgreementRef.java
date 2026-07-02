package it.pagopa.interop.new_arch.common.agreement.domain;

import java.util.UUID;

public record AgreementRef(UUID id) {

    public static AgreementRef of(UUID id) {
        return new AgreementRef(id);
    }
}
