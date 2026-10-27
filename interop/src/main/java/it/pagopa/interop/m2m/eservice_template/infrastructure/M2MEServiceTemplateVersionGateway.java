package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionGateway;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
// import it.pagopa.domain.FileDownloadMultipart;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class M2MEServiceTemplateVersionGateway implements EServiceTemplateVersionGateway {

    private final M2MEServiceTemplateRestClient restClient;
    private final EntityStore entityStore;
    // private final M2MEServiceTemplateVersionMapper mapper;

    @Override
    public EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        return null;
        /*return restClient.getEServiceTemplateVersion(templateRef.id(), versionRef.versionId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(versionDetails -> {
                    Optional<EServiceTemplate> maybeTemplate = entityStore.getById(templateRef.id(), EServiceTemplate.class);
                    // return mapper.toTemplateWithUpsertVersion(versionDetails, maybeTemplate.orElse(null));
                    return null;
                })
                .updateContext()
                .map(template -> {
                    *//*EServiceTemplateVersion foundVersion = template.findVersion(versionRef.versionId());
                    if (foundVersion == null) {
                        throw new IllegalStateException("Template version not found after upsert");
                    }
                    return foundVersion;*//*
                    return null;
                })
                .get();*/
    }

    @Override
    public EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion uploadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateVersion updateDraftVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, UpdateEServiceTemplateVersionCommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EServiceTemplateInterface downloadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        /*var fileDownloadMultipart = download(templateRef.id(), versionRef.versionId());
        return new EServiceTemplateInterface(
                fileDownloadMultipart.getId(),
                templateRef,
                versionRef,
                fileDownloadMultipart.getFilename(),
                fileDownloadMultipart.getPrettyName(),
                fileDownloadMultipart.getFile().getBytes(StandardCharsets.UTF_8)
        );*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        var templateVersion = getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplateInterface metadata = Optional.ofNullable(templateVersion.getInterfaceDocument())
            .orElseThrow(() -> new IllegalStateException("La versione del template non ha un'interfaccia"));

        return Optional.of(downloadInterface(templateRef, versionRef));
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.M2M_V3;
    }

    /*private FileDownloadMultipart download(UUID templateId, UUID versionId) {
        return restClient.downloadEServiceTemplateVersionInterface(templateId, versionId)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .get();
    }*/
}
