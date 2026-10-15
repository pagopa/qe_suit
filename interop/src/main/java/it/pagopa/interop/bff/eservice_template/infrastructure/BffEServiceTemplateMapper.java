package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(
        config = TestMapperConfig.class,
        uses = {BffEServiceDescriptorMapper.class, SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceTemplateMapper {

    default EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source) {
        return toEServiceTemplate(source, null);
    }

    default EServiceTemplate toEServiceTemplate(
            EServiceTemplateVersionDetails source,
            EServiceTemplate existing
    ) {
        var template = source.getEserviceTemplate();
        var document = source.getInterface();
        EServiceTemplateVersion oldVersion = existing == null ? null : existing.getVersions().stream()
                .filter(version -> version.getId().equals(source.getId()))
                .findFirst()
                .orElse(null);
        EServiceTemplateInterface interfaceDocument = toInterface(document, template.getId(), source.getId());
        if (interfaceDocument != null && oldVersion != null && oldVersion.getInterfaceDocument() != null
                && oldVersion.getInterfaceDocument().getId().equals(interfaceDocument.getId())) {
            EServiceTemplateInterface previous = oldVersion.getInterfaceDocument();
            interfaceDocument = previous.refreshMetadata(interfaceDocument);
        }

        EServiceTemplateVersion version = EServiceTemplateVersion.builder()
                .id(source.getId())
                .version(source.getVersion() == null ? null : source.getVersion().toString())
                .state(EServiceTemplateVersionState.valueOf(source.getState().getValue()))
                .voucherLifespan(source.getVoucherLifespan())
                .dailyCallsPerConsumer(source.getDailyCallsPerConsumer())
                .dailyCallsTotal(source.getDailyCallsTotal())
                .agreementApprovalPolicy(source.getAgreementApprovalPolicy() == null
                        ? null
                        : AgreementApprovalPolicy.valueOf(source.getAgreementApprovalPolicy().getValue()))
                .attributes(source.getAttributes() == null ? null : toAttributes(source.getAttributes()))
                .interfaceDocument(interfaceDocument)
                .docs(toDocumentRefs(source.getDocs()))
                .build();

        List<EServiceTemplateVersion> versions = new ArrayList<>();
        if (existing != null) {
            existing.getVersions().stream()
                    .filter(currentVersion -> !currentVersion.getId().equals(versionId(source)))
                    .forEach(versions::add);
        }
        versions.add(version);
        var templateBuilder = EServiceTemplate.builder()
                .id(template.getId())
                .creatorId(template.getCreator() == null ? null : template.getCreator().getId())
                .name(template.getName())
                .description(template.getDescription())
                .intendedTarget(template.getIntendedTarget())
                .technology(EServiceTechnology.valueOf(template.getTechnology().getValue()))
                .mode(EServiceMode.valueOf(template.getMode().getValue()))
                .personalData(template.getPersonalData())
                .versions(versions);

        if (template.getRiskAnalysis() != null) {
            template.getRiskAnalysis().stream()
                    .map(riskAnalysis -> EServiceRiskAnalysis.builder()
                            .id(riskAnalysis.getId())
                            .name(riskAnalysis.getName())
                            .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm() == null
                                    ? null
                                    : RiskAnalysisForm.builder()
                                            .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                                            .answers(riskAnalysis.getRiskAnalysisForm().getAnswers())
                                            .build())
                            .build())
                    .forEach(templateBuilder::riskAnalysis);
        } else if (existing != null) {
            existing.getRiskAnalyses().forEach(templateBuilder::riskAnalysis);
        }
        return templateBuilder.build();
    }

    private static UUID versionId(EServiceTemplateVersionDetails source) {
        return source.getId();
    }

    private static EServiceTemplateInterface toInterface(EServiceDoc source, UUID templateId, UUID versionId) {
        return source == null ? null : new EServiceTemplateInterface(
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
        return source == null ? null : DocumentRef.builder().id(source.getId()).build();
    }

    default List<DocumentRef> toDocumentRefs(List<EServiceDoc> source) {
        return source == null
                ? List.of()
                : source.stream().map(this::toDocumentRef).toList();
    }

    default Attributes toAttributes(
            it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes source
    ) {
        return org.mapstruct.factory.Mappers.getMapper(BffEServiceDescriptorMapper.class).toAttributes(source);
    }
}
