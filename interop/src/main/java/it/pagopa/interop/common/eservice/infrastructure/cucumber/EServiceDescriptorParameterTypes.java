package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceDescriptorParameterTypes {

    private final EntityStore entityStore;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;

    @ParameterType("sospesa|bozza|deprecata|pubblicata")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        return switch (state) {
            case "sospesa" -> EServiceDescriptorState.SUSPENDED;
            case "bozza" -> EServiceDescriptorState.DRAFT;
            case "deprecata" -> EServiceDescriptorState.DEPRECATED;
            case "pubblicata" -> EServiceDescriptorState.PUBLISHED;
            default -> throw new IllegalArgumentException("Invalid EServiceDescriptorState: " + state);
        };
    }

    @ParameterType("la versione ([0-9]+) dell'e-service \"([^\"]+)\"")
    public SelectedEServiceDescriptor eServiceDescriptorByVersionAndAlias(String version, String alias) {
        int versionNumber = Integer.parseInt(version);
        EService selectedEService = entityStore.getByAliasOrThrow(alias, EService.class);

        EServiceDescriptor selectedDescriptor =
                selectedEService.findDescriptor(versionNumber)
                        .orElseGet(() -> {
                            boolean isNextVersion = versionNumber == selectedEService.getDescriptors().size() + 1;
                            boolean hasDraftDescriptor = selectedEService.findDraftDescriptor().isPresent();

                            if(!isNextVersion)
                                throw new IllegalArgumentException("You are trying to create version " + versionNumber + " but the latest available version is " + selectedEService.getDescriptors().size());

                            if(hasDraftDescriptor)
                                throw new IllegalArgumentException("The latest available version is in draft status; it is not possible to create another one.");

                            return eServiceDescriptorUseCase.addDescriptor(selectedEService);
                        });

        return new SelectedEServiceDescriptor(selectedEService, selectedDescriptor);
    }
}
