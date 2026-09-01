package it.pagopa.interop.web.agreement.infrastructure;

import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebAgreementGateway implements AgreementGateway {

    private final EServiceDetailPage eServiceDetailPage;

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
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        throw new UnsupportedOperationException("WebAgreementGateway does not support activateAgreement operation");
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }

    private void assertAgreementButtonDisabled(AgreementCreationFailureReason reason) {
        if (!eServiceDetailPage.agreementButton().isDisabled()) {
            throw new AssertionError(
                    "Expected agreement button to be disabled for reason: " + reason
            );
        }
    }
}
