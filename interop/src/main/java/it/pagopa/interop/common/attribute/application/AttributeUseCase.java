package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AttributeUseCase {
    private final AttributeGateway attributeGateway;
    private final AttributeRequestFactory requestFactory;

    public Attribute createDeclaredAttribute(Consumer<AttributeCreationCommand> config) {
        AttributeCreationCommand command = requestFactory.defaultDeclaredAttributeCreationCommand();
        config.accept(command);

        return attributeGateway.createDeclaredAttribute(command);
    }

    public Attribute createDeclaredAttribute() {
        return createDeclaredAttribute(command -> {
        });
    }
}

