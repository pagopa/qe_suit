package it.pagopa.interop.new_arch.common.client.domain;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
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
public class Client implements TestModel {
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