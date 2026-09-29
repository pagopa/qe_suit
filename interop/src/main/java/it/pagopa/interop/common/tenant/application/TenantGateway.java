package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.kernel.domain.*;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface TenantGateway extends Plugin<Channel> {
    void assignCertifiedAttribute(UUID attributeId, Tenant tenant);

}
