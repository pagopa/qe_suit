package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
        config = TestMapperConfig.class,
        uses = {
                BffEServiceDescriptorMapper.class,
                SharedMapper.class,
                BffCommonMapper.class
        }
)
public interface BffEServiceTemplateMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "state", source = ".", qualifiedByName = "resolveTemplateState")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "riskAnalyses", ignore = true)
    EServiceTemplate toTemplateBase(EServiceTemplateDetails source);

    @Mapping(
            target = "version",
            expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))"
    )
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

    @Mapping(target = "version", expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))")
    @Mapping(target = "interfaceDocument", expression = "java(toInterface(source.getInterface(), source.getEserviceTemplate().getId(), source.getId()))")
    @Mapping(target = "docs", source = "docs", qualifiedByName = "templateDocumentRefs")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "suspendedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "audience", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "serverUrls", expression = "java(new java.util.ArrayList<>())")
    EServiceTemplateVersion toDetailedTemplateVersion(EServiceTemplateVersionDetails source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    EServiceTemplateVersionState toDomainVersionState(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState source);

    @Mapping(target = "id", source = "source.eserviceTemplate.id")
    @Mapping(target = "creatorId", source = "source.eserviceTemplate.creator.id")
    @Mapping(target = "name", source = "source.eserviceTemplate.name")
    @Mapping(target = "description", source = "source.eserviceTemplate.description")
    @Mapping(target = "intendedTarget", source = "source.eserviceTemplate.intendedTarget")
    @Mapping(target = "technology", source = "source.eserviceTemplate.technology")
    @Mapping(target = "mode", source = "source.eserviceTemplate.mode")
    @Mapping(target = "personalData", source = "source.eserviceTemplate.personalData")
    @Mapping(target = "state", source = "existing.state")
    @Mapping(target = "createdAt", source = "existing.createdAt")
    @Mapping(target = "updatedAt", source = "existing.updatedAt")
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "riskAnalyses", expression = "java(resolveRiskAnalyses(source, existing))")
    EServiceTemplate toTemplateFromVersionDetails(EServiceTemplateVersionDetails source, EServiceTemplate existing);

    default EServiceTemplate toEServiceTemplatePreservingVersions(EServiceTemplateDetails source, EServiceTemplate existingTemplate) {
        if (source == null) {
            return existingTemplate;
        }

        EServiceTemplate mapped = toTemplateBase(source);

        List<EServiceTemplateVersion> mergedVersions =
                existingTemplate != null && existingTemplate.getVersions() != null
                        ? new ArrayList<>(existingTemplate.getVersions())
                        : new ArrayList<>();

        if (source.getVersions() != null) {
            source.getVersions().stream()
                    .map(this::toTemplateVersion)
                    .forEach(version -> upsertVersion(mergedVersions, version));
        }

        if (source.getDraftVersion() != null) {
            upsertVersion(
                    mergedVersions,
                    toTemplateVersion(source.getDraftVersion())
            );
        }

        List<EServiceRiskAnalysis> riskAnalyses =
                existingTemplate != null && existingTemplate.getRiskAnalyses() != null
                        ? new ArrayList<>(existingTemplate.getRiskAnalyses())
                        : new ArrayList<>();

        return mapped.toBuilder()
                .versions(mergedVersions)
                .riskAnalyses(riskAnalyses)
                .build();
    }

    default EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source) {
        return toEServiceTemplate(source, null);
    }

    default EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source, EServiceTemplate existing) {
        if (source == null) {
            return existing;
        }

        EServiceTemplateVersion version = toDetailedTemplateVersion(source);

        List<EServiceTemplateVersion> versions =
                existing != null && existing.getVersions() != null
                        ? new ArrayList<>(existing.getVersions())
                        : new ArrayList<>();

        int existingVersionIndex = -1;

        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion previous = versions.get(i);

            if (previous.getId().equals(version.getId())) {
                existingVersionIndex = i;

                EServiceTemplateInterface incomingInterface =
                        version.getInterfaceDocument();

                EServiceTemplateInterface previousInterface =
                        previous.getInterfaceDocument();

                if (incomingInterface != null
                        && previousInterface != null
                        && previousInterface.getId().equals(incomingInterface.getId())) {
                    version = version.toBuilder()
                            .interfaceDocument(
                                    previousInterface.refreshMetadata(incomingInterface)
                            )
                            .build();
                }

                break;
            }
        }

        if (existingVersionIndex >= 0) {
            versions.set(existingVersionIndex, version);
        } else {
            versions.add(version);
        }

        return toTemplateFromVersionDetails(source, existing)
                .toBuilder()
                .versions(versions)
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

        boolean hasPublished = source.getVersions().stream()
                .anyMatch(version ->
                        version.getState()
                                == it.pagopa.interop.generated.openapi.clients.bff.model
                                .EServiceTemplateVersionState.PUBLISHED
                );

        return hasPublished
                ? EServiceTemplateState.PUBLISHED
                : EServiceTemplateState.UNKNOWN;
    }

    private static void upsertVersion(List<EServiceTemplateVersion> versions, EServiceTemplateVersion incoming) {
        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion current = versions.get(i);

            if (current.getId().equals(incoming.getId())) {
                versions.set(
                        i,
                        current.toBuilder()
                                .version(incoming.getVersion())
                                .state(incoming.getState())
                                .build()
                );
                return;
            }
        }

        versions.add(incoming);
    }

    default EServiceTemplateInterface toInterface(EServiceDoc source, UUID templateId, UUID versionId) {
        return source == null
                ? null
                : new EServiceTemplateInterface(
                source.getId(),
                new EServiceTemplateRef(templateId),
                new EServiceTemplateVersionRef(versionId),
                source.getName(),
                source.getContentType(),
                source.getPrettyName(),
                source.getChecksum(),
                null
        );
    }

    default DocumentRef toDocumentRef(EServiceDoc source) {
        return source == null
                ? null
                : DocumentRef.builder()
                .id(source.getId())
                .build();
    }

    @Named("templateDocumentRefs")
    default List<DocumentRef> toDocumentRefs(List<EServiceDoc> source) {
        return source == null
                ? new ArrayList<>()
                : source.stream()
                .map(this::toDocumentRef)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default List<EServiceRiskAnalysis> resolveRiskAnalyses(EServiceTemplateVersionDetails source, EServiceTemplate existing) {
        var riskAnalyses = source.getEserviceTemplate().getRiskAnalysis();

        if (riskAnalyses == null) {
            return existing != null && existing.getRiskAnalyses() != null
                    ? new ArrayList<>(existing.getRiskAnalyses())
                    : new ArrayList<>();
        }

        return riskAnalyses.stream()
                .map(riskAnalysis -> EServiceRiskAnalysis.builder()
                        .id(riskAnalysis.getId())
                        .name(riskAnalysis.getName())
                        .riskAnalysisForm(
                                riskAnalysis.getRiskAnalysisForm() == null
                                        ? null
                                        : RiskAnalysisForm.builder()
                                        .version(
                                                riskAnalysis.getRiskAnalysisForm()
                                                        .getVersion()
                                        )
                                        .answers(
                                                riskAnalysis.getRiskAnalysisForm()
                                                        .getAnswers()
                                        )
                                        .build()
                        )
                        .build()
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }
}