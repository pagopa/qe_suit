package it.pagopa.interop.common.producer_keychain.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BaseReadAllProducerKeychainRequest {
    private final Integer offset;
    private final Integer limit;
    private final String q;
    private final List<UUID> userIds;
    private final UUID eserviceId;

    public static BaseReadAllProducerKeychainRequest unfiltered() {
        return new BaseReadAllProducerKeychainRequest(0, 50, null, null, null);
    }
}
