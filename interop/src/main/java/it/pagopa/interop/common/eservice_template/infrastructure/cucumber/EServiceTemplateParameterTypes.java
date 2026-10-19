package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateSOAPInterfaceFailureReason;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceTemplateParameterTypes {
    private final EntityStore entityStore;

    @ParameterType("template|template preparato")
    public EServiceTemplate eServiceTemplate(String reference) {
        return entityStore.getLastOrThrow(EServiceTemplate.class);
    }

    @ParameterType("numero di URL non corrispondente|URL non ammesso")
    public EServiceTemplateSOAPInterfaceFailureReason soapInterfaceFailureReason(String reason) {
        return switch (reason) {
            case "numero di URL non corrispondente" -> EServiceTemplateSOAPInterfaceFailureReason.URL_COUNT_MISMATCH;
            case "URL non ammesso" -> EServiceTemplateSOAPInterfaceFailureReason.URL_NOT_ALLOWED;
            default -> throw new IllegalArgumentException("Motivo di rifiuto SOAP non supportato: " + reason);
        };
    }

    @ParameterType("appena creata|versione appena creata|del template|versione del template|versione più recente del template|versione \\d+ del template|della versione \\d+ del template")
    public EServiceTemplateVersion templateVersion(String reference) {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        if (reference.chars().noneMatch(Character::isDigit)) return template.getLatestVersion();
        return template.getVersion(versionNumber(reference));
    }

    @ParameterType("descrittore|descriptor|descriptor appena creato|appena creato|appena creata|versione appena creata|dell'e-service|versione dell'e-service|versione più recente dell'e-service|versione \\d+ dell'e-service")
    public EServiceDescriptor eServiceDescriptor(String reference) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        if (reference.chars().noneMatch(Character::isDigit)) return eService.getLatestDescriptor();
        return eService.getDescriptor(versionNumber(reference));
    }

    private static int versionNumber(String reference) {
        return Integer.parseInt(reference.replaceAll("\\D+", ""));
    }
}
