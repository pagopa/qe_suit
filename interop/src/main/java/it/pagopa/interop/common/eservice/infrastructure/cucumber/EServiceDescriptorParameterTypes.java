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

    @ParameterType("sospesa|bozza|deprecata|pubblicata|in archiviazione sospesa")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        return switch (state) {
            case "sospesa" -> EServiceDescriptorState.SUSPENDED;
            case "bozza" -> EServiceDescriptorState.DRAFT;
            case "deprecata" -> EServiceDescriptorState.DEPRECATED;
            case "pubblicata" -> EServiceDescriptorState.PUBLISHED;
            case "in archiviazione sospesa" -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
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
