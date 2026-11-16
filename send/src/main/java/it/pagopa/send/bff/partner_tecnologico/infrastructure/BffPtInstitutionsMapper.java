package it.pagopa.send.bff.partner_tecnologico.infrastructure;

import it.pagopa.send.common.partner_tecnologico.domain.PtDelegationStatus;
import it.pagopa.send.common.partner_tecnologico.domain.PtInstitution;
import it.pagopa.send.generated.openapi.clients.bff.model.BffPtInstitution;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/** Dagli enti deleganti del BFF al dominio. */
@Component
public class BffPtInstitutionsMapper {

    public PtInstitution toDomain(BffPtInstitution institution) {
        return new PtInstitution(
                institution.getInstitutionId(),
                institution.getInstitutionName(),
                institution.getInstitutionTaxCode(),
                PtDelegationStatus.valueOf(institution.getStatus().getValue()),
                OffsetDateTime.parse(institution.getStatusChangedAt()));
    }
}
