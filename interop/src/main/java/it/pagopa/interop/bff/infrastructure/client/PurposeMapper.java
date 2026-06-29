package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface PurposeMapper {
    Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Purpose dto);
    Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig dto);

    default Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
    }
    default Purpose toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource dto) {
        return null;
    }
    default Purpose toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }
    default Purpose toDomain(Void dto) {
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
    default List<Purpose> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Purposes dto) {
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
    default List<Purpose> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.RemainingDailyCallsResponse dto) {
        return List.of();
    }
}