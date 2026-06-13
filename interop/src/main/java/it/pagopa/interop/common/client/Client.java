package it.pagopa.interop.common.client;

import it.pagopa.interop.common.template.TestModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.security.KeyPair;
import java.util.Set;

@RequiredArgsConstructor
public class Client implements TestModel {

    @Delegate
    private final it.pagopa.interop.generated.openapi.clients.bff.model.Client embeddedModel;

    @Getter
    private final Set<KeyPair> keyPairs;

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