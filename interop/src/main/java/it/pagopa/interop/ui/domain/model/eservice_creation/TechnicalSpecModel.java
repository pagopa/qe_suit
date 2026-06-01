package it.pagopa.interop.ui.domain.model.eservice_creation;

import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Paths;

public record TechnicalSpecModel(
        String aud,
        String voucherLifespan,
        String interfaceAttachmentPath,
        AsyncExchangeProperties asyncExchangeProperties,
        String callbackInterfaceAttachmentPath
) {
    public static TechnicalSpecModel buildDefault() {
        try {
            String defaultPath = new ClassPathResource("assets/origin-interface.yaml").getFilePath().toAbsolutePath().toString();
            return new TechnicalSpecModel(
                    "quality-assurance",
                    "1",
                    defaultPath,
                    new AsyncExchangeProperties()
                            .responseTime(60)
                            .maxResultSet(1)
                            .resourceAvailableTime(60)
                            .bulk(false)
                            .confirmation(false),
                    defaultPath
            );
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load assets from file", e);
        }
    }

    public String getInterfaceFileName() {
        return interfaceAttachmentPath != null ? Paths.get(interfaceAttachmentPath).getFileName().toString() : null;
    }

    public String getCallbackInterfaceFileName() {
        return callbackInterfaceAttachmentPath != null ? Paths.get(callbackInterfaceAttachmentPath).getFileName().toString() : null;
    }
}
