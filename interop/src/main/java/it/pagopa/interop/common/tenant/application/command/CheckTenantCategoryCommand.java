package it.pagopa.interop.common.tenant.application.command;

import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.NonNull;
import lombok.Value;

@Value
public class CheckTenantCategoryCommand {
    @NonNull TenantRef tenant;
    @NonNull TenantCategory category;
    boolean expectedMembership;
}
