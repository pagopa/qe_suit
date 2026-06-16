package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurposeMapper {

    Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Purpose dto);

    Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig dto);

    Purpose toDomain(PurposeVersionResource dto);

    default Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Purpose> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Purposes dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Purpose> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.RemainingDailyCallsResponse dto) {
        return List.of();
    }

    default Purpose toDomain(Void dto) {
        return null;
    }

    default Purpose toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }
}