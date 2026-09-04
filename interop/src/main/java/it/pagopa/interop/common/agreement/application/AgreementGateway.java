package it.pagopa.interop.common.agreement.application;

import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

public interface AgreementGateway extends Plugin<Channel> {

    Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation);

    default Agreement createAgreement(EService eService, EServiceDescriptor descriptor) {
        return createAgreement(eService, descriptor, null);
    }

    void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation, AgreementCreationFailureReason reason);

    default void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, AgreementCreationFailureReason reason) {
        shouldFailToCreateAgreement(eService, descriptor, null, reason);
    }

    Agreement getAgreement(AgreementRef ref);

    Agreement submitAgreement(Agreement agreement);

    Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation);

    void shouldSeeBannerAdvisingTheUpdateOfTheAgreement(EService eService);
}
