package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.FileDownloadMultipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileDownloadMultipartUtils {

    private static final Pattern MULTIPART_FIELD_NAME = Pattern.compile("(?:^|;)\\s*name=\"?([^\";]+)\"?");

    private FileDownloadMultipartUtils() {
    }

    public static FileDownloadMultipart from(byte[] data) {

        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Downloaded multipart file is empty");
        }

        Charset bytePreservingCharset = StandardCharsets.ISO_8859_1;
        String body = new String(data, bytePreservingCharset);
        int firstLineEnd = body.indexOf("\r\n");
        if (firstLineEnd < 3 || !body.startsWith("--")) {
            throw new IllegalArgumentException("Downloaded content is not a valid multipart body");
        }

        String boundary = body.substring(0, firstLineEnd);
        FileDownloadMultipart result = new FileDownloadMultipart();
        int partStart = firstLineEnd + 2;
        String nextBoundaryMarker = "\r\n" + boundary;

        while (true) {
            int headersEnd = body.indexOf("\r\n\r\n", partStart);
            if (headersEnd < 0) {
                throw new IllegalArgumentException("Downloaded multipart part has invalid headers");
            }

            Matcher fieldNameMatcher = MULTIPART_FIELD_NAME.matcher(body.substring(partStart, headersEnd));
            if (!fieldNameMatcher.find()) {
                throw new IllegalArgumentException("Downloaded multipart part has no field name");
            }

            int contentStart = headersEnd + 4;
            int contentEnd = body.indexOf(nextBoundaryMarker, contentStart);
            if (contentEnd < 0) {
                throw new IllegalArgumentException("Downloaded multipart part has no closing boundary");
            }
            String value = new String(
                    body.substring(contentStart, contentEnd).getBytes(bytePreservingCharset),
                    StandardCharsets.UTF_8
            );

            switch (fieldNameMatcher.group(1)) {
                case "file" -> result.setFile(value);
                case "filename" -> result.setFilename(value);
                case "contentType" -> result.setContentType(value);
                case "id" -> result.setId(UUID.fromString(value.trim()));
                case "prettyName" -> result.setPrettyName(value);
                default -> {
                }
            }

            int boundaryStart = contentEnd + 2;
            if (body.startsWith(boundary + "--", boundaryStart)) {
                break;
            }
            if (!body.startsWith(boundary + "\r\n", boundaryStart)) {
                throw new IllegalArgumentException("Downloaded multipart body has an invalid boundary");
            }
            partStart = boundaryStart + boundary.length() + 2;
        }

        if (result.getFile() == null) {
            throw new IllegalArgumentException("Downloaded multipart body does not contain a file field");
        }
        return result;
    }
}
