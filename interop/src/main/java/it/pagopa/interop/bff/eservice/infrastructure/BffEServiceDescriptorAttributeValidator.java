package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class BffEServiceDescriptorAttributeValidator {
    private BffEServiceDescriptorAttributeValidator() {}

    static boolean matches(DescriptorAttributesSeed expected, Attributes actual) {
        if (expected == null) return true;
        if (actual == null) return false;
        var command = new BffUpdateEServiceDescriptorCommand();
        command.attributes(actual);
        var observed = command.getBffPayload().getAttributes();
        return groups(expected.getCertified()).equals(groups(observed.getCertified()))
                && groups(expected.getDeclared()).equals(groups(observed.getDeclared()))
                && groups(expected.getVerified()).equals(groups(observed.getVerified()));
    }

    private static List<Set<DescriptorAttributeSeed>> groups(List<List<DescriptorAttributeSeed>> groups) {
        return groups == null ? List.of() : groups.stream()
                .<Set<DescriptorAttributeSeed>>map(HashSet::new).toList();
    }
}
