package it.pagopa.interop.m2m.agreement.infrastructure;

import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = TestMapperConfig.class, uses = {SharedMapper.class})
public interface M2MAgreementMapper {

    Agreement toAgreement(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Agreement source);
}
