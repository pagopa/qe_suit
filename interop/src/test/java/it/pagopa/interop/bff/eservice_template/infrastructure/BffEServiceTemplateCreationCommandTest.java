package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BffEServiceTemplateCreationCommandTest {
    @Test
    void fluent_mapping_is_lazy_and_does_not_validate_values() {
        BffEServiceTemplateCreationCommand command = new BffEServiceTemplateCreationCommand();
        EServiceTemplateSeed payload = command.getBffCreationPayload();

        assertThat(payload.getVersion()).isNull();
        assertThat(command.name("t").description("d").intendedTarget("i")
                .technology(EServiceTechnology.SOAP).mode(EServiceMode.RECEIVE)
                .handlePersonalData(true).isSignalHubEnabled(true).isAsync(true)
                .versionDescription("v").voucherLifespan(-1).dailyCallsPerConsumer(0)
                .dailyCallsTotal(-2).agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC))
                .isSameAs(command);
        assertThat(payload.getName()).isEqualTo("t");
        assertThat(payload.getDescription()).isEqualTo("d");
        assertThat(payload.getIntendedTarget()).isEqualTo("i");
        assertThat(payload.getTechnology().getValue()).isEqualTo("SOAP");
        assertThat(payload.getMode().getValue()).isEqualTo("RECEIVE");
        assertThat(payload.getPersonalData()).isTrue();
        assertThat(payload.getIsSignalHubEnabled()).isTrue();
        assertThat(payload.getAsyncExchange()).isTrue();
        assertThat(payload.getVersion().getDescription()).isEqualTo("v");
        assertThat(payload.getVersion().getVoucherLifespan()).isEqualTo(-1);
        assertThat(payload.getVersion().getDailyCallsPerConsumer()).isZero();
        assertThat(payload.getVersion().getDailyCallsTotal()).isEqualTo(-2);
        assertThat(payload.getVersion().getAgreementApprovalPolicy().getValue()).isEqualTo("AUTOMATIC");
    }

    @Test
    void from_preserves_existing_version_block() {
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
                .description("version already configured");
        EServiceTemplateSeed payload = new EServiceTemplateSeed().version(version);
        BffEServiceTemplateCreationCommand command = BffEServiceTemplateCreationCommand.from(payload);

        command.voucherLifespan(120);

        assertThat(command.getBffCreationPayload()).isSameAs(payload);
        assertThat(payload.getVersion()).isSameAs(version);
        assertThat(version.getDescription()).isEqualTo("version already configured");
        assertThat(version.getVoucherLifespan()).isEqualTo(120);
    }

    @Test
    void request_factory_returns_independent_fully_configured_bff_commands() {
        BffEServiceTemplateRequestFactory factory = new BffEServiceTemplateRequestFactory();
        BffEServiceTemplateCreationCommand first = factory.defaultCreationEServiceTemplateCommand();
        BffEServiceTemplateCreationCommand second = factory.defaultCreationEServiceTemplateCommand();

        assertThat(first).isNotSameAs(second);
        assertThat(first.getBffCreationPayload()).isNotSameAs(second.getBffCreationPayload());
        assertThat(first.getBffCreationPayload().getName()).startsWith("template");
        assertThat(first.getBffCreationPayload().getDescription())
                .isEqualTo("Template e-service per test automatici");
        assertThat(first.getBffCreationPayload().getIntendedTarget())
                .isEqualTo("Enti aderenti alla piattaforma Interop");
        assertThat(first.getBffCreationPayload().getTechnology().getValue()).isEqualTo("REST");
        assertThat(first.getBffCreationPayload().getMode().getValue()).isEqualTo("DELIVER");
        assertThat(first.getBffCreationPayload().getPersonalData()).isFalse();
        assertThat(first.getBffCreationPayload().getIsSignalHubEnabled()).isFalse();
        assertThat(first.getBffCreationPayload().getAsyncExchange()).isFalse();
        assertThat(first.getBffCreationPayload().getVersion().getDescription())
                .isEqualTo("Prima versione del template di test");
        assertThat(first.getBffCreationPayload().getVersion().getVoucherLifespan()).isEqualTo(60);
        assertThat(first.getBffCreationPayload().getVersion().getDailyCallsPerConsumer()).isEqualTo(1);
        assertThat(first.getBffCreationPayload().getVersion().getDailyCallsTotal()).isEqualTo(10);
        assertThat(first.getBffCreationPayload().getVersion().getAgreementApprovalPolicy().getValue())
                .isEqualTo("AUTOMATIC");
        assertThat(factory.supports(Channel.BFF)).isTrue();
        assertThat(factory.supports(Channel.WEB_BROWSER)).isFalse();
        assertThat(factory.supports(Channel.M2M_V3)).isFalse();
    }
}
