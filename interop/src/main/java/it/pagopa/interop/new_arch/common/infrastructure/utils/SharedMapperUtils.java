package it.pagopa.interop.new_arch.common.infrastructure.utils;

import org.mapstruct.Named;
import java.time.Instant;
import java.time.OffsetDateTime;

public class SharedMapperUtils {

    @Named("mapStringToInstant")
    public Instant mapStringToInstant(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return OffsetDateTime.parse(dateStr).toInstant();
    }
}