package it.pagopa.interop.new_arch.bff.infrastructure.mapping;

import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationWithCompactTenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.interop.new_arch.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.new_arch.common.kernel.domain.Delegation;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationTenant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

@Mapper(config = TestMapperConfig.class)
public interface BffCommonMapper {

    Delegation toDelegationRef(DelegationWithCompactTenants source);

    DelegationTenant toDelegationTenantRef(CompactOrganization source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    it.pagopa.interop.new_arch.common.kernel.domain.TenantKind toDomainTenantKind(TenantKind source);

    it.pagopa.interop.new_arch.common.kernel.domain.EServiceTemplateRef toTemplateRef(EServiceTemplateRef source);
}
