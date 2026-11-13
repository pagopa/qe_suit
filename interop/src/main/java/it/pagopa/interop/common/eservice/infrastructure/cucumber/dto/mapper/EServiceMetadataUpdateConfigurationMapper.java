package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceMetadataUpdateConfiguration;
import it.pagopa.utils.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EServiceMetadataUpdateConfigurationMapper {
    public Map<String, String> apply(
            List<EServiceMetadataUpdateConfiguration> configurations,
            EServiceUpdateCommand command
    ) {
        Map<String, String> values = new HashMap<>();

        for (EServiceMetadataUpdateConfiguration configuration : configurations) {
            String field = configuration.getField();
            String value = configuration.getValue();

            if (values.containsKey(field)) {
                throw new IllegalArgumentException("Campo metadato e-service duplicato: " + field);
            }

            switch (field) {
                case "nome" -> {
                    value = RandomUtils.randomAlphanumericName(value, 10);
                    command.name(value);
                }
                case "descrizione" -> command.description(value);
                case "technology" -> command.technology(EServiceTechnology.valueOf(value));
                case "mode" -> command.mode(EServiceMode.valueOf(value));
                case "signalHubEnabled" -> command.isSignalHubEnabled(parseBoolean(field, value));
                case "consumerDelegable" -> command.isConsumerDelegable(parseBoolean(field, value));
                case "clientAccessDelegable" -> command.isClientAccessDelegable(parseBoolean(field, value));
                case "handlePersonalData" -> command.handlePersonalData(parseBoolean(field, value));
                case "asyncExchange" -> command.asyncExchange(parseBoolean(field, value));
                default -> throw new IllegalArgumentException(
                        "Campo metadato e-service non supportato: " + field
                );
            }

            values.put(field, value);
        }

        return values;
    }

    private static Boolean parseBoolean(String field, String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Valore booleano non valido per " + field + ": " + value);
    }
}
