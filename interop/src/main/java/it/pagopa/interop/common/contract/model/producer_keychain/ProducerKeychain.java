package it.pagopa.interop.common.contract.model.producer_keychain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.pagopa.interop.common.contract.model.TestModel;
import lombok.*;

import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProducerKeychain implements TestModel {

    private UUID id;
    private String name;
    private String description;
    private List<Key> keys;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key {
        private KeyPair keyPair;
    }
}