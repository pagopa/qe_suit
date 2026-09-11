package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.common.attribute.application.AttributeRequestFactory;
import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.bff.attribute.application.BffAttributeCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.utils.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class BffAttributeRequestFactory implements AttributeRequestFactory {

    @Override
    public AttributeCreationCommand defaultDeclaredAttributeCreationCommand() {
        return new BffAttributeCreationCommand()
                .name(RandomUtils.randomAlphanumericName("declared-attribute"))
                .description(RandomUtils.randomAlphanumericName("declared-attribute-description"));
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

