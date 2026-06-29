package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(config = StrictMapperConfig.class)
public interface ProducerKeychainMapper {

}