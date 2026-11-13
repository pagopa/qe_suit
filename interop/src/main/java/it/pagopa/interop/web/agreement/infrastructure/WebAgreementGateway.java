package it.pagopa.interop.web.agreement.infrastructure;

import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.web.agreement.application.WebAgreementSecurityGateway;
import it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerAgreementDetailsPage;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceDetailPage;
import it.pagopa.interop.web.infrastructure.suit.component.NotFoundComponent;
import it.pagopa.utils.async.DelayUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.function.Supplier;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class WebAgreementGateway implements AgreementGateway, WebAgreementSecurityGateway {

    private final EServiceDetailPage eServiceDetailPage;
    private final ProducerAgreementDetailPage producerAgreementDetailPage;
    private final ConsumerAgreementDetailsPage consumerAgreementDetailsPage;
    private final NotFoundComponent notFoundComponent;

    @Override
    public void viewConsumerAgreementDetails(Agreement agreement) {
        consumerAgreementDetailsPage.navigateTo(agreement.getId().toString());
        consumerAgreementDetailsPage.assertLoaded();
    }

    @Override
    public void viewProducerAgreementDetails(Agreement agreement) {
        producerAgreementDetailPage.navigateTo(agreement.getId().toString());
        producerAgreementDetailPage.assertLoaded();
    }

    @Override
    public void verifyConsumerAgreementDownloadAvailability(Agreement agreement) {
        verifyAgreementDownloadAvailability(
                () -> consumerAgreementDetailsPage.generalInfoSection().downloadAgreementButton(),
                () -> viewConsumerAgreementDetails(agreement)
        );
    }

    @Override
    public void verifyProducerAgreementDownloadAvailability(Agreement agreement) {
        verifyAgreementDownloadAvailability(
                () -> producerAgreementDetailPage.generalInfoSection().downloadAgreementButton(),
                () -> viewProducerAgreementDetails(agreement)
        );
    }

    @Override
    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support ensureActiveAgreement operation");
    }

    @Override
    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support createAgreement operation");
    }

    @Override
    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation, AgreementCreationFailureReason reason) {
        eServiceDetailPage.navigateTo(eService.getId().toString(), descriptor.getId().toString());
        eServiceDetailPage.assertLoaded();

        switch (reason) {
            case ESERVICE_INVALID_STATE, DEPRECATED_VERSION -> assertAgreementButtonDisabled(reason);
        }
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support getAgreement operation");
    }

    @Override
    public Agreement submitAgreement(Agreement agreement) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support submitAgreement operation");
    }

    @Override
    public void attemptAgreementSubmission(Agreement agreement) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support attemptAgreementSubmission operation");
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        producerAgreementDetailPage.navigateTo(agreement.getId().toString());
        producerAgreementDetailPage.assertLoaded();
        producerAgreementDetailPage.activeButton().click();
        producerAgreementDetailPage.confirmButton().click();
        return agreement;
    }

    @Override
    public void attemptAgreementActivation(Agreement agreement) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support attemptAgreementActivation operation");
    }

    @Override
    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support suspendAgreement operation");
    }

    @Override
    public Agreement archiveAgreement(Agreement agreement) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support archiveAgreement operation");
    }

    @Override
    public Agreement awaitState(Agreement agreement, Set<AgreementState> expectedStates) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support awaitState operation");
    }

    @Override
    public void blockUnauthorizedAccessToErogazioneSection(Agreement agreement) {
        producerAgreementDetailPage.navigateTo(agreement.getId().toString());
        notFoundComponent.assertLoaded();
    }


    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }

    private void verifyAgreementDownloadAvailability(Supplier<Button> downloadButton, Runnable reloadPage) {
        assertThat(downloadButton.get().isDisabled())
                .as("Il link per scaricare la richiesta di fruizione inizialmente è disabilitato")
                .isTrue();

        // Si aspetta un tempo di sicurezza affinché la firma del documento di accordo avvenga
        DelayUtils.waitForSeconds(10);
        if (Thread.currentThread().isInterrupted()) {
            throw new IllegalStateException("Attesa della generazione del documento interrotta");
        }
        // Se non si ricarica la pagina il pulsante resta disabilitato anche se il documento è pronto
        reloadPage.run();

        assertThat(downloadButton.get().isDisabled())
                .as("Il link per scaricare la richiesta di fruizione deve essere abilitato dopo una congrua attesa")
                .isFalse();
    }

    private void assertAgreementButtonDisabled(AgreementCreationFailureReason reason) {
        if (!eServiceDetailPage.agreementButton().isDisabled()) {
            throw new AssertionError(
                    "Expected agreement button to be disabled for reason: " + reason
            );
        }
    }
}
