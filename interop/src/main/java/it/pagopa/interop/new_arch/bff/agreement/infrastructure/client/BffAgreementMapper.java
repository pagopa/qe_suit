package it.pagopa.interop.new_arch.bff.agreement.infrastructure.client;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementDelegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementsEService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.new_arch.common.infrastructure.utils.SharedMapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface BffAgreementMapper {

    @Mapping(target = "producerId", source = "producer")
    @Mapping(target = "consumerId", source = "consumer")
    @Mapping(target = "eserviceId", source = "eservice")
    @Mapping(target = "delegationId", source = "delegation")
    Agreement toAgreement(
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