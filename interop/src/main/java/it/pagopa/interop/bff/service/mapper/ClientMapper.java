package it.pagopa.interop.bff.service.mapper;

import it.pagopa.interop.bff.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientPurpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface ClientMapper {

    @Mapping(target = "consumerId", source = "consumer")
    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "purposes", source = "purposes")
    Client toClient(
            it.pagopa.interop.generated.openapi.clients.bff.model.Client source
    );

    @Mapping(target = "id", source = "purposeId")
    @Mapping(target = "eserviceId", source = "eservice")
    @Mapping(target = "consumerId", ignore = true)
    @Mapping(target = "suspendedByConsumer", ignore = true)
    @Mapping(target = "suspendedByProducer", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isFreeOfCharge", ignore = true)
    @Mapping(target = "delegationId", ignore = true)
    @Mapping(target = "versions", ignore = true)
    Purpose toPurpose(ClientPurpose source);

    Set<Purpose> toPurposesSet(java.util.List<ClientPurpose> source);

    default Client toDomainWithUpsert(
            it.pagopa.interop.generated.openapi.clients.bff.model.Client source,
            Client current
    ) {
        Client mapped = toClient(source);

        if (current == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .keys(current.getKeys())
                .users(current.getUsers())
                .purposes(mergePurposes(mapped.getPurposes(), current.getPurposes()))
                .build();
    }

    default Set<Purpose> mergePurposes(Set<Purpose> mapped, Set<Purpose> current) {
        if (mapped == null || mapped.isEmpty()) {
            return current;
        }

        if (current == null || current.isEmpty()) {
            return mapped;
        }

        return mapped.stream()
                .map(mappedPurpose -> current.stream()
                        .filter(currentPurpose -> currentPurpose.getId().equals(mappedPurpose.getId()))
                        .findFirst()
                        .map(currentPurpose -> mappedPurpose.toBuilder()
                                .consumerId(currentPurpose.getConsumerId())
                                .suspendedByConsumer(currentPurpose.getSuspendedByConsumer())
                                .suspendedByProducer(currentPurpose.getSuspendedByProducer())
                                .description(currentPurpose.getDescription())
                                .createdAt(currentPurpose.getCreatedAt())
                                .updatedAt(currentPurpose.getUpdatedAt())
                                .isFreeOfCharge(currentPurpose.getIsFreeOfCharge())
                                .delegationId(currentPurpose.getDelegationId())
                                .versions(currentPurpose.getVersions())
                                .build())
                        .orElse(mappedPurpose))
                .collect(java.util.stream.Collectors.toSet());
    }

    default UUID map(CompactOrganization source) {
        return source == null ? null : source.getId();
    }

    default UUID map(CompactEService source) {
        return source == null ? null : source.getId();
    }
}