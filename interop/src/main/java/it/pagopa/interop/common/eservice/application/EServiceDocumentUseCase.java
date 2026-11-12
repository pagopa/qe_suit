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
}
