package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceDocumentUseCase {
    private final EServiceDescriptorGateway gateway;

    public EServiceDescriptor addDocument(EService service, EServiceDescriptor descriptor, String path, String name) {
        if (path == null || path.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Document fixture path and name must not be blank");
        }
        return gateway.addDocument(service.getRef(), descriptor.getRef(), path, name);
    }

    public void attemptValidDocumentUpload(EService service, EServiceDescriptor descriptor) {
        gateway.attemptValidDocumentUpload(service.getRef(), descriptor.getRef());
    }

    public void attemptDocumentUpload(EService service, EServiceDescriptor descriptor, String path, String name) {
        if (path == null || path.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Document fixture path and name must not be blank");
        }
        gateway.attemptDocumentUpload(service.getRef(), descriptor.getRef(), path, name);
    }

    public void attemptDocumentRename(EService service, EServiceDescriptor descriptor, String documentName, String newName) {
        var matches = descriptor.getDocs().stream()
                .filter(document -> documentName.equals(document.getPrettyName()))
                .toList();
        if (matches.size() != 1) {
            throw new IllegalStateException("Expected exactly one document named '" + documentName
                    + "' in descriptor " + descriptor.getId() + ", found " + matches.size());
        }
        gateway.attemptDocumentRename(service.getRef(), descriptor.getRef(), matches.get(0), newName);
    }
}
