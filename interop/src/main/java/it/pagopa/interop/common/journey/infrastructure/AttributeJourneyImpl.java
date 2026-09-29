package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.journey.application.AttributeJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AttributeJourneyImpl implements AttributeJourney<AttributeJourneyImpl> {

    private final AttributeUseCase attributeUseCase;

    @Override
    public AttributeJourneyImpl createCertifiedAttribute() {
        attributeUseCase.createCertifiedAttribute();
        return this;
    }
}
