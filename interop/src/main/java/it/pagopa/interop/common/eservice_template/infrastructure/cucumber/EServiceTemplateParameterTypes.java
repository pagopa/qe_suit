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
        if (reference.contains("appena creata")) {
            if (template.getVersions().size() != 1) {
                throw new IllegalStateException(
                        "Impossibile risolvere la versione appena creata: trovate "
                                + template.getVersions().size() + " versioni per template " + template.getId()
                );
            }
            return template.getVersions().iterator().next();
        }
        if (!reference.matches(".*\\d+.*")) {
            return latest(template.getVersions(), EServiceTemplateVersion::getVersion, "template");
        }
        return byVersion(template.getVersions(), EServiceTemplateVersion::getVersion,
                versionNumber(reference), "template");
    }

    @ParameterType("appena creato|appena creata|versione appena creata|dell'e-service|versione dell'e-service|versione più recente dell'e-service|versione \\d+ dell'e-service")
    public EServiceDescriptor eServiceDescriptor(String reference) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        if (reference.contains("appena creat")) {
            if (eService.getDescriptors().size() != 1) {
                throw new IllegalStateException(
                        "Impossibile risolvere il descriptor appena creato: trovati "
                                + eService.getDescriptors().size() + " descriptor per EService " + eService.getId()
                );
            }
            return eService.getDescriptors().iterator().next();
        }
        if (!reference.matches(".*\\d+.*")) {
            return latest(eService.getDescriptors(), EServiceDescriptor::getVersion, "e-service");
        }
        return byVersion(eService.getDescriptors(), EServiceDescriptor::getVersion,
                versionNumber(reference), "e-service");
    }

    private static int versionNumber(String reference) {
        return Integer.parseInt(reference.replaceAll("\\D+", ""));
    }

    private static <T> T latest(List<T> entities, Function<T, String> version, String resource) {
        Map<Integer, T> versions = uniqueVersions(entities, version, resource);
        return versions.entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new NoSuchElementException("Nessuna versione trovata per " + resource));
    }

    private static <T> T byVersion(
            List<T> entities,
            Function<T, String> version,
            int requestedVersion,
            String resource
    ) {
        T result = uniqueVersions(entities, version, resource).get(requestedVersion);
        if (result == null) {
            throw new NoSuchElementException("Versione " + requestedVersion + " non trovata per " + resource);
        }
        return result;
    }

    private static <T> Map<Integer, T> uniqueVersions(
            List<T> entities,
            Function<T, String> version,
            String resource
    ) {
        Map<Integer, T> versions = new HashMap<>();
        for (T entity : entities) {
            int number = Integer.parseInt(version.apply(entity));
            if (versions.putIfAbsent(number, entity) != null) {
                throw new IllegalStateException(
                        "Versione numerica duplicata " + number + " per " + resource
                );
            }
        }
        return versions;
    }
}
