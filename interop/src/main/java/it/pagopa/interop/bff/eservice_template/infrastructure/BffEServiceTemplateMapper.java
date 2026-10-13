package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BffEServiceTemplateMapper {
    private final BffEServiceDescriptorMapper descriptorMapper;

    public EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source) {
        var template = source.getEserviceTemplate();
        var document = source.getInterface();

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
                .attributes(source.getAttributes() == null ? null : descriptorMapper.toAttributes(source.getAttributes()))
                .interfaceDocument(toDocumentRef(document))
                .docs(toDocumentRefs(source.getDocs()))
                .build();

        var templateBuilder = EServiceTemplate.builder()
                .id(template.getId())
                .creatorId(template.getCreator() == null ? null : template.getCreator().getId())
                .name(template.getName())
                .description(template.getDescription())
                .intendedTarget(template.getIntendedTarget())
                .technology(EServiceTechnology.valueOf(template.getTechnology().getValue()))
                .mode(EServiceMode.valueOf(template.getMode().getValue()))
                .personalData(template.getPersonalData())
                .version(version);

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
        }
        return templateBuilder.build();
    }

    public DocumentRef toDocumentRef(EServiceDoc source) {
        return source == null ? null : DocumentRef.builder().id(source.getId()).build();
    }

    public List<DocumentRef> toDocumentRefs(List<EServiceDoc> source) {
        return source == null
                ? List.of()
                : source.stream().map(this::toDocumentRef).toList();
    }
}
