package it.pagopa.interop.common.kernel.domain;

import java.util.UUID;

public interface DocumentMetadata {
    UUID getId();

    String getName();

    String getContentType();

    String getPrettyName();

    String getChecksum();
}
