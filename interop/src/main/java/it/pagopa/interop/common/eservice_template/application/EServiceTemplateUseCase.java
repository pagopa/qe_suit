package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceTemplateUseCase {
    private final EServiceTemplateGateway eServiceTemplateGateway;
    private final EServiceTemplateRequestFactory requestFactory;
    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public EServiceTemplate createEServiceTemplate(Consumer<EServiceTemplateCreationCommand> config) {
        EServiceTemplateCreationCommand creationCommand = requestFactory.defaultCreationEServiceTemplateCommand();
        config.accept(creationCommand);

        return createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand creationCommand) {
        return eServiceTemplateGateway.createEServiceTemplate(creationCommand);
    }

    public EServiceTemplate getEServiceTemplate(EServiceTemplate eServiceTemplate) {
        return eServiceTemplateGateway.getEServiceTemplate(eServiceTemplate.getRef());
    }

    public EServiceTemplate updateEServiceTemplate(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateCommand> config) {
        UpdateEServiceTemplateCommand updateCommand = requestFactory.defaultUpdateEServiceTemplateCommand();
        config.accept(updateCommand);

        return updateEServiceTemplate(eServiceTemplate, updateCommand);
    }

    public EServiceTemplate updateEServiceTemplate(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateCommand command) {
        return eServiceTemplateGateway.updateEServiceTemplate(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateName(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateNameCommand> config) {
        UpdateEServiceTemplateNameCommand command = requestFactory.defaultUpdateTemplateNameCommand();
        config.accept(command);

        return updateTemplateName(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateName(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateNameCommand command) {
        return eServiceTemplateGateway.updateTemplateName(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateIntendedTargetCommand> config) {
        UpdateEServiceTemplateIntendedTargetCommand command = requestFactory.defaultUpdateTemplateIntendedTargetCommand();
        config.accept(command);

        return updateTemplateIntendedTarget(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateIntendedTargetCommand command) {
        return eServiceTemplateGateway.updateTemplateIntendedTarget(eServiceTemplate.getRef(), command);
    }

    public EServiceTemplate updateTemplateDescription(EServiceTemplate eServiceTemplate, Consumer<UpdateEServiceTemplateDescriptionCommand> config) {
        UpdateEServiceTemplateDescriptionCommand command = requestFactory.defaultUpdateTemplateDescriptionCommand();
        config.accept(command);

        return updateTemplateDescription(eServiceTemplate, command);
    }

    public EServiceTemplate updateTemplateDescription(EServiceTemplate eServiceTemplate, UpdateEServiceTemplateDescriptionCommand command) {
        return eServiceTemplateGateway.updateTemplateDescription(eServiceTemplate.getRef(), command);
    }

    public EServiceRiskAnalysis addLatestRiskAnalysis(Tenant tenant, EServiceTemplate eServiceTemplate, boolean completed) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(tenant, completed);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        CreateEServiceTemplateRiskAnalysisCommand command =
                requestFactory.createRiskAnalysisCommand(tenant.getTenantType(), form);
        return eServiceTemplateGateway.addRiskAnalysis(eServiceTemplate.getRef(), command);
    }

    public void attemptTemplateNameUpdate(EServiceTemplate template, String name) {
        var command = requestFactory.defaultUpdateTemplateNameCommand().name(name);
        eServiceTemplateGateway.attemptTemplateNameUpdate(template.getRef(), command);
    }

    public void attemptTemplateDescriptionUpdate(EServiceTemplate template, String description) {
        var command = requestFactory.defaultUpdateTemplateDescriptionCommand().description(description);
        eServiceTemplateGateway.attemptTemplateDescriptionUpdate(template.getRef(), command);
    }

    public void attemptTemplateIntendedTargetUpdate(EServiceTemplate template, String intendedTarget) {
        var command = requestFactory.defaultUpdateTemplateIntendedTargetCommand().intendedTarget(intendedTarget);
        eServiceTemplateGateway.attemptTemplateIntendedTargetUpdate(template.getRef(), command);
    }

    public void attemptTemplateCreation(String name) {
        var command = requestFactory.defaultCreationEServiceTemplateCommand().name(name);
        eServiceTemplateGateway.attemptTemplateCreation(command);
    }

    public void attemptAddRiskAnalysisWithName(
            Tenant tenant,
            EServiceTemplate eServiceTemplate,
            String name
    ) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(tenant, true);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        CreateEServiceTemplateRiskAnalysisCommand command =
                requestFactory.createRiskAnalysisCommand(tenant.getTenantType(), form)
                        .name(name);
        eServiceTemplateGateway.attemptAddRiskAnalysis(eServiceTemplate.getRef(), command);
    }
}
