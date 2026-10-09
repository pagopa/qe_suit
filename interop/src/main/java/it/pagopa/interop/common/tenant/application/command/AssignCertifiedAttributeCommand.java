package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.kernel.domain.AttributeRef;

public interface AssignCertifiedAttributeCommand {
    AssignCertifiedAttributeCommand attribute(AttributeRef attributeRef);
}
