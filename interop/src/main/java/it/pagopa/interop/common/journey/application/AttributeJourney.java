package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;

import java.util.function.Consumer;

public interface AttributeJourney<SELF extends AttributeJourney<SELF>> extends JourneyModule {
    SELF createDeclaredAttribute(Consumer<AttributeCreationCommand> config);

    default SELF createDeclaredAttribute() {
        return createDeclaredAttribute(config -> {
        });
    }
}

