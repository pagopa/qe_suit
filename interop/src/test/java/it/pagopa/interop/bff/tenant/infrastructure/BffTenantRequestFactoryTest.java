package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.interop.bff.tenant.application.*;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantRequestFactory;
import it.pagopa.interop.common.tenant.application.command.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BffTenantRequestFactoryTest {

    private final TenantRequestFactory factory = new BffTenantRequestFactory();

    @Test
    @DisplayName("TenantRef e AttributeRef rifiutano UUID nulli")
    @SuppressWarnings("ConstantConditions")
    void refsShouldRejectNullIds() {
        assertThatThrownBy(() -> new TenantRef(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TenantRef.of(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new AttributeRef(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> AttributeRef.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Il comando certified incapsula il payload e consente di modificare l'attributo")
    void shouldBuildCertifiedAttributeSeed() {
        UUID attrId = UUID.randomUUID();
        AssignCertifiedAttributeCommand command = factory.assignCertifiedAttributeCommand(AttributeRef.of(attrId));

        assertThat(command).isInstanceOf(BffAssignCertifiedAttributeCommand.class);
        assertThat(((BffAssignCertifiedAttributeCommand) command).getBffPayload().getId()).isEqualTo(attrId);
        UUID otherId = UUID.randomUUID();
        assertThat(command.attribute(AttributeRef.of(otherId))).isSameAs(command);
        assertThat(((BffAssignCertifiedAttributeCommand) command).getBffPayload().getId()).isEqualTo(otherId);
    }

    @Test
    @DisplayName("Il comando discrete valorizza ID e certifiedDiscreteValue")
    void shouldBuildCertifiedDiscreteAttributeSeed() {
        UUID attrId = UUID.randomUUID();
        AssignCertifiedDiscreteAttributeCommand command = factory.assignCertifiedDiscreteAttributeCommand(AttributeRef.of(attrId), 42);

        assertThat(command).isInstanceOf(BffAssignCertifiedDiscreteAttributeCommand.class);
        var payload = ((BffAssignCertifiedDiscreteAttributeCommand) command).getBffPayload();
        assertThat(payload.getId()).isEqualTo(attrId);
        assertThat(payload.getCertifiedDiscreteValue()).isEqualTo(42);
        assertThat(command.value(43)).isSameAs(command);
        assertThat(payload.getCertifiedDiscreteValue()).isEqualTo(43);
    }

    @Test
    @DisplayName("Il comando declared valorizza l'ID e mantiene delegationId a null")
    void shouldBuildDeclaredAttributeSeed() {
        UUID attrId = UUID.randomUUID();
        AssignDeclaredAttributeCommand command = factory.assignDeclaredAttributeCommand(AttributeRef.of(attrId));

        assertThat(command).isInstanceOf(BffAssignDeclaredAttributeCommand.class);
        var payload = ((BffAssignDeclaredAttributeCommand) command).getBffPayload();
        assertThat(payload.getId()).isEqualTo(attrId);
        assertThat(payload.getDelegationId()).isNull();
    }

    @Test
    @DisplayName("Il comando verified valorizza ID e agreementId con expirationDate null")
    void shouldBuildVerifiedAttributeSeed() {
        UUID attrId = UUID.randomUUID();
        UUID agreementId = UUID.randomUUID();
        AssignVerifiedAttributeCommand command = factory.assignVerifiedAttributeCommand(AttributeRef.of(attrId), AgreementRef.of(agreementId));

        assertThat(command).isInstanceOf(BffAssignVerifiedAttributeCommand.class);
        var payload = ((BffAssignVerifiedAttributeCommand) command).getBffPayload();
        assertThat(payload.getId()).isEqualTo(attrId);
        assertThat(payload.getAgreementId()).isEqualTo(agreementId);
        assertThat(payload.getExpirationDate()).isNull();
        assertThat(command.expirationDate("2030-01-01T00:00:00Z")).isSameAs(command);
        assertThat(payload.getExpirationDate()).isEqualTo("2030-01-01T00:00:00Z");
    }

    @Test
    @DisplayName("Il comando di revoca verified valorizza l'agreementId")
    void shouldBuildRevokeVerifiedAttributeRequest() {
        UUID agreementId = UUID.randomUUID();
        RevokeVerifiedAttributeCommand command = factory.revokeVerifiedAttributeCommand(AgreementRef.of(agreementId));

        assertThat(command).isInstanceOf(BffRevokeVerifiedAttributeCommand.class);
        assertThat(((BffRevokeVerifiedAttributeCommand) command).getBffPayload().getAgreementId()).isEqualTo(agreementId);
    }

    @Test
    void shouldRejectMissingCommandInputs() {
        assertThatThrownBy(() -> factory.assignCertifiedAttributeCommand(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.assignCertifiedDiscreteAttributeCommand(null, 1)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.assignDeclaredAttributeCommand(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.assignVerifiedAttributeCommand(null, AgreementRef.of(UUID.randomUUID()))).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.assignVerifiedAttributeCommand(AttributeRef.of(UUID.randomUUID()), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.revokeVerifiedAttributeCommand(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> factory.revokeVerifiedAttributeCommand(AgreementRef.of(null))).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("La factory supporta il canale BFF e rifiuta altri canali")
    void shouldSupportBffChannel() {
        assertThat(factory.supports(Channel.BFF)).isTrue();
        assertThat(factory.supports(Channel.M2M_V3)).isFalse();
        assertThat(factory.supports(Channel.WEB_BROWSER)).isFalse();
    }
}



