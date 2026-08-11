package it.pagopa.interop.bff.infrastructure.mapping;

import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.DelegationTenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import java.util.Arrays;

@Mapper(config = TestMapperConfig.class)
public interface BffCommonMapper {

    Delegation toDelegationRef(DelegationWithCompactTenants source);

    DelegationTenant toDelegationTenantRef(CompactOrganization source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    it.pagopa.interop.common.kernel.domain.TenantKind toDomainTenantKind(TenantKind source);

    it.pagopa.interop.common.kernel.domain.EServiceTemplateRef toTemplateRef(EServiceTemplateRef source);

    default User toUser(CompactUser source) {
        if (source == null) {
            return null;
        }
        return Arrays.stream(User.values())
                .filter(user -> user.getUserId().equals(source.getUserId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No User found with userId %s".formatted(source.getUserId())));
    }
}
