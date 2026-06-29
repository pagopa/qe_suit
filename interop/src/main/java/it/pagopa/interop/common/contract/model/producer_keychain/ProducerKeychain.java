package it.pagopa.interop.common.contract.model.producer_keychain;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.shared.Key;
import it.pagopa.interop.common.contract.model.shared.UserRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProducerKeychain implements TestModel {
    UUID id;
    String name;
    String description;
    List<Key> keys;
    Set<UserRef> users;
}