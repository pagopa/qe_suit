package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BffUpdateEServiceDescriptorCommand implements UpdateEServiceDescriptorCommand {
    private final UpdateEServiceDescriptorSeed bffPayload;

    public static BffUpdateEServiceDescriptorCommand from(UpdateEServiceDescriptorSeed payload) {
        return new BffUpdateEServiceDescriptorCommand(payload);
    }

    private BffUpdateEServiceDescriptorCommand(UpdateEServiceDescriptorSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceDescriptorCommand() {
        this.bffPayload = new UpdateEServiceDescriptorSeed();
    }

    @Override
    public UpdateEServiceDescriptorCommand voucherLifespan(Integer voucherLifespan) {
        bffPayload.setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        bffPayload.setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal) {
        bffPayload.setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand audience(List<String> audience) {
        bffPayload.setAudience(audience);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }

    @Override
    public UpdateEServiceDescriptorCommand attributes(Attributes attributes) {
        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        ArrayList<List<DescriptorAttributeSeed>> attributeRequirements;

        // Per tutti i tipi di attributi:
        // - su BFF c'è una lista di requisiti i cui elementi sono liste di attributi
        // - su Common c'è solo una lista di attributi che tramite group indicano il requisito di appartenenza

        // Lista di requisiti sugli attributi certificati
        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getCertified());
        attributesSeed.setCertified(attributeRequirements);

        // Lista di requisiti sugli attributi dichiarati
        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getDeclared());
        attributesSeed.setDeclared(attributeRequirements);

        // Lista di requisiti sugli attributi verificati
        attributeRequirements = copyCommonAttributesIntoAttributeRequirementsAccordingToItsGroup(attributes.getVerified());
        attributesSeed.setVerified(attributeRequirements);

        bffPayload.attributes(attributesSeed);
        return this;
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
        // Individua i gruppi esistenti sulla struttura Common e prepara le relative liste su BFF
        ArrayList<List<DescriptorAttributeSeed>> attributeRequirements = new ArrayList<>();
        int groupCount = 0;
        for (Attribute attribute : attributes) {
            int currentGroup = attribute.getGroup();
            if (currentGroup > groupCount) groupCount = currentGroup;
        }
        for (int i = 0; i < groupCount; i++) attributeRequirements.add(new ArrayList<>());
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

    @Override
    public UpdateEServiceDescriptorCommand agreementApprovalPolicy(AgreementApprovalPolicy policy) {
        bffPayload.agreementApprovalPolicy(
                it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.valueOf(policy.name())
        );
        return this;
    }
}
