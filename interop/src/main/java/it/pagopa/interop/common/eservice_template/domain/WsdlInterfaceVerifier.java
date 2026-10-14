package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;

import java.util.*;

public final class WsdlInterfaceVerifier {
    private final WsdlAddressReader addressReader = new WsdlAddressReader();
    private final WsdlLocationSpanScanner locationScanner = new WsdlLocationSpanScanner();

    public void assertWellFormed(byte[] document) {
        addressReader.assertWellFormed(document);
    }

    public void assertAddresses(byte[] document, List<SoapAddressConfiguration> expected) {
        List<WsdlAddressReader.SoapAddress> actual = addressReader.parse(document);
        Map<PortKey, Deque<String>> expectedByPort = new HashMap<>();
        for (SoapAddressConfiguration address : expected) {
            PortKey key = new PortKey(address.service(), address.port());
            expectedByPort.computeIfAbsent(key, ignored -> new ArrayDeque<>()).addLast(address.address());
        }
        for (WsdlAddressReader.SoapAddress address : actual) {
            Deque<String> locations = expectedByPort.get(new PortKey(address.service(), address.port()));
            if (locations == null || locations.isEmpty()) {
                throw new AssertionError("Indirizzo SOAP inatteso per %s/%s: %s".formatted(
                        address.service(), address.port(), address.location()
                ));
            }
            String expectedLocation = locations.removeFirst();
            if (!expectedLocation.equals(address.location())) {
                throw new AssertionError("Indirizzo SOAP errato per %s/%s: atteso <%s>, trovato <%s>".formatted(
                        address.service(), address.port(), expectedLocation, address.location()
                ));
            }
        }
        expectedByPort.forEach((key, locations) -> {
            if (!locations.isEmpty()) {
                throw new AssertionError("Indirizzi SOAP mancanti per %s/%s: %s".formatted(
                        key.service(), key.port(), locations
                ));
            }
        });
        if (actual.size() != expected.size()) {
            throw new AssertionError("Numero di indirizzi SOAP differente: attesi %d, trovati %d".formatted(
                    expected.size(), actual.size()
            ));
        }
    }

    public void assertOnlyConfiguredLocationsDiffer(byte[] original, byte[] customized) {
        List<WsdlAddressReader.SoapAddress> originalAddresses = addressReader.parse(original);
        List<WsdlAddressReader.SoapAddress> customizedAddresses = addressReader.parse(customized);
        List<WsdlLocationSpanScanner.Span> originalSpans = locationScanner.scan(original);
        List<WsdlLocationSpanScanner.Span> customizedSpans = locationScanner.scan(customized);
        if (originalAddresses.size() != customizedAddresses.size()
                || originalAddresses.size() != originalSpans.size()
                || customizedAddresses.size() != customizedSpans.size()) {
            throw new AssertionError("Struttura degli indirizzi SOAP modificata");
        }
        int originalOffset = 0;
        int customizedOffset = 0;
        for (int index = 0; index < originalSpans.size(); index++) {
            WsdlLocationSpanScanner.Span originalSpan = originalSpans.get(index);
            WsdlLocationSpanScanner.Span customizedSpan = customizedSpans.get(index);
            assertEqualSegment(original, originalOffset, originalSpan.start(),
                    customized, customizedOffset, customizedSpan.start());
            originalOffset = originalSpan.end();
            customizedOffset = customizedSpan.end();
        }
        assertEqualSegment(original, originalOffset, original.length,
                customized, customizedOffset, customized.length);
    }

    private static void assertEqualSegment(
            byte[] first,
            int firstStart,
            int firstEnd,
            byte[] second,
            int secondStart,
            int secondEnd
    ) {
        if (!Arrays.equals(
                Arrays.copyOfRange(first, firstStart, firstEnd),
                Arrays.copyOfRange(second, secondStart, secondEnd)
        )) {
            throw new AssertionError("Sono cambiati byte esterni ai valori location SOAP");
        }
    }

    private record PortKey(String service, String port) {}
}
