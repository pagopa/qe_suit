package it.pagopa.interop.bff.service.mapper;

import it.pagopa.interop.bff.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementDelegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementsEService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface AgreementMapper {

    @Mapping(target = "producerId", source = "producer")
    @Mapping(target = "consumerId", source = "consumer")
    @Mapping(target = "eserviceId", source = "eservice")
    @Mapping(target = "delegationId", source = "delegation")
    it.pagopa.interop.common.contract.model.agreement.Agreement toAgreement(
            it.pagopa.interop.generated.openapi.clients.bff.model.Agreement source
    );

    default UUID map(CompactOrganization source) {
        return source == null ? null : source.getId();
    }

    default UUID map(Tenant source) {
        return source == null ? null : source.getId();
    }

    default UUID map(AgreementsEService source) {
        return source == null ? null : source.getId();
    }

    default UUID map(AgreementDelegation source) {
        return source == null ? null : source.getId();
    }
}