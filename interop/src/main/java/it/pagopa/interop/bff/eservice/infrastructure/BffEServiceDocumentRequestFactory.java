package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Builds the multipart parameters for the descriptor document creation endpoint
 * ({@code POST /eservices/{eServiceId}/descriptors/{descriptorId}/documents}).
 * WSDL fixtures are loaded from {@code contract/wsdl/<name>} on the classpath.
 */
@Component
public class BffEServiceDocumentRequestFactory {

    private static final String WSDL_FIXTURE_FOLDER = "contract/wsdl/";

    public static final String KIND_INTERFACE = "INTERFACE";
    public static final String KIND_ASYNC_EXCHANGE_CALLBACK_INTERFACE = "ASYNC_EXCHANGE_CALLBACK_INTERFACE";

    public record DocumentRequest(String kind, String prettyName, File doc) {
    }

    public File wsdlFixture(String name) {
        return FileUtils.loadClasspathResourceAsTempFile(WSDL_FIXTURE_FOLDER + name);
    }

    public DocumentRequest interfaceDocumentRequest(String fixtureName) {
        return documentRequest(KIND_INTERFACE, fixtureName);
    }

    public DocumentRequest documentRequest(String kind, String fixtureName) {
        return new DocumentRequest(
                kind,
                RandomUtils.randomAlphanumericName("interface-"),
                wsdlFixture(fixtureName)
        );
    }
}

