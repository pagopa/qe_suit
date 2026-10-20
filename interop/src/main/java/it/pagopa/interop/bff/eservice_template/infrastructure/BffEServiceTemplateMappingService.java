package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BffEServiceTemplateMappingService {

    private final BffEServiceTemplateMapper mapper;

    public EServiceTemplate toEServiceTemplatePreservingVersions(
            EServiceTemplateDetails source,
            EServiceTemplate existingTemplate
    ) {
        if (source == null) {
            return existingTemplate;
        }

        EServiceTemplate mapped = mapper.toTemplateBase(source);
        List<EServiceTemplateVersion> mergedVersions =
                existingTemplate != null && existingTemplate.getVersions() != null
                        ? new ArrayList<>(existingTemplate.getVersions())
                        : new ArrayList<>();

        if (source.getVersions() != null) {
            source.getVersions().stream()
                    .map(mapper::toTemplateVersion)
                    .forEach(version -> upsertVersion(mergedVersions, version));
        }

        if (source.getDraftVersion() != null) {
            upsertVersion(mergedVersions, mapper.toTemplateVersion(source.getDraftVersion()));
        }

        List<EServiceRiskAnalysis> riskAnalyses =
                existingTemplate != null && existingTemplate.getRiskAnalyses() != null
                        ? new ArrayList<>(existingTemplate.getRiskAnalyses())
                        : new ArrayList<>();

        return mapped.toBuilder()
                .versions(mergedVersions)
                .riskAnalyses(riskAnalyses)
                .build();
    }

    public EServiceTemplate toEServiceTemplate(EServiceTemplateVersionDetails source) {
        return toEServiceTemplate(source, null);
    }

    public EServiceTemplate toEServiceTemplate(
            EServiceTemplateVersionDetails source,
            EServiceTemplate existing
    ) {
        if (source == null) {
            return existing;
        }

        EServiceTemplateVersion version = mapper.toDetailedTemplateVersion(source);
        List<EServiceTemplateVersion> versions =
                existing != null && existing.getVersions() != null
                        ? new ArrayList<>(existing.getVersions())
                        : new ArrayList<>();

        int existingVersionIndex = findVersionIndex(versions, version);
        if (existingVersionIndex >= 0) {
            version = preserveInterfaceContent(version, versions.get(existingVersionIndex));
            versions.set(existingVersionIndex, version);
        } else {
            versions.add(version);
        }

        return mapper.toTemplateFromVersionDetails(source, existing)
                .toBuilder()
                .versions(versions)
                .build();
    }

    private static int findVersionIndex(List<EServiceTemplateVersion> versions, EServiceTemplateVersion incoming) {
        for (int i = 0; i < versions.size(); i++) {
            if (versions.get(i).getId().equals(incoming.getId())) {
                return i;
            }
        }
        return -1;
    }

    private static EServiceTemplateVersion preserveInterfaceContent(
            EServiceTemplateVersion incoming,
            EServiceTemplateVersion previous
    ) {
        EServiceTemplateInterface incomingInterface = incoming.getInterfaceDocument();
        EServiceTemplateInterface previousInterface = previous.getInterfaceDocument();

        if (incomingInterface == null
                || previousInterface == null
                || !previousInterface.getId().equals(incomingInterface.getId())) {
            return incoming;
        }

        return incoming.toBuilder()
                .interfaceDocument(previousInterface.refreshMetadata(incomingInterface))
                .build();
    }

    private static void upsertVersion(List<EServiceTemplateVersion> versions, EServiceTemplateVersion incoming) {
        for (int i = 0; i < versions.size(); i++) {
            EServiceTemplateVersion current = versions.get(i);
            if (current.getId().equals(incoming.getId())) {
                versions.set(
                        i,
                        current.toBuilder()
                                .version(incoming.getVersion())
                                .state(incoming.getState())
                                .build()
                );
                return;
            }
        }
        versions.add(incoming);
    }
}
