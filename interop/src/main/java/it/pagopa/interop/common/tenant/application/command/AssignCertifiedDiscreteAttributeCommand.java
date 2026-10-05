package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.attribute.domain.AttributeRef;

public interface AssignCertifiedDiscreteAttributeCommand {
    AssignCertifiedDiscreteAttributeCommand attribute(AttributeRef attributeRef);
    AssignCertifiedDiscreteAttributeCommand value(int value);
}
