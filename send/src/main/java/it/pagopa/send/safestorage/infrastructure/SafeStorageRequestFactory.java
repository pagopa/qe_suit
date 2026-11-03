package it.pagopa.send.safestorage.infrastructure;

import it.pagopa.send.generated.openapi.clients.safestorage.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
    public class SafeStorageRequestFactory {

    /**
     * Chiavi presenti nella configurazione di indicizzazione di SafeStorage. Un tag non censito
     * viene rifiutato con 400 "Tag &lt;nome&gt; not found in the indexing configuration", il che
     * invaliderebbe la baseline di ogni caso generato.
     */
    private static final String INDEXED_TAG = "global_indexed_singlevalue";
    private static final String MULTIVALUE_TAG = "global_multivalue";
    private static final String TAG_VALUE = "test1";

    @Value("${safestorage.api.client-id}") private String clientId;

    public String clientId() { return clientId; }
    public String checksumValue() { return "MTIzNDU2Nzg5MDEyMzQ1Ng=="; } // match pattern

    public FileCreationRequest fileCreationRequest() {
        return uploadReservationRequest()
                .tags(Map.of(INDEXED_TAG, List.of(TAG_VALUE)));
    }

    /**
     * Richiesta minima usata dalla precondizione di upload: senza tag, cosi' che la creazione del
     * file su cui girano gli altri contract test non dipenda dalla configurazione di indicizzazione
     * ne' dal payload scelto come baseline del fuzzing.
     */
    public FileCreationRequest uploadReservationRequest() {
        return new FileCreationRequest()
                .contentType("application/pdf")
                .documentType("PN_NOTIFICATION_ATTACHMENTS")
                .status("PRELOADED");
    }

    public UpdateFileMetadataRequest updateFileMetadataRequest() {
        return new UpdateFileMetadataRequest()
                .status("ATTACHED")
                .retentionUntil(OffsetDateTime.now().plusDays(180))
                .availableUntil(OffsetDateTime.now().plusDays(30));
    }

    public AdditionalFileTagsUpdateRequest tagsUpdateRequest() {
        return new AdditionalFileTagsUpdateRequest()
                .SET(Map.of(INDEXED_TAG, List.of(TAG_VALUE)))
                .DELETE(Map.of(MULTIVALUE_TAG, List.of(TAG_VALUE)));
    }

    public AdditionalFileTagsMassiveUpdateRequest tagsMassiveUpdateRequest(String fileKey) {
        Tags tag = new Tags().fileKey(fileKey)
                .SET(Map.of(INDEXED_TAG, List.of(TAG_VALUE)))
                .DELETE(Map.of());
        return new AdditionalFileTagsMassiveUpdateRequest().tags(List.of(tag));
    }
}