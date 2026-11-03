package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResources;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateWithCompactCreator;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffPurposeTemplateMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "title", source = "purposeTitle")
    @Mapping(target = "description", source = "purposeDescription")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "linkedEServiceIds", ignore = true)
    @Mapping(target = "linkedResourceIds", ignore = true)
    @Mapping(target = "riskAnalysisAnswerIds", source = "purposeRiskAnalysisForm", qualifiedByName = "mapRiskAnalysisAnswerIds")
    PurposeTemplate toPurposeTemplate(PurposeTemplateWithCompactCreator source);

    @Named("mapRiskAnalysisAnswerIds")
    default Map<String, UUID> mapRiskAnalysisAnswerIds(RiskAnalysisFormTemplate form) {
        if (form == null || form.getAnswers() == null) {
            return Map.of();
        }
        Map<String, UUID> result = new LinkedHashMap<>();
        for (Map.Entry<String, RiskAnalysisTemplateAnswer> entry : form.getAnswers().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getValue().getId() != null) {
                result.put(entry.getKey(), entry.getValue().getId());
            }
        }
        return result;
    }

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    PurposeTemplateState toDomainState(
            it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateState source
    );

    default PurposeTemplate toPurposeTemplate(
            PurposeTemplateWithCompactCreator source,
            PurposeTemplate existing
    ) {
        if (source == null) {
            return existing;
        }

        PurposeTemplate mapped = toPurposeTemplate(source);
        if (existing == null) {
            return mapped;
        }

        Map<String, UUID> answerIds = (mapped.getRiskAnalysisAnswerIds() != null && !mapped.getRiskAnalysisAnswerIds().isEmpty())
                ? mapped.getRiskAnalysisAnswerIds()
                : (existing.getRiskAnalysisAnswerIds() != null ? existing.getRiskAnalysisAnswerIds() : Map.of());

        return mapped.toBuilder()
                .clearLinkedEServiceIds()
                .linkedEServiceIds(existing.getLinkedEServiceIds() != null ? existing.getLinkedEServiceIds() : List.of())
                .clearLinkedResourceIds()
                .linkedResourceIds(existing.getLinkedResourceIds() != null ? existing.getLinkedResourceIds() : List.of())
                .clearRiskAnalysisAnswerIds()
                .riskAnalysisAnswerIds(answerIds)
                .build();
    }

    default PurposeTemplate toPurposeTemplateWithLinkedResources(
            PurposeTemplate existing,
            LinkableResources linkableResources
    ) {
        if (existing == null || linkableResources == null || linkableResources.getResults() == null) {
            return existing;
        }

        Set<UUID> eserviceIds = new LinkedHashSet<>(
                existing.getLinkedEServiceIds() != null ? existing.getLinkedEServiceIds() : List.of()
        );
        Set<UUID> resourceIds = new LinkedHashSet<>(
                existing.getLinkedResourceIds() != null ? existing.getLinkedResourceIds() : List.of()
        );

        for (LinkableResource resource : linkableResources.getResults()) {
            if (resource == null) {
                continue;
            }
            if (resource.getEservice() != null && resource.getEservice().getId() != null) {
                UUID eserviceId = resource.getEservice().getId();
                eserviceIds.add(eserviceId);
                resourceIds.add(eserviceId);
            }
            if (resource.getEserviceTemplate() != null && resource.getEserviceTemplate().getId() != null) {
                resourceIds.add(resource.getEserviceTemplate().getId());
            }
        }

        return existing.toBuilder()
                .clearLinkedEServiceIds()
                .linkedEServiceIds(eserviceIds)
                .clearLinkedResourceIds()
                .linkedResourceIds(resourceIds)
                .build();
    }
}

