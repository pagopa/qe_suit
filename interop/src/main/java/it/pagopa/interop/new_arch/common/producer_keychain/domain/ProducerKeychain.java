package it.pagopa.interop.new_arch.common.producer_keychain.domain;

import it.pagopa.interop.new_arch.common.kernel.domain.Key;
import it.pagopa.interop.new_arch.common.kernel.Identifiable;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProducerKeychain implements Identifiable {
    UUID id;
    String name;
    String description;
    List<Key> keys;
    Set<UserRef> users;
}