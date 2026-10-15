package it.pagopa.interop.common.eservice;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EServiceDescriptorInterfaceTest {
    @Test
    void binary_is_defensive_and_metadata_refresh_requires_same_id_and_non_null_checksum() {
        UUID documentId = UUID.randomUUID();
        byte[] input = new byte[]{0, (byte) 0xff, 1};
        Document metadata = metadata(documentId, "sha256:one");
        EServiceDescriptorInterface original = new EServiceDescriptorInterface(
                metadata, EServiceRef.of(UUID.randomUUID()), EServiceDescriptorRef.of(UUID.randomUUID()), input
        );
        input[0] = 9;
        original.getContent()[1] = 9;
        assertThat(original.getContent()).containsExactly(0, (byte) 0xff, 1);

        var matching = original.refresh(metadata(documentId, "sha256:one"),
                original.getEServiceRef(), original.getDescriptorRef());
        var missingChecksum = original.refresh(metadata(documentId, null),
                original.getEServiceRef(), original.getDescriptorRef());
        var replacement = original.refresh(metadata(UUID.randomUUID(), "sha256:one"),
                original.getEServiceRef(), original.getDescriptorRef());

        assertThat(matching.getContent()).containsExactly(0, (byte) 0xff, 1);
        assertThat(missingChecksum.getContent()).isNull();
        assertThat(replacement.getContent()).isNull();
    }

    @Test
    void metadata_only_and_empty_downloaded_content_are_distinct() {
        var refs = new EServiceRef(UUID.randomUUID());
        var descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());
        EServiceDescriptorInterface metadataOnly = new EServiceDescriptorInterface(
                metadata(UUID.randomUUID(), null), refs, descriptorRef, null
        );
        EServiceDescriptorInterface emptyDownload = metadataOnly.withContent(new byte[0]);

        assertThat(metadataOnly.hasDownloadedContent()).isFalse();
        assertThat(emptyDownload.hasDownloadedContent()).isTrue();
        assertThat(emptyDownload.getContent()).isEmpty();
    }

    private static Document metadata(UUID id, String checksum) {
        return Document.builder().id(id).name("interface.wsdl").prettyName("interface.wsdl")
                .contentType("application/xml").checksum(checksum).build();
    }
}
