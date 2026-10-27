package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.RequiredAttributeUseCase;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.RequiredAttributeJourney;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequiredAttributeJourneyImpl implements RequiredAttributeJourney<RequiredAttributeJourneyImpl> {
    private final RequiredAttributeUseCase requiredAttributeUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;

    @Override
    public RequiredAttributeJourneyImpl configureRequiredAttributes(
            EService eService, int version, TenantRef producer, List<RequiredAttributeCommand> requests
    ) {
        if (!producer.id().toString().equals(eService.getProducerId())) {
            throw new IllegalArgumentException("The stated producer does not own the e-service");
        }
        var descriptor = eService.getDescriptorOrThrow(version);
        var attributes = requiredAttributeUseCase.resolve(requests);
        descriptorUseCase.updateRequiredAttributes(eService, descriptor, attributes);
        return this;
    }

}
