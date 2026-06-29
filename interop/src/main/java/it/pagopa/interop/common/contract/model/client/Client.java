package it.pagopa.interop.common.contract.model.client;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.shared.Key;
import it.pagopa.interop.common.contract.model.shared.UserRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
    Set<Key> keys;
    ClientKind kind;
    Set<Purpose> purposes;
    Set<UserRef> users;
}