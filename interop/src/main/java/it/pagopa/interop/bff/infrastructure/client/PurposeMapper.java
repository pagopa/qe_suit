package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.purpose.PurposeVersion;
import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface PurposeMapper {

}