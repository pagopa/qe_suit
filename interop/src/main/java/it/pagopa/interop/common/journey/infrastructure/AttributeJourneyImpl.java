package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.journey.application.AttributeJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AttributeJourneyImpl implements AttributeJourney<AttributeJourneyImpl> {

    private final AttributeUseCase attributeUseCase;
    private final EntityStore entityStore;

    @Override
    public AttributeJourneyImpl createDeclaredAttribute(Consumer<AttributeCreationCommand> config) {
        Attribute attribute = attributeUseCase.createDeclaredAttribute(config);
        entityStore.upsert(attribute);
        return this;
    }
}

