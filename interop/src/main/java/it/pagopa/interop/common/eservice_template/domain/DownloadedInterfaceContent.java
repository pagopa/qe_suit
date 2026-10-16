package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;

public final class DownloadedInterfaceContent {
    private DownloadedInterfaceContent() {}

    public static byte[] descriptorBytes(EServiceDescriptor descriptor) {
        if (descriptor.getInterfaceDocument() == null || !descriptor.getInterfaceDocument().hasDownloadedContent()) {
            throw new AssertionError("Il contenuto dell'interfaccia del descriptor non è stato scaricato");
        }
        return descriptor.getInterfaceDocument().getContent();
    }

    public static byte[] templateBytes(EServiceTemplateVersion version) {
        if (version.getInterfaceDocument() == null || !version.getInterfaceDocument().hasDownloadedContent()) {
            throw new AssertionError("Il contenuto baseline del template non è stato scaricato");
        }
        return version.getInterfaceDocument().getContent();
    }
}
