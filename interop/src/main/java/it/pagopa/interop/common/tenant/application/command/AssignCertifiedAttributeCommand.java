package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.attribute.domain.AttributeRef;

public interface AssignCertifiedAttributeCommand {
    AssignCertifiedAttributeCommand attribute(AttributeRef attributeRef);
}
