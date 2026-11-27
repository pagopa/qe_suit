package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;
import java.util.UUID;

public interface EServiceTemplateVersionGateway extends Plugin<Channel> {
    void deleteDraftVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    void suspendVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion getEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion addVersion(EServiceTemplateRef templateRef);

    EServiceTemplateVersion publishEServiceTemplateVersion(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    EServiceTemplateVersion uploadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath);

    EServiceTemplateVersion addDocument(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String fixturePath,
            String documentName
    );

    void requestDocumentUpload(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef, String fixturePath);

    void attemptFileUpload(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String fixturePath,
            DocumentKind kind,
            String documentName
    );

    void attemptDocumentRename(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UUID documentId,
            String newName
    );

    void attemptDocumentDownload(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UUID documentId
    );

    void shouldFailToUploadInterface(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            String fixturePath,
            SoapInterfaceUploadFailureReason reason
    );

    EServiceTemplateVersion updateDraftVersion(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    );

    EServiceTemplateInterface downloadInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    Optional<EServiceTemplateInterface> readCurrentInterface(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef);

    void attemptDraftVersionUpdate(
            EServiceTemplateRef templateRef,
            EServiceTemplateVersionRef versionRef,
            UpdateEServiceTemplateVersionCommand command
    );
}
