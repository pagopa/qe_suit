package it.pagopa.send.common.partner_tecnologico.domain;

import java.time.OffsetDateTime;

/**
 * Ente delegante di un Partner Tecnologico: nome, codice fiscale, stato della delega e data dell'ultimo cambio di
 * stato.
 */
public record PtInstitution(
        String institutionId,
        String name,
        String taxCode,
        PtDelegationStatus status,
        OffsetDateTime statusChangedAt
) {
}
