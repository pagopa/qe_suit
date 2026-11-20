package it.pagopa.interop.common.infrastructure;

import it.pagopa.utils.RandomUtils;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfigurableText;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfigurableTextValueResolver;
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

    @Named("resolveConfigurableText")
    public String resolveConfigurableText(ConfigurableText text) {
        return ConfigurableTextValueResolver.resolve(text);
    }

    @Named("randomizedString")
    public String randomizedString(String str) {
        return RandomUtils.randomAlphanumericName(str);
    }
}