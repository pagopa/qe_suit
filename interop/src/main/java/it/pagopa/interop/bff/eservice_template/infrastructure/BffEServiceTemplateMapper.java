package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
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
public interface BffEServiceTemplateMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "state", source = ".", qualifiedByName = "resolveTemplateState")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "riskAnalyses", ignore = true)
    EServiceTemplate toTemplateBase(EServiceTemplateDetails source);

    @Mapping(target = "version", expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))")
    @Mapping(target = "voucherLifespan", ignore = true)
    @Mapping(target = "dailyCallsPerConsumer", ignore = true)
    @Mapping(target = "dailyCallsTotal", ignore = true)
    @Mapping(target = "agreementApprovalPolicy", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "interfaceDocument", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "suspendedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "docs", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "audience", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "serverUrls", expression = "java(new java.util.ArrayList<>())")
    EServiceTemplateVersion toTemplateVersion(CompactEServiceTemplateVersion source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    EServiceTemplateVersionState toDomainVersionState(
            it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState source
    );

    default EServiceTemplate toEServiceTemplatePreservingVersions(
            EServiceTemplateDetails source,
            EServiceTemplate existingTemplate
    ) {
        if (source == null) {
            return existingTemplate;
        }

        EServiceTemplate mapped = toTemplateBase(source);
        List<EServiceTemplateVersion> mergedVersions = new ArrayList<>();

        if (existingTemplate != null && existingTemplate.getVersions() != null) {
            mergedVersions.addAll(existingTemplate.getVersions());
        }

        if (source.getVersions() != null) {
            source.getVersions().stream()
                    .map(this::toTemplateVersion)
                    .forEach(version -> upsertVersion(mergedVersions, version, true));
        }

        if (source.getDraftVersion() != null) {
            upsertVersion(mergedVersions, toTemplateVersion(source.getDraftVersion()), true);
        }

        List<it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis> riskAnalyses =
                existingTemplate != null && existingTemplate.getRiskAnalyses() != null
                        ? new ArrayList<>(existingTemplate.getRiskAnalyses())
                        : new ArrayList<>();

        return mapped.toBuilder()
                .versions(new ArrayList<>(mergedVersions))
                .riskAnalyses(riskAnalyses)
                .build();
    }

    @Named("resolveTemplateState")
    default EServiceTemplateState resolveTemplateState(EServiceTemplateDetails source) {
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

    private static void upsertVersion(
            List<EServiceTemplateVersion> versions,
            EServiceTemplateVersion incoming,
            boolean overwriteCompactFields
    ) {
        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion current = versions.get(i);
            if (current.getId().equals(incoming.getId())) {
                if (overwriteCompactFields) {
                    versions.set(i, current.toBuilder()
                            .version(incoming.getVersion())
                            .state(incoming.getState())
                            .build());
                }
                return;
            }
        }

        versions.add(incoming);
    }
}



