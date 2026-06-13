package it.pagopa.interop.common.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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