package it.pagopa.interop.common.contract.request;

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
public class ProducerKeychainReadAllRequest {
    private final Integer offset;
    private final Integer limit;
    private final String q;
    private final List<UUID> userIds;
    private final UUID eserviceId;

    public static ProducerKeychainReadAllRequest unfiltered() {
        return new ProducerKeychainReadAllRequest(0, 50, null, null, null);
    }
}
