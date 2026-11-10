package it.pagopa.interop.common.infrastructure.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Backend metadata for one isolated document-accessibility fixture. */
public record DocumentDownloadFixture(
        UUID resourceId,
        UUID versionId,
        String interfaceName,
        List<String> additionalDocumentNames,
        String callbackName
) {
    public DocumentDownloadFixture {
        additionalDocumentNames = List.copyOf(additionalDocumentNames);
    }

    public List<String> documentationNames() {
        List<String> names = new ArrayList<>();
        names.add(interfaceName);
        names.addAll(additionalDocumentNames);
        return List.copyOf(names);
    }

    public List<String> technicalSpecificationNames() {
        return callbackName == null ? List.of(interfaceName) : List.of(interfaceName, callbackName);
    }
}
