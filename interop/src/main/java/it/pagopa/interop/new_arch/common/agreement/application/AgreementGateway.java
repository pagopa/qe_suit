package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;


import java.util.Optional;

public interface AgreementGateway extends Plugin<Channel> {

    AgreementRef createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation);

    default AgreementRef createAgreement(EService eService, EServiceDescriptor descriptor) {
        return createAgreement(eService, descriptor, null);
    }

    Agreement getAgreement(AgreementRef ref);

    Optional<AgreementRef> submitAgreement(Agreement agreement);

    Optional<AgreementRef> activateAgreement(Agreement agreement, @Nullable DelegationRef delegation);
}
