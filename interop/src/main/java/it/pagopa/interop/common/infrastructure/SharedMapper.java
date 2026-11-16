package it.pagopa.interop.common.infrastructure;

import it.pagopa.utils.RandomUtils;
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

    @Named("randomizedString")
    public String randomizedString(String str) {
        return RandomUtils.randomAlphanumericName(str);
    }
}