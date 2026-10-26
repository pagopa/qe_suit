package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.attribute.domain.Attributes;

import java.util.List;
import java.util.function.UnaryOperator;

public interface EServiceTemplateInstanceUpdateDescriptorCommand {
    EServiceTemplateInstanceUpdateDescriptorCommand audience(List<String> audience);
    EServiceTemplateInstanceUpdateDescriptorCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer);
    EServiceTemplateInstanceUpdateDescriptorCommand dailyCallsTotal(Integer dailyCallsTotal);
    EServiceTemplateInstanceUpdateDescriptorCommand attributes(Attributes attributes);
    EServiceTemplateInstanceUpdateDescriptorCommand attributes(UnaryOperator<Attributes> attributes);
}
