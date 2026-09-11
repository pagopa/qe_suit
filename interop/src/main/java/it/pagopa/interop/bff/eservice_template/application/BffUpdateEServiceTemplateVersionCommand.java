package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BffUpdateEServiceTemplateVersionCommand implements UpdateEServiceTemplateVersionCommand {
    private final UpdateEServiceTemplateVersionSeed bffPayload;

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
    public UpdateEServiceTemplateVersionCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }

    @Override
    public UpdateEServiceTemplateVersionCommand declaredAttribute(Attribute attribute) {
        EServiceTemplateAttributesSeed attributes = bffPayload.getAttributes();

        if (attributes == null) {
            attributes = new EServiceTemplateAttributesSeed()
                    .certified(new ArrayList<>())
                    .declared(new ArrayList<>())
                    .verified(new ArrayList<>());
            bffPayload.setAttributes(attributes);
        }

        List<List<EServiceTemplateVersionAttributeSeed>> mutableDeclared = new ArrayList<>(attributes.getDeclared());
        mutableDeclared.add(List.of(
                new EServiceTemplateVersionAttributeSeed()
                        .id(attribute.getId())
                        .explicitAttributeVerification(false)
        ));
        attributes.setDeclared(mutableDeclared);

        return this;
    }
}

