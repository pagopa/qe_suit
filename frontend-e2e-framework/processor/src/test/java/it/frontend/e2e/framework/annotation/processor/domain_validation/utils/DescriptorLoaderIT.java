package it.frontend.e2e.framework.annotation.processor.domain_validation.utils;

import it.frontend.e2e.framework.annotation.processor.domain_validation.factory.DomainValidatorFactory;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.descriptor_parser.json.JsonDomainDescriptorParser;
import it.frontend.e2e.framework.core.meta.Descriptor;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DescriptorLoaderIT {

    @Test
    void shouldLoadValidDescriptorsAndSkipInvalidOnes() {
        DescriptorLoader loader =
                new DescriptorLoader(
                        new JsonDomainDescriptorParser(
                                new com.fasterxml.jackson.databind.ObjectMapper()
                        ),
                        new DomainValidatorFactory()
                );

        Messager messager = mock(Messager.class);

        List<Descriptor> domains =
                loader.loadAll(
                        getClass().getClassLoader(),
                        messager
                );

        // solo quello valido deve essere caricato
        assertEquals(1, domains.size());
        assertEquals("payments", domains.get(0).domainId);

        // errori segnalati
        verify(messager, atLeastOnce()).printMessage(
                eq(Diagnostic.Kind.ERROR),
                anyString()
        );
    }
}
