package it.pagopa.send.radd.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File ZIP allegato a un'operazione RADD (appendice "FILE ZIP" dello spec): contiene solo
 * {@code metadati_destinatario.json}, gli estremi della carta d'identita' del destinatario secondo
 * {@code json-schema-identity-doc-info.json} (destinatario persona fisica, nessun delegato).
 */
@Component
public class RaddDocumentsZipFactory {

    static final String RECIPIENT_METADATA_ENTRY = "metadati_destinatario.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ZIP valido per l'operazione indicata, con il suo checksum.
     */
    public RaddDocumentsZip validZip(String operationId) {
        return zipOf(Map.of(RECIPIENT_METADATA_ENTRY, recipientMetadata(operationId)));
    }

    /**
     * ZIP formalmente valido ma senza {@code metadati_destinatario.json}.
     */
    public RaddDocumentsZip zipWithoutRecipientMetadata() {
        return zipOf(Map.of("leggimi.txt", "nessun metadato".getBytes(StandardCharsets.UTF_8)));
    }

    public String sha256Base64(byte[] content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(content);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponibile", e);
        }
    }

    private byte[] recipientMetadata(String operationId) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("operationId", operationId);
        metadata.put("docType", "CIE");
        metadata.put("docNumber", "GA99999LI");
        metadata.put("docIssuer", "Comune di Rho");
        metadata.put("issueDate", "2023-11-20");
        metadata.put("expireDate", "2033-11-20");
        try {
            return objectMapper.writeValueAsBytes(metadata);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Serializzazione dei metadati del destinatario fallita", e);
        }
    }

    private RaddDocumentsZip zipOf(Map<String, byte[]> entries) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Creazione dello ZIP RADD fallita", e);
        }
        byte[] content = bytes.toByteArray();
        return new RaddDocumentsZip(content, sha256Base64(content));
    }

    /**
     * Contenuto binario dello ZIP e checksum SHA-256 in base64 (da passare a {@code documentUpload} e alla PUT).
     */
    public record RaddDocumentsZip(byte[] content, String checksum) {
    }
}
