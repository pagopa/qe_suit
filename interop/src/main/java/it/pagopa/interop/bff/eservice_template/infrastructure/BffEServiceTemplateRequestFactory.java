package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffEServiceTemplateRequestFactory {

    public EServiceTemplateSeed creationRequest() {
        return Instancio.of(EServiceTemplateSeed.class)
                .set(field(EServiceTemplateSeed::getName), randomAlphanumericName("eservice-template", 15))
                .set(field(EServiceTemplateSeed::getIntendedTarget), randomAlphanumericName("target", 30))
                .set(field(EServiceTemplateSeed::getDescription), randomAlphanumericName("description", 50))
                .set(field(EServiceTemplateSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceTemplateSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceTemplateSeed::getVersion), defaultVersionSeed())
                .set(field(EServiceTemplateSeed::getIsSignalHubEnabled), false)
                .set(field(EServiceTemplateSeed::getPersonalData), false)
                .set(field(EServiceTemplateSeed::getAsyncExchange), false)
                .create();
    }

    public EServiceTemplateNameUpdateSeed updateNameRequest() {
        return Instancio.of(EServiceTemplateNameUpdateSeed.class)
                .set(field(EServiceTemplateNameUpdateSeed::getName), randomAlphanumericName("eservice-template-name", 15))
                .create();
    }

    public EServiceTemplateIntendedTargetUpdateSeed updateIntendedTargetRequest() {
        return Instancio.of(EServiceTemplateIntendedTargetUpdateSeed.class)
                .set(field(EServiceTemplateIntendedTargetUpdateSeed::getIntendedTarget), randomAlphanumericName("target", 25))
                .create();
    }

    public EServiceTemplateDescriptionUpdateSeed updateDescriptionRequest() {
        return Instancio.of(EServiceTemplateDescriptionUpdateSeed.class)
                .set(field(EServiceTemplateDescriptionUpdateSeed::getDescription), randomAlphanumericName("description", 50))
                .create();
    }

    private VersionSeedForEServiceTemplateCreation defaultVersionSeed() {
        return Instancio.of(VersionSeedForEServiceTemplateCreation.class)
                .set(field(VersionSeedForEServiceTemplateCreation::getDescription), randomAlphanumericName("version-description", 35))
                .set(field(VersionSeedForEServiceTemplateCreation::getVoucherLifespan), 60)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsTotal), 10)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsPerConsumer), 1)
                .set(field(VersionSeedForEServiceTemplateCreation::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .create();
    }
}

