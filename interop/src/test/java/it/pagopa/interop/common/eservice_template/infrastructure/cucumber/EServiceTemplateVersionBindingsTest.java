package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;
import io.cucumber.java.en.And;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EServiceTemplateVersionBindingsTest {
    @Test
    void simple_and_explicit_template_bindings_select_entities_in_the_resolver() throws Exception {
        var store = new InMemoryEntityStore();
        var first = EServiceTemplateVersion.builder().id(UUID.randomUUID()).version("1").build();
        var latest = EServiceTemplateVersion.builder().id(UUID.randomUUID()).version("10").build();
        store.upsert(EServiceTemplate.builder().id(UUID.randomUUID()).versions(List.of(latest, first)).build());
        var resolver = new EServiceTemplateParameterTypes(store);
        var registry = new ParameterTypeRegistry(Locale.ITALIAN);
        String pattern = EServiceTemplateParameterTypes.class.getMethod("templateVersion", String.class)
                .getAnnotation(io.cucumber.java.ParameterType.class).value();
        registry.defineParameterType(new ParameterType<>("templateVersion", pattern,
                EServiceTemplateVersion.class, resolver::templateVersion));
        String binding = EServiceTemplateInterfaceSteps.class
                .getMethod("assertTemplateInterfaceUnchanged", EServiceTemplateVersion.class)
                .getAnnotation(And.class).value();
        var expression = new ExpressionFactory(registry).createExpression(binding);

        assertThat(expression.match("l'interfaccia del template è rimasta invariata")
                .get(0).getValue()).isSameAs(latest);
        assertThat(expression.match("l'interfaccia della versione 1 del template è rimasta invariata")
                .get(0).getValue()).isSameAs(first);
    }

    @Test
    void simple_and_explicit_descriptor_bindings_use_the_same_assertion() throws Exception {
        var store = new InMemoryEntityStore();
        var first = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1").build();
        var latest = EServiceDescriptor.builder().id(UUID.randomUUID()).version("10").build();
        store.upsert(EService.builder().id(UUID.randomUUID()).descriptors(List.of(latest, first)).build());
        var resolver = new EServiceTemplateParameterTypes(store);
        var registry = new ParameterTypeRegistry(Locale.ITALIAN);
        String pattern = EServiceTemplateParameterTypes.class.getMethod("eServiceDescriptor", String.class)
                .getAnnotation(io.cucumber.java.ParameterType.class).value();
        registry.defineParameterType(new ParameterType<>("eServiceDescriptor", pattern,
                EServiceDescriptor.class, resolver::eServiceDescriptor));
        String binding = SoapSteps.class.getMethod("assertWellFormed", EServiceDescriptor.class)
                .getAnnotation(And.class).value();
        var expression = new ExpressionFactory(registry).createExpression(binding);

        assertThat(expression.match("il WSDL del descriptor dell'e-service è XML ben formato")
                .get(0).getValue()).isSameAs(latest);
        assertThat(expression.match("il WSDL del descriptor versione 1 dell'e-service è XML ben formato")
                .get(0).getValue()).isSameAs(first);
    }
}
