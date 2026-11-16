package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceMetadataUpdateConfiguration;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EServiceMetadataUpdateConfigurationMapper {

    private static final String LAST_USED = "%last-used%";

    private final EntityStore entityStore;

    public Map<String, String> apply(
            List<EServiceMetadataUpdateConfiguration> configurations,
            EServiceUpdateCommand command
    ) {
        Map<String, String> values = new HashMap<>();
        Optional<EService> lastEService = entityStore.getLast(EService.class);

        for (EServiceMetadataUpdateConfiguration configuration : configurations) {
            String field = configuration.getField();
            String value = configuration.getValue();

            if (values.containsKey(field)) {
                throw new IllegalArgumentException("Campo metadato e-service duplicato: " + field);
            }

            switch (field) {
                case "nome" -> {
                    value = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getName()
                            : RandomUtils.randomAlphanumericName(value, 10);
                    command.name(value);
                }
                case "descrizione" -> {
                    value = LAST_USED.equals(value) ? lastEServiceOrThrow(lastEService).getDescription() : value;
                    command.description(value);
                }
                case "technology" -> {
                    EServiceTechnology technology = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getTechnology()
                            : EServiceTechnology.valueOf(value);
                    command.technology(technology);
                    value = technology == null ? null : technology.name();
                }
                case "mode" -> {
                    EServiceMode mode = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getMode()
                            : EServiceMode.valueOf(value);
                    command.mode(mode);
                    value = mode == null ? null : mode.name();
                }
                case "signalHubEnabled" -> {
                    Boolean enabled = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getIsSignalHubEnabled()
                            : parseBoolean(field, value);
                    command.isSignalHubEnabled(enabled);
                    value = enabled == null ? null : enabled.toString();
                }
                case "consumerDelegable" -> {
                    Boolean delegable = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getIsConsumerDelegable()
                            : parseBoolean(field, value);
                    command.isConsumerDelegable(delegable);
                    value = delegable == null ? null : delegable.toString();
                }
                case "clientAccessDelegable" -> {
                    Boolean delegable = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getIsClientAccessDelegable()
                            : parseBoolean(field, value);
                    command.isClientAccessDelegable(delegable);
                    value = delegable == null ? null : delegable.toString();
                }
                case "handlePersonalData" -> {
                    Boolean personalData = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getPersonalData()
                            : parseBoolean(field, value);
                    command.handlePersonalData(personalData);
                    value = personalData == null ? null : personalData.toString();
                }
                case "asyncExchange" -> {
                    Boolean asyncExchange = LAST_USED.equals(value)
                            ? lastEServiceOrThrow(lastEService).getAsyncExchange()
                            : parseBoolean(field, value);
                    command.asyncExchange(asyncExchange);
                    value = asyncExchange == null ? null : asyncExchange.toString();
                }
                default -> throw new IllegalArgumentException(
                        "Campo metadato e-service non supportato: " + field
                );
            }

            values.put(field, value);
        }

        return values;
    }

    private static EService lastEServiceOrThrow(Optional<EService> lastEService) {
        return lastEService.orElseThrow(
                () -> new IllegalArgumentException("Nessun e-service disponibile per il valore " + LAST_USED)
        );
    }

    private static Boolean parseBoolean(String field, String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Valore booleano non valido per " + field + ": " + value);
    }
}
