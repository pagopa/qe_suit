package it.pagopa.interop.common.agreement.domain;

import javax.annotation.Nonnull;
import java.util.UUID;

public record AgreementRef(@Nonnull UUID id) {
    public static AgreementRef of(UUID id) {
        return new AgreementRef(id);
    }
}
