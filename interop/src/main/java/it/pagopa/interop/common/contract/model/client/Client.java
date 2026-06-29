package it.pagopa.interop.common.contract.model.client;

import it.pagopa.interop.common.contract.enums.InteropClientType;
import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.tenant.Tenant;
import lombok.*;
import lombok.experimental.Delegate;

import java.security.KeyPair;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client implements TestModel {

    private UUID id;
    private Tenant consumer;
    private UUID adminId;
    private String adminName;
    private String name;
    private List<Purpose> purposes;
    private String description;
    private InteropClientType kind;
    private Set<KeyPair> keyPairs;

    public void addKeyPair(KeyPair keyPair) {
        if (keyPair != null) {
            keyPairs.add(keyPair);
        }
    }

    public void addAllKeyPairs(java.util.Set<KeyPair> pairs) {
        if (pairs != null) {
            keyPairs.addAll(pairs);
        }
    }

    public KeyPair getLastKeyPair() {
        if (keyPairs.isEmpty()) return null;
        KeyPair last = null;
        for (KeyPair kp : keyPairs) {
            last = kp;
        }
        return last;
    }
}