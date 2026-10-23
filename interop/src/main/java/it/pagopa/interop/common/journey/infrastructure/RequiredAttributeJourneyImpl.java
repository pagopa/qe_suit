package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.RequiredAttributeUseCase;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.RequiredAttributeJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequiredAttributeJourneyImpl implements RequiredAttributeJourney<RequiredAttributeJourneyImpl> {
    private final RequiredAttributeUseCase requiredAttributeUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final CurrentUserSession session;

    @Override
    public RequiredAttributeJourneyImpl configureRequiredAttributes(
            EService eService, int version, TenantRef producer, List<RequiredAttributeCommand> requests
    ) {
        if (!producer.id().toString().equals(eService.getProducerId())) {
            throw new IllegalArgumentException("The stated producer does not own the e-service");
        }
        var descriptor = eService.getDescriptorOrThrow(version);
        User originalUser = session.getUser();
        Tenant originalTenant = session.getTenant();
        var attributes = resolveAsPagoPa(producer, requests, originalUser, originalTenant);
        descriptorUseCase.updateRequiredAttributes(eService, descriptor, attributes);
        return this;
    }

    private it.pagopa.interop.common.attribute.domain.Attributes resolveAsPagoPa(
            TenantRef producer, List<RequiredAttributeCommand> requests, User originalUser, Tenant originalTenant
    ) {
        try {
            session.set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
            return requiredAttributeUseCase.resolve(producer, requests);
        } finally {
            session.set(originalUser, originalTenant);
        }
    }
}
