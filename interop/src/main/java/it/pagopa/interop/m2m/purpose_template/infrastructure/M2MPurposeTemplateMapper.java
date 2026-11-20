package it.pagopa.interop.m2m.purpose_template.infrastructure;

import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import java.util.List;
import java.util.Map;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class}
)
public interface M2MPurposeTemplateMapper {

    @Mapping(target = "title", source = "purposeTitle")
    @Mapping(target = "description", source = "purposeDescription")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "riskAnalysisFormTemplate", ignore = true)
    @Mapping(target = "linkedEServiceIds", expression = "java(java.util.List.of())")
    @Mapping(target = "linkedResourceIds", expression = "java(java.util.List.of())")
    @Mapping(target = "riskAnalysisAnswerIds", expression = "java(java.util.Map.of())")
    PurposeTemplate toPurposeTemplate(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplate source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    PurposeTemplateState toDomainState(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplateState source);

    default PurposeTemplate toPurposeTemplate(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplate source,
            PurposeTemplate existing
    ) {
        if (source == null) {
            return existing;
        }

        PurposeTemplate mapped = toPurposeTemplate(source);
        if (existing == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .riskAnalysisFormTemplate(existing.getRiskAnalysisFormTemplate())
                .clearLinkedEServiceIds()
                .linkedEServiceIds(existing.getLinkedEServiceIds() != null
                        ? existing.getLinkedEServiceIds() : List.of())
                .clearLinkedResourceIds()
                .linkedResourceIds(existing.getLinkedResourceIds() != null
                        ? existing.getLinkedResourceIds() : List.of())
                .clearRiskAnalysisAnswerIds()
                .riskAnalysisAnswerIds(existing.getRiskAnalysisAnswerIds() != null
                        ? existing.getRiskAnalysisAnswerIds() : Map.of())
                .build();
    }
}
