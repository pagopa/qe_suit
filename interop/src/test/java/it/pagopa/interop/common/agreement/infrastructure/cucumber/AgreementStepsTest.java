package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AgreementStepsTest {
    private final AgreementUseCase agreementUseCase = mock(AgreementUseCase.class);
    private final AuthSession authSession = mock(AuthSession.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final InteropJourney interopJourney = mock(InteropJourney.class);
    private final TenantUseCase tenantUseCase = mock(TenantUseCase.class);
    private final EServiceDescriptorUseCase descriptorUseCase = mock(EServiceDescriptorUseCase.class);
    private final EServiceRequestFactory requestFactory = mock(EServiceRequestFactory.class);
    private final AgreementSteps steps = new AgreementSteps(
            agreementUseCase,
            authSession,
            entityStore,
            interopJourney,
            tenantUseCase,
            descriptorUseCase,
            requestFactory
    );

    @Test
    void waitsForContractAvailabilityAndVerifiesSignedContractAbsence() {
        var agreement = Agreement.builder().id(UUID.randomUUID()).build();

        steps.agreementContractAvailable(agreement);
        steps.agreementSignedContractNotYetAvailable(agreement);

        verify(agreementUseCase).awaitContractAvailable(agreement);
        verify(agreementUseCase).verifySignedContractNotYetAvailable(agreement);
    }

    @Test
    void attachesAndRemovesConsumerDocuments() {
        var document = Document.builder().id(UUID.randomUUID()).name("allegato-a.pdf").build();
        var agreement = Agreement.builder().id(UUID.randomUUID()).consumerDocument(document).build();

        steps.attachConsumerDocumentToAgreement(agreement);
        steps.attachNamedConsumerDocumentToAgreement("allegato-a.pdf", agreement);
        steps.removeAttachedDocumentFromAgreement(agreement);

        verify(agreementUseCase).addConsumerDocument(agreement, null);
        verify(agreementUseCase).addConsumerDocument(agreement, "allegato-a.pdf");
        verify(agreementUseCase).removeLastConsumerDocument(agreement);
    }

    @Test
    void submitsAssignsVerifiedAttributeAndActivatesAgreement() {
        var agreement = Agreement.builder().id(UUID.randomUUID()).build();
        var pending = agreement.toBuilder().state(AgreementState.PENDING).build();
        var active = agreement.toBuilder().state(AgreementState.ACTIVE).build();
        var attribute = Attribute.builder().id(UUID.randomUUID()).build();
        when(agreementUseCase.submitAgreement(agreement)).thenReturn(pending);
        when(agreementUseCase.activateAgreement(pending)).thenReturn(active);

        steps.submitAgreement(agreement);
        steps.assignVerifiedAttributeForAgreement(attribute, Tenant.PAGO_PA, pending);
        steps.activateAgreement(pending);

        verify(agreementUseCase).submitAgreement(agreement);
        verify(agreementUseCase).awaitState(pending, Set.of(AgreementState.PENDING));
        verify(tenantUseCase).assignVerifiedAttribute(
                TenantRef.of(Tenant.PAGO_PA.getOrganizationId()),
                attribute.getRef(),
                pending.getRef()
        );
        verify(agreementUseCase).activateAgreement(pending);
        verify(agreementUseCase).awaitState(active, Set.of(AgreementState.ACTIVE));
    }

    @Test
    void configuresSelectedDescriptorForManualApprovalAsProducer() {
        var descriptor = EServiceDescriptor.builder()
                .id(UUID.randomUUID())
                .version("1")
                .state(EServiceDescriptorState.DRAFT)
                .build();
        var updatedDescriptor = descriptor.toBuilder()
                .agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL)
                .build();
        var eService = EService.builder()
                .id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString())
                .descriptors(new ArrayList<>(List.of(descriptor)))
                .build();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        when(requestFactory.defaultUpdateDescriptorCommand()).thenReturn(command);
        when(command.agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL)).thenReturn(command);
        when(descriptorUseCase.updateDescriptor(eService, descriptor, command)).thenReturn(updatedDescriptor);

        steps.configureDescriptorForManualAgreementApproval(new SelectedEServiceDescriptor(eService, 1));

        verify(authSession).set(Tenant.COMUNE_DI_MILANO);
        verify(command).agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL);
        verify(descriptorUseCase).updateDescriptor(eService, descriptor, command);
    }

    @Test
    void createsRejectedAgreementWithAndWithoutAlias() {
        var eService = EService.builder().id(UUID.randomUUID()).build();
        when(interopJourney.withConsumer(Tenant.PAGO_PA)).thenReturn(interopJourney);
        when(entityStore.getByAlias("RF-1", Agreement.class)).thenReturn(Optional.empty());

        steps.createRejectedAgreementForEService(eService, Tenant.PAGO_PA);
        steps.createRejectedAgreementForEServiceWithAlias(eService, Tenant.PAGO_PA, "RF-1");

        verify(interopJourney).linkAgreement(eService, AgreementState.REJECTED);
        verify(interopJourney).linkAgreement(eService, AgreementState.REJECTED, null, "RF-1");
    }
}
