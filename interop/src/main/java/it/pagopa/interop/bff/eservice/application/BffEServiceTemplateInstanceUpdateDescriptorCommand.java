package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class BffEServiceTemplateInstanceUpdateDescriptorCommand implements EServiceTemplateInstanceUpdateDescriptorCommand {

    private List<String> audience;
    private AgreementApprovalPolicy agreementApprovalPolicy;
    private Integer dailyCallsPerConsumer;
    private Integer dailyCallsTotal;
    private DescriptorAttributesSeed attributes;
    private Attributes commonAttributes = Attributes.builder().build();

    public static BffEServiceTemplateInstanceUpdateDescriptorCommand from(UpdateEServiceTemplateInstanceDescriptorQuotas payload) {
        return new BffEServiceTemplateInstanceUpdateDescriptorCommand(payload);
    }

    public static BffEServiceTemplateInstanceUpdateDescriptorCommand from(UpdateEServiceDescriptorTemplateInstanceSeed payload) {
        return new BffEServiceTemplateInstanceUpdateDescriptorCommand(payload);
    }

    private BffEServiceTemplateInstanceUpdateDescriptorCommand(UpdateEServiceTemplateInstanceDescriptorQuotas payload) {
        this.dailyCallsPerConsumer = payload.getDailyCallsPerConsumer();
        this.dailyCallsTotal = payload.getDailyCallsTotal();
        this.attributes = payload.getAttributes();
    }

    private BffEServiceTemplateInstanceUpdateDescriptorCommand(UpdateEServiceDescriptorTemplateInstanceSeed payload) {
        this.audience = payload.getAudience();
        this.agreementApprovalPolicy = AgreementApprovalPolicy.valueOf(payload.getAgreementApprovalPolicy().toString());
        this.dailyCallsPerConsumer = payload.getDailyCallsPerConsumer();
        this.dailyCallsTotal = payload.getDailyCallsTotal();
        this.attributes = payload.getAttributes();
    }

    public UpdateEServiceTemplateInstanceDescriptorQuotas getBffUpdatePublishedEServiceInstancePayload() {
        UpdateEServiceTemplateInstanceDescriptorQuotas payload = new UpdateEServiceTemplateInstanceDescriptorQuotas();
        payload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        payload.setDailyCallsTotal(dailyCallsTotal);
        payload.setAttributes(attributes);
        return payload;
    }

    public UpdateEServiceDescriptorTemplateInstanceSeed getBffUpdateDraftServiceInstancePayload() {
        UpdateEServiceDescriptorTemplateInstanceSeed payload = new UpdateEServiceDescriptorTemplateInstanceSeed();
        payload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        payload.setDailyCallsTotal(dailyCallsTotal);
        payload.setAudience(audience);
        payload.setAgreementApprovalPolicy(
                it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.fromValue(agreementApprovalPolicy.toString())
        );
        payload.setAttributes(attributes);
        return payload;
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand audience(List<String> audience) {
        this.audience = audience;
        return this;
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        this.dailyCallsPerConsumer = dailyCallsPerConsumer;
        return this;
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal) {
        this.dailyCallsTotal = dailyCallsTotal;
        return this;
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand attributes(Attributes attributes) {
        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        ArrayList<List<DescriptorAttributeSeed>> attributeRequirements;

        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getCertified());
        attributesSeed.setCertified(attributeRequirements);
        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getDeclared());
        attributesSeed.setDeclared(attributeRequirements);
        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getVerified());
        attributesSeed.setVerified(attributeRequirements);

        this.commonAttributes = attributes;
        this.attributes = attributesSeed;

        return this;
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand attributes(UnaryOperator<Attributes> attributes) {
        return this.attributes(attributes.apply(this.commonAttributes));
    }

    private ArrayList<List<DescriptorAttributeSeed>> copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(
            List<Attribute> attributes
    ) {
        ArrayList<List<DescriptorAttributeSeed>> attributeRequirements = createListsAsManyGroupsInCommonAttributes(attributes);
        for (Attribute commonAttribute : attributes) {
            attributeRequirements.get(commonAttribute.getGroup() - 1).add(
                    createAttributeSeedFromCommonAttribute(commonAttribute)
            );
        }
        return attributeRequirements;
    }

    private ArrayList<List<DescriptorAttributeSeed>> createListsAsManyGroupsInCommonAttributes(List<Attribute> attributes) {

        if (attributes == null || attributes.isEmpty()) {
            return new ArrayList<>();
        }

        int groupCount = 0;
        for (Attribute attribute : attributes) {
            Integer group = attribute.getGroup();
            if (group == null || group < 1) {
                throw new IllegalArgumentException("Required attribute groups must start at 1");
            }
            groupCount = Math.max(groupCount, group);
        }

        ArrayList<List<DescriptorAttributeSeed>> attributeRequirements = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            attributeRequirements.add(new ArrayList<>());
        }

        return attributeRequirements;
    }

    private DescriptorAttributeSeed createAttributeSeedFromCommonAttribute(Attribute commonAttribute) {
        DescriptorAttributeSeed attributeSeed = new DescriptorAttributeSeed();
        attributeSeed.setId(commonAttribute.getId());
        attributeSeed.setExplicitAttributeVerification(
                commonAttribute.getKind() == it.pagopa.interop.common.attribute.domain.AttributeKind.VERIFIED
        );
        attributeSeed.setDailyCallsPerConsumer(commonAttribute.getDailyCallsPerConsumer());
        if (commonAttribute.getDiscreteConfig() != null) {
            EServiceAttributeCertifiedDiscreteConfig discreteConfig = new EServiceAttributeCertifiedDiscreteConfig();
            discreteConfig.setThreshold(commonAttribute.getDiscreteConfig().getThreshold());
            discreteConfig.setComparator(
                    AttributeCertifiedDiscreteComparator.fromValue(
                            commonAttribute.getDiscreteConfig().getComparator().name()
                    )
            );
            attributeSeed.setDiscreteConfig(discreteConfig);
        }
        return attributeSeed;
    }
}
