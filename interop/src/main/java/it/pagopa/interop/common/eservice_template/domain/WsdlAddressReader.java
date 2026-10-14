package it.pagopa.interop.common.eservice_template.domain;

import org.springframework.util.xml.StaxUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

final class WsdlAddressReader {
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";

    void assertWellFormed(byte[] document) {
        XMLStreamReader reader = null;
        try {
            reader = inputFactory().createXMLStreamReader(new ByteArrayInputStream(document));
            while (reader.hasNext()) reader.next();
        } catch (XMLStreamException exception) {
            throw new AssertionError("Il WSDL non è XML ben formato", exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException exception) {
                    throw new AssertionError("Errore durante la chiusura del parser XML", exception);
                }
            }
        }
    }

    List<SoapAddress> parse(byte[] document) {
        XMLInputFactory factory = inputFactory();
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
                    } else if (isSoapAddress(namespace, localName)) {
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

    private static XMLInputFactory inputFactory() {
        XMLInputFactory factory = StaxUtils.createDefensiveInputFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    }

    private static boolean isSoapAddress(String namespace, String localName) {
        return "address".equals(localName)
                && (SOAP11_NAMESPACE.equals(namespace) || SOAP12_NAMESPACE.equals(namespace));
    }

    record SoapAddress(String service, String port, String location) {}
}
