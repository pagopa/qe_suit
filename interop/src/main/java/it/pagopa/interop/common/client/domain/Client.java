package it.pagopa.interop.common.client.domain;

import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.Identifiable;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Client implements Identifiable {
    UUID id;
    UUID consumerId;
    String name;
    String description;

    @Builder.Default
    LinkedHashSet<Key> keys = new LinkedHashSet<>();

    ClientKind kind;
    User admin;

    @Builder.Default
    List<Purpose> purposes = new LinkedList<>();

    @Builder.Default
    List<UserRef> users  = new LinkedList<>();

    public Key getLastKey() {
        if (keys.isEmpty()) {
            return null;
        }

        return keys.stream()
                .reduce((first, second) -> second)
                .orElse(null);
    }
}