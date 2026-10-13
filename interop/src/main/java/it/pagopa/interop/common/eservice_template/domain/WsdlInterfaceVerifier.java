package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import org.springframework.util.xml.StaxUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WsdlInterfaceVerifier {
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";

    public void assertWellFormed(byte[] document) {
        parse(document);
    }

    public void assertAddresses(byte[] document, List<SoapAddressConfiguration> expected) {
        List<SoapAddress> actual = parse(document);
        Map<PortKey, Deque<String>> expectedByPort = new HashMap<>();
        for (SoapAddressConfiguration address : expected) {
            PortKey key = new PortKey(address.service(), address.port());
            expectedByPort.computeIfAbsent(key, ignored -> new ArrayDeque<>()).addLast(address.address());
        }

        for (SoapAddress address : actual) {
            Deque<String> expectedLocations = expectedByPort.get(new PortKey(address.service(), address.port()));
            if (expectedLocations == null || expectedLocations.isEmpty()) {
                throw new AssertionError("Indirizzo SOAP inatteso per %s/%s: %s".formatted(
                        address.service(), address.port(), address.location()
                ));
            }
            String expectedLocation = expectedLocations.removeFirst();
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
        List<SoapAddress> originalAddresses = parse(original);
        List<SoapAddress> customizedAddresses = parse(customized);
        List<LocationSpan> originalSpans = addressLocationSpans(original);
        List<LocationSpan> customizedSpans = addressLocationSpans(customized);
        if (originalAddresses.size() != customizedAddresses.size()
                || originalAddresses.size() != originalSpans.size()
                || customizedAddresses.size() != customizedSpans.size()) {
            throw new AssertionError("Struttura degli indirizzi SOAP modificata");
        }

        int originalOffset = 0;
        int customizedOffset = 0;
        for (int index = 0; index < originalSpans.size(); index++) {
            LocationSpan originalSpan = originalSpans.get(index);
            LocationSpan customizedSpan = customizedSpans.get(index);
            assertEqualSegment(original, originalOffset, originalSpan.start(),
                    customized, customizedOffset, customizedSpan.start());
            originalOffset = originalSpan.end();
            customizedOffset = customizedSpan.end();
        }
        assertEqualSegment(original, originalOffset, original.length,
                customized, customizedOffset, customized.length);
    }

    private List<SoapAddress> parse(byte[] document) {
        XMLInputFactory factory = StaxUtils.createDefensiveInputFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

        List<SoapAddress> addresses = new ArrayList<>();
        String serviceName = null;
        String portName = null;
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(document));
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String namespace = reader.getNamespaceURI();
                    String localName = reader.getLocalName();
                    if (WSDL_NAMESPACE.equals(namespace) && "service".equals(localName)) {
                        serviceName = reader.getAttributeValue(null, "name");
                    } else if (WSDL_NAMESPACE.equals(namespace) && "port".equals(localName)) {
                        portName = reader.getAttributeValue(null, "name");
                    } else if ((SOAP11_NAMESPACE.equals(namespace) || SOAP12_NAMESPACE.equals(namespace))
                            && "address".equals(localName)) {
                        String location = reader.getAttributeValue(null, "location");
                        if (serviceName == null || portName == null || location == null) {
                            throw new AssertionError("Elemento SOAP address privo di servizio, porta o location");
                        }
                        addresses.add(new SoapAddress(serviceName, portName, location));
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String namespace = reader.getNamespaceURI();
                    String localName = reader.getLocalName();
                    if (WSDL_NAMESPACE.equals(namespace) && "port".equals(localName)) {
                        portName = null;
                    } else if (WSDL_NAMESPACE.equals(namespace) && "service".equals(localName)) {
                        serviceName = null;
                        portName = null;
                    }
                }
            }
            reader.close();
        } catch (XMLStreamException exception) {
            throw new AssertionError("Il WSDL non è XML ben formato", exception);
        }
        return addresses;
    }

    private List<LocationSpan> addressLocationSpans(byte[] document) {
        List<LocationSpan> spans = new ArrayList<>();
        Deque<Map<String, String>> namespaceScopes = new ArrayDeque<>();
        int index = 0;
        while (index < document.length) {
            if (document[index] != '<') {
                index++;
                continue;
            }
            if (startsWith(document, index, "<!--")) {
                index = after(document, index + 4, "-->");
                continue;
            }
            if (startsWith(document, index, "<![CDATA[")) {
                index = after(document, index + 9, "]]>");
                continue;
            }
            if (startsWith(document, index, "<?")) {
                index = after(document, index + 2, "?>");
                continue;
            }
            if (startsWith(document, index, "<!")) {
                index = afterDeclaration(document, index + 2);
                continue;
            }
            if (startsWith(document, index, "</")) {
                index = after(document, index + 2, ">");
                if (!namespaceScopes.isEmpty()) namespaceScopes.pop();
                continue;
            }

            int tagStart = index + 1;
            int cursor = tagStart;
            while (cursor < document.length && !isDelimiter(document[cursor])) cursor++;
            String qualifiedName = utf8(document, tagStart, cursor);
            int prefixSeparator = qualifiedName.indexOf(':');
            String prefix = prefixSeparator < 0 ? "" : qualifiedName.substring(0, prefixSeparator);
            String localName = qualifiedName.substring(qualifiedName.indexOf(':') + 1);
            Map<String, String> namespaces = namespaceScopes.isEmpty()
                    ? new HashMap<>()
                    : new HashMap<>(namespaceScopes.peek());
            List<LexicalAttribute> attributes = new ArrayList<>();
            boolean selfClosing = false;
            while (cursor < document.length) {
                cursor = skipWhitespace(document, cursor);
                if (cursor >= document.length || document[cursor] == '>') {
                    cursor++;
                    break;
                }
                if (document[cursor] == '/') {
                    selfClosing = true;
                    cursor++;
                    cursor = skipWhitespace(document, cursor);
                    if (cursor < document.length && document[cursor] == '>') cursor++;
                    break;
                }
                int nameStart = cursor;
                while (cursor < document.length && !isAttributeDelimiter(document[cursor])) cursor++;
                String attributeName = utf8(document, nameStart, cursor);
                cursor = skipWhitespace(document, cursor);
                if (cursor >= document.length || document[cursor] != '=') {
                    throw new AssertionError("Attributo XML non valido nel WSDL");
                }
                cursor = skipWhitespace(document, cursor + 1);
                if (cursor >= document.length || (document[cursor] != '\'' && document[cursor] != '"')) {
                    throw new AssertionError("Valore attributo XML non quotato nel WSDL");
                }
                byte quote = document[cursor++];
                int valueStart = cursor;
                while (cursor < document.length && document[cursor] != quote) cursor++;
                if (cursor >= document.length) {
                    throw new AssertionError("Valore attributo XML non terminato nel WSDL");
                }
                attributes.add(new LexicalAttribute(
                        attributeName,
                        utf8(document, valueStart, cursor),
                        new LocationSpan(valueStart, cursor)
                ));
                cursor++;
            }

            for (LexicalAttribute attribute : attributes) {
                if ("xmlns".equals(attribute.name())) {
                    namespaces.put("", attribute.value());
                } else if (attribute.name().startsWith("xmlns:")) {
                    namespaces.put(attribute.name().substring("xmlns:".length()), attribute.value());
                }
            }
            String elementNamespace = namespaces.get(prefix);
            if ("address".equals(localName)
                    && (SOAP11_NAMESPACE.equals(elementNamespace) || SOAP12_NAMESPACE.equals(elementNamespace))) {
                attributes.stream()
                        .filter(attribute -> "location".equals(attribute.name()))
                        .map(LexicalAttribute::span)
                        .forEach(spans::add);
            }
            if (!selfClosing) namespaceScopes.push(namespaces);
            index = cursor;
        }
        return spans;
    }

    private static void assertEqualSegment(
            byte[] first, int firstStart, int firstEnd,
            byte[] second, int secondStart, int secondEnd
    ) {
        if (!Arrays.equals(
                Arrays.copyOfRange(first, firstStart, firstEnd),
                Arrays.copyOfRange(second, secondStart, secondEnd)
        )) {
            throw new AssertionError("Sono cambiati byte esterni ai valori location SOAP");
        }
    }

    private static boolean startsWith(byte[] value, int offset, String token) {
        byte[] expected = token.getBytes(StandardCharsets.US_ASCII);
        if (offset + expected.length > value.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (value[offset + i] != expected[i]) return false;
        }
        return true;
    }

    private static int after(byte[] value, int offset, String token) {
        while (offset < value.length) {
            if (startsWith(value, offset, token)) return offset + token.length();
            offset++;
        }
        throw new AssertionError("Token XML non terminato: " + token);
    }

    private static int afterDeclaration(byte[] value, int offset) {
        byte quote = 0;
        int subsetDepth = 0;
        for (int i = offset; i < value.length; i++) {
            byte current = value[i];
            if (quote != 0) {
                if (current == quote) quote = 0;
            } else if (current == '\'' || current == '"') {
                quote = current;
            } else if (current == '[') {
                subsetDepth++;
            } else if (current == ']') {
                subsetDepth--;
            } else if (current == '>' && subsetDepth == 0) {
                return i + 1;
            }
        }
        throw new AssertionError("Dichiarazione XML non terminata");
    }

    private static int skipWhitespace(byte[] value, int offset) {
        while (offset < value.length && Character.isWhitespace((char) value[offset])) offset++;
        return offset;
    }

    private static boolean isDelimiter(byte value) {
        return Character.isWhitespace((char) value) || value == '/' || value == '>';
    }

    private static boolean isAttributeDelimiter(byte value) {
        return Character.isWhitespace((char) value) || value == '=' || value == '/' || value == '>';
    }

    private static String utf8(byte[] value, int start, int end) {
        return new String(value, start, end - start, StandardCharsets.UTF_8);
    }

    private record PortKey(String service, String port) {}

    private record SoapAddress(String service, String port, String location) {}

    private record LocationSpan(int start, int end) {}

    private record LexicalAttribute(String name, String value, LocationSpan span) {}
}
