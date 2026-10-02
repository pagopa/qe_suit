package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceTemplateVersionMapper {

    DocumentRef toDocumentRef(EServiceDoc source);

    @Mapping(target = "version", expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))")
    @Mapping(target = "interfaceDocument", source = "interface")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "suspendedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "docs", source = "docs")
    @Mapping(target = "audience", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "serverUrls", expression = "java(new java.util.ArrayList<>())")
    EServiceTemplateVersion toTemplateVersion(EServiceTemplateVersionDetails source);

    @Mapping(target = "id", source = "eserviceTemplate.id")
    @Mapping(target = "creatorId", source = "eserviceTemplate.creator.id")
    @Mapping(target = "name", source = "eserviceTemplate.name")
    @Mapping(target = "description", source = "eserviceTemplate.description")
    @Mapping(target = "intendedTarget", source = "eserviceTemplate.intendedTarget")
    @Mapping(target = "technology", source = "eserviceTemplate.technology")
    @Mapping(target = "mode", source = "eserviceTemplate.mode")
    @Mapping(target = "state", source = "eserviceTemplate", qualifiedByName = "resolveTemplateState")
    @Mapping(target = "personalData", source = "eserviceTemplate.personalData")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "riskAnalyses", ignore = true)
    EServiceTemplate toTemplateBase(EServiceTemplateVersionDetails source);

    @Mapping(target = "certified", source = "certified", qualifiedByName = "flattenAttributes")
    @Mapping(target = "declared", source = "declared", qualifiedByName = "flattenAttributes")
    @Mapping(target = "verified", source = "verified", qualifiedByName = "flattenAttributes")
    Attributes toAttributes(DescriptorAttributes source);

    it.pagopa.interop.common.attribute.domain.Attribute toAttribute(DescriptorAttribute source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    EServiceTemplateVersionState toDomainVersionState(
            it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState source
    );

    default EServiceTemplate toTemplateWithUpsertVersion(
            EServiceTemplateVersionDetails source,
            EServiceTemplate existingTemplate
    ) {
        if (source == null) {
            return existingTemplate;
        }

        EServiceTemplate updatedBase = toTemplateBase(source);
        EServiceTemplateVersion newVersion = toTemplateVersion(source);

        List<EServiceTemplateVersion> finalVersions = new ArrayList<>();
        if (existingTemplate != null && existingTemplate.getVersions() != null) {
            finalVersions.addAll(existingTemplate.getVersions());
        }

        upsertVersion(finalVersions, newVersion);

        List<it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis> riskAnalyses =
                existingTemplate != null && existingTemplate.getRiskAnalyses() != null
                        ? new ArrayList<>(existingTemplate.getRiskAnalyses())
                        : new ArrayList<>();

        return updatedBase.toBuilder()
                .versions(new ArrayList<>(finalVersions))
                .riskAnalyses(riskAnalyses)
                .build();
    }

    @Named("resolveTemplateState")
    default EServiceTemplateState resolveTemplateState(
            it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails source
    ) {
        if (source == null) {
            return EServiceTemplateState.UNKNOWN;
        }

        if (source.getDraftVersion() != null) {
            return EServiceTemplateState.DRAFT;
        }

        if (source.getVersions() == null || source.getVersions().isEmpty()) {
            return EServiceTemplateState.UNKNOWN;
        }

        boolean hasPublished = source.getVersions().stream().anyMatch(
                version -> version.getState() == it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState.PUBLISHED
        );

        return hasPublished ? EServiceTemplateState.PUBLISHED : EServiceTemplateState.UNKNOWN;
    }

    @Named("flattenAttributes")
    default List<it.pagopa.interop.common.attribute.domain.Attribute> flattenAttributes(
            List<List<DescriptorAttribute>> nestedList
    ) {
        if (nestedList == null) {
            return List.of();
        }

        List<it.pagopa.interop.common.attribute.domain.Attribute> flattenedList = new ArrayList<>();

        for (int i = 0; i < nestedList.size(); i++) {
            List<DescriptorAttribute> innerList = nestedList.get(i);
            if (innerList == null) {
                continue;
            }

            for (DescriptorAttribute bffAttr : innerList) {
                if (bffAttr == null) {
                    continue;
                }

                it.pagopa.interop.common.attribute.domain.Attribute mapped = toAttribute(bffAttr);
                flattenedList.add(mapped.toBuilder().group(i).build());
            }
        }

        return flattenedList;
    }

    private static void upsertVersion(List<EServiceTemplateVersion> versions, EServiceTemplateVersion incoming) {
        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion current = versions.get(i);
            if (current.getId().equals(incoming.getId())) {
                versions.set(i, incoming);
                return;
            }
        }

        versions.add(incoming);
    }
}



