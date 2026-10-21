package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceAttributeCertifiedDiscreteConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Getter
public class BffUpdateEServiceTemplateVersionCommand implements UpdateEServiceTemplateVersionCommand {
    private final UpdateEServiceTemplateVersionSeed bffPayload;
    private Attributes attributes = Attributes.builder().build();

    public static BffUpdateEServiceTemplateVersionCommand from(UpdateEServiceTemplateVersionSeed payload) {
        return new BffUpdateEServiceTemplateVersionCommand(payload);
    }

    private BffUpdateEServiceTemplateVersionCommand(UpdateEServiceTemplateVersionSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateVersionCommand() {
        this.bffPayload = new UpdateEServiceTemplateVersionSeed();
    }

    @Override
    public UpdateEServiceTemplateVersionCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand voucherLifespan(Integer voucherLifespan) {
        bffPayload.setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        bffPayload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand dailyCallsTotal(Integer dailyCallsTotal) {
        bffPayload.setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy) {
        bffPayload.setAgreementApprovalPolicy(
                it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.fromValue(
                        agreementApprovalPolicy.name()
                )
        );
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand attributes(Attributes attributes) {
        this.attributes = attributes;
        bffPayload.setAttributes(new EServiceTemplateAttributesSeed()
                .certified(toAttributeRequirements(attributes.getCertified()))
                .declared(toAttributeRequirements(attributes.getDeclared()))
                .verified(toAttributeRequirements(attributes.getVerified())));
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand attributes(UnaryOperator<Attributes> attributes) {
        return this.attributes(attributes.apply(this.attributes));
    }


    private List<List<EServiceTemplateVersionAttributeSeed>> toAttributeRequirements(List<Attribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return new ArrayList<>();
        }

        int maxGroup = attributes.stream()
                .mapToInt(Attribute::getGroup)
                .max()
                .orElse(-1);

        List<List<EServiceTemplateVersionAttributeSeed>> attributeRequirements = new ArrayList<>(maxGroup + 1);
        for (int i = 0; i <= maxGroup; i++) {
            attributeRequirements.add(new ArrayList<>());
        }

        for (Attribute attribute : attributes) {
            int group = attribute.getGroup();
            if (group >= 0 && group < attributeRequirements.size()) {
                attributeRequirements.get(group).add(toAttributeSeed(attribute));
            }
        }

        attributeRequirements.removeIf(List::isEmpty);
        return attributeRequirements;
    }

    private List<List<EServiceTemplateVersionAttributeSeed>> toAttributeRequirements_OLD(List<Attribute> attributes) {
        int groupCount = attributes.isEmpty() ? 0 : attributes.stream().mapToInt(Attribute::getGroup).max().getAsInt() + 1;
        List<List<EServiceTemplateVersionAttributeSeed>> attributeRequirements = new ArrayList<>(groupCount);
        for (int i = 0; i < groupCount; i++) {
            attributeRequirements.add(new ArrayList<>());
        }

        for (Attribute attribute : attributes) {
            attributeRequirements.get(attribute.getGroup()).add(toAttributeSeed(attribute));
        }
        return attributeRequirements;
    }

    private EServiceTemplateVersionAttributeSeed toAttributeSeed(Attribute commonAttribute) {
        EServiceTemplateVersionAttributeSeed attributeSeed = new EServiceTemplateVersionAttributeSeed();
        attributeSeed.setId(commonAttribute.getId());
        attributeSeed.setExplicitAttributeVerification(
                commonAttribute.getKind() == AttributeKind.VERIFIED
        );
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
