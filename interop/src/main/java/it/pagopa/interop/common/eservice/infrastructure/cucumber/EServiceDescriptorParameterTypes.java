package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceDescriptorParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("sospesa|bozza|in bozza|deprecata|pubblicata|in archiviazione|in archiviazione sospesa|archiviata|in attesa di approvazione")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        return switch (state) {
            case "sospesa" -> EServiceDescriptorState.SUSPENDED;
            case "bozza", "in bozza" -> EServiceDescriptorState.DRAFT;
            case "deprecata" -> EServiceDescriptorState.DEPRECATED;
            case "pubblicata" -> EServiceDescriptorState.PUBLISHED;
            case "in archiviazione" -> EServiceDescriptorState.ARCHIVING;
            case "in archiviazione sospesa" -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            case "archiviata" -> EServiceDescriptorState.ARCHIVED;
            case "in attesa di approvazione" -> EServiceDescriptorState.WAITING_FOR_APPROVAL;
            default -> throw new IllegalArgumentException("Invalid EServiceDescriptorState: " + state);
        };
    }

    @ParameterType("la versione ([0-9]+) dell'e-service \"([^\"]+)\"")
    public SelectedEServiceDescriptor eServiceDescriptorByVersionAndAlias(String version, String alias) {
        int versionNumber = Integer.parseInt(version);
        EService selectedEService = entityStore.getByAliasOrThrow(alias, EService.class);

        return new SelectedEServiceDescriptor(selectedEService, versionNumber);
    }
}
