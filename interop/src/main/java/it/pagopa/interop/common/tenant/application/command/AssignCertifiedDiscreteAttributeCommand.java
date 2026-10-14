package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.kernel.domain.AttributeRef;

public interface AssignCertifiedDiscreteAttributeCommand {
    AssignCertifiedDiscreteAttributeCommand attribute(AttributeRef attributeRef);
    AssignCertifiedDiscreteAttributeCommand value(int value);
}
