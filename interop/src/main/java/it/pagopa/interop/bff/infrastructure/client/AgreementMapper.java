package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.agreement.Agreement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = StrictMapperConfig.class,
        uses = {
                TenantMapper.class,
                EServiceMapper.class,
                AttributeMapper.class
        })
public interface AgreementMapper {

    //TODO: AgreementsEService -> EService, VerifiedAttribute -> Attribute, CertifiedAttribute -> Attribute, CertifiedDiscreteAttribute -> Attribute, DeclaredAttribute -> Attribute

    @Mapping(target = "delegationId", source = "delegation.id")
    @Mapping(target = "delegate", source = "delegation.delegate")
    Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Agreement dto);

    Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight dto);

    default Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
    }

    default Agreement toDomain(Void dto) {
        return null;
    }

    default Agreement toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Agreements dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.HasCertifiedAttributes dto) {
        return List.of();
    }
}