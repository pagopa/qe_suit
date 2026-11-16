package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;

@Service
@RequiredArgsConstructor
public class EServiceArchivingUseCase {
    private final EServiceUseCase services;
    private final EServiceDescriptorUseCase descriptors;

    public void archiveEntireEService(EService eService) {
        List<EServiceDescriptor> current = eService.getDescriptors().stream()
                .map(descriptor -> descriptors.getDescriptor(eService, descriptor))
                .toList();
        if (current.isEmpty()) {
            throw new IllegalStateException("Cannot archive an e-service without descriptors: " + eService.getId());
        }
        // Reject inactive versions rather than silently deleting them as a side effect of this setup.
        current.forEach(descriptor -> archivingState(descriptor.getState()));
        EServiceDescriptor latest = current.stream()
                .max(java.util.Comparator.comparingInt(descriptor -> Integer.parseInt(descriptor.getVersion())))
                .orElseThrow();
        if (latest.getState() != PUBLISHED && latest.getState() != SUSPENDED) {
            throw new IllegalStateException(
                    "Whole e-service archiving requires the latest version to be PUBLISHED or SUSPENDED, but was "
                            + latest.getState());
        }

        services.archiveEService(eService, GracePeriodDays.NUMBER_60);
        for (EServiceDescriptor descriptor : current) {
            EServiceDescriptorState expected = archivingState(descriptor.getState());
            EServiceDescriptor actual = descriptors.awaitState(eService, descriptor, expected);
            if (actual.getState() != expected) {
                throw new IllegalStateException("Expected descriptor " + descriptor.getId()
                        + " in state " + expected + " but was " + actual.getState());
            }
        }
    }

    private static EServiceDescriptorState archivingState(EServiceDescriptorState state) {
        return switch (state) {
            case PUBLISHED, DEPRECATED -> ARCHIVING;
            case SUSPENDED -> ARCHIVING_SUSPENDED;
            case ARCHIVING, ARCHIVING_SUSPENDED, ARCHIVED -> state;
            default -> throw new IllegalStateException(
                    "Whole e-service archiving setup requires published versions; unsupported state " + state);
        };
    }
}
