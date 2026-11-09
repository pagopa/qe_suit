package it.pagopa.interop.common.eservice_template.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WsdlFixtureBindingTest {
    private static final String WSDL = "http://schemas.xmlsoap.org/wsdl/";
    private static final String SOAP11 = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12 = "http://schemas.xmlsoap.org/wsdl/soap12/";

    static Stream<Path> fixtures() throws Exception {
        Path directory = Path.of(WsdlFixtureBindingTest.class
                .getResource("/assets/eservice-template").toURI());
        try (var files = Files.list(directory)) {
            List<Path> fixtures = files.filter(path -> path.toString().endsWith(".wsdl")).sorted().toList();
            assertThat(fixtures).isNotEmpty();
            return fixtures.stream();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void every_binding_defines_the_port_type_operations_with_matching_soap_body(Path fixture) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Element definitions = factory.newDocumentBuilder().parse(fixture.toFile()).getDocumentElement();
        List<Element> bindings = children(definitions, WSDL, "binding");
        assertThat(bindings).isNotEmpty();

        for (Element binding : bindings) {
            String type = binding.getAttribute("type");
            String[] typeName = type.split(":", 2);
            String prefix = typeName.length == 2 ? typeName[0] : null;
            assertThat(binding.lookupNamespaceURI(prefix)).isEqualTo(definitions.getAttribute("targetNamespace"));
            Element portType = children(definitions, WSDL, "portType").stream()
                    .filter(element -> element.getAttribute("name").equals(typeName[typeName.length - 1]))
                    .findFirst().orElseThrow();
            List<Element> abstractOperations = children(portType, WSDL, "operation");
            List<Element> operations = children(binding, WSDL, "operation");
            assertThat(abstractOperations).isNotEmpty();
            assertThat(operations).extracting(element -> element.getAttribute("name"))
                    .containsExactlyInAnyOrderElementsOf(abstractOperations.stream()
                            .map(element -> element.getAttribute("name")).toList());

            List<Element> soapBindings = Stream.concat(
                    children(binding, SOAP11, "binding").stream(),
                    children(binding, SOAP12, "binding").stream()).toList();
            assertThat(soapBindings).hasSize(1);
            String namespace = soapBindings.get(0).getNamespaceURI();
            for (Element operation : operations) {
                List<Element> soapOperations = children(operation, namespace, "operation");
                assertThat(soapOperations).hasSize(1);
                assertThat(soapOperations.get(0).getAttribute("soapAction")).isNotBlank();
                Element abstractOperation = abstractOperations.stream()
                        .filter(element -> element.getAttribute("name").equals(operation.getAttribute("name")))
                        .findFirst().orElseThrow();
                for (String direction : List.of("input", "output")) {
                    List<Element> messages = children(operation, WSDL, direction);
                    assertThat(messages).hasSize(children(abstractOperation, WSDL, direction).size());
                    for (Element message : messages) {
                        List<Element> bodies = children(message, namespace, "body");
                        assertThat(bodies).hasSize(1);
                        assertThat(bodies.get(0).getAttribute("use")).isEqualTo("literal");
                    }
                }
            }
        }
    }

    private static List<Element> children(Element parent, String namespace, String name) {
        var nodes = parent.getChildNodes();
        return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .filter(element -> namespace.equals(element.getNamespaceURI()) && name.equals(element.getLocalName()))
                .toList();
    }
}
