package it.pagopa.interop.bff.model;

import it.pagopa.interop.common.domain.model.TestModel;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ProducerKeychain implements TestModel {

    public class Key {

    }

    UUID id;
    String name;
    String description;
    List<Key> keys;
}
