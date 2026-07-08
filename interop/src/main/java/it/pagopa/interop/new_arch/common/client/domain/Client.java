package it.pagopa.interop.new_arch.common.client.domain;

import it.pagopa.interop.new_arch.common.infrastructure.security.crypto.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRef;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Client implements Identifiable {
    UUID id;
    UUID consumerId;
    String name;
    String description;
    List<Key> keys;
    ClientKind kind;
    Set<Purpose> purposes;
    Set<UserRef> users;

    public Key getLastKey() {
        if (keys == null || keys.isEmpty()) {
            return null;
        }

        return keys.get(keys.size() - 1);
    }
}