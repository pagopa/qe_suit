package it.pagopa.interop.common.infrastructure;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
public class SharedMapper {

    @Named("mapStringToInstant")
    public Instant mapStringToInstant(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return OffsetDateTime.parse(dateStr).toInstant();
    }
}