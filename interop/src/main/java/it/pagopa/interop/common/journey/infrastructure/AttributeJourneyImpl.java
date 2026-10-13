package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.journey.application.AttributeJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class AttributeJourneyImpl implements AttributeJourney<AttributeJourneyImpl> {

    private final AttributeUseCase attributeUseCase;
    private final EntityStore entityStore;

    @Override
    public AttributeJourneyImpl createCertifiedAttribute() {
        attributeUseCase.createCertifiedAttribute();
        return this;
    }

    @Override
    public Attributes certifiedAttributesWithDailyCallsPerConsumer(int dailyCallsPerConsumer) {
        Attribute attribute = entityStore.getLastOrThrow(Attribute.class)
                .toBuilder()
                .dailyCallsPerConsumer(dailyCallsPerConsumer)
                .build();
        return Attributes.builder().certified(List.of(attribute)).build();
    }
}
