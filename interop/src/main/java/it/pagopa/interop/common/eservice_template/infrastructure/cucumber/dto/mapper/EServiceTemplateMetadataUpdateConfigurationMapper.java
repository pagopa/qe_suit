package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateMetadataUpdateConfiguration;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EServiceTemplateMetadataUpdateConfigurationMapper {

    private static final String LAST_USED = "%last-used%";

    private final EntityStore entityStore;

    public Map<String, String> apply(
            List<EServiceTemplateMetadataUpdateConfiguration> configurations,
            UpdateEServiceTemplateCommand command
    ) {
        Map<String, String> values = new HashMap<>();
        Optional<EServiceTemplate> lastEServiceTemplate = entityStore.getLast(EServiceTemplate.class);

        for (EServiceTemplateMetadataUpdateConfiguration configuration : configurations) {
            String field = configuration.getField();
            String value = configuration.getValue();

            if (values.containsKey(field)) {
                throw new IllegalArgumentException("Campo metadato e-service template duplicato: " + field);
            }

            switch (field) {
                case "nome" -> {
                    value = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getName()
                            : RandomUtils.randomAlphanumericName(value, 10);
                    command.name(value);
                }
                case "descrizione" -> {
                    value = LAST_USED.equals(value) ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getDescription() : value;
                    command.description(value);
                }
                case "intendedTarget" -> {
                    value = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getIntendedTarget()
                            : value;
                    command.intendedTarget(value);
                }
                case "technology" -> {
                    EServiceTechnology technology = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getTechnology()
                            : EServiceTechnology.valueOf(value);
                    command.technology(technology);
                    value = technology == null ? null : technology.name();
                }
                case "mode" -> {
                    EServiceMode mode = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getMode()
                            : EServiceMode.valueOf(value);
                    command.mode(mode);
                    value = mode == null ? null : mode.name();
                }
                case "isSignalHubEnabled" -> {
                    Boolean enabled = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getIsSignalHubEnabled()
                            : parseBoolean(field, value);
                    command.isSignalHubEnabled(enabled);
                    value = enabled == null ? null : enabled.toString();
                }
                case "personalData" -> {
                    Boolean personalData = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getPersonalData()
                            : parseBoolean(field, value);
                    command.handlePersonalData(personalData);
                    value = personalData == null ? null : personalData.toString();
                }
                case "asyncExchange" -> {
                    Boolean asyncExchange = LAST_USED.equals(value)
                            ? lastEServiceTemplateOrThrow(lastEServiceTemplate).getAsyncExchange()
                            : parseBoolean(field, value);
                    command.isAsync(asyncExchange);
                    value = asyncExchange == null ? null : asyncExchange.toString();
                }
                default -> throw new IllegalArgumentException(
                        "Campo metadato e-service template non supportato: " + field
                );
            }

            values.put(field, value);
        }

        return values;
    }

    private static EServiceTemplate lastEServiceTemplateOrThrow(Optional<EServiceTemplate> lastEServiceTemplate) {
        return lastEServiceTemplate.orElseThrow(
                () -> new IllegalArgumentException("Nessun e-service template disponibile per il valore " + LAST_USED)
        );
    }

    private static Boolean parseBoolean(String field, String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Valore booleano non valido per " + field + ": " + value);
    }
}
