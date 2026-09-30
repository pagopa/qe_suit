package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffPurposeMapper {

    @Mapping(target = "consumerId", source = "consumer.id")
    @Mapping(target = "eserviceId", source = "eservice.id")
    @Mapping(target = "delegationId", source = "delegation.id")
    Purpose toPurpose(
            it.pagopa.interop.generated.openapi.clients.bff.model.Purpose purpose
    );

    @Mapping(target = "purposeVersionState", source = "state")
    @Mapping(
            target = "createdAt",
            source = "createdAt",
            qualifiedByName = "mapStringToInstant"
    )
    @Mapping(
            target = "updatedAt",
            source = "updatedAt",
            qualifiedByName = "mapStringToInstant"
    )
    @Mapping(
            target = "suspendedAt",
            source = "suspendedAt",
            qualifiedByName = "mapStringToInstant"
    )
    PurposeVersion toPurposeVersion(
            it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion purposeVersion
    );
}