package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceArchivingUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.infrastructure.cucumber.parameter_type.CommonParameterTypes;
import it.pagopa.interop.common.infrastructure.cucumber.parameter_type.ScenarioParameterTypes;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EServiceArchivingStepsTest {
    @Test
    void selectsTheExplicitProducerAndDelegatesWholeServiceArchiving() {
        var archiving = mock(EServiceArchivingUseCase.class);
        var session = mock(AuthSession.class);
        var service = EService.builder().id(UUID.randomUUID()).build();
        var steps = new EServiceArchivingSteps(archiving, session);

        steps.archiveEntireEService(service, Tenant.COMUNE_DI_MILANO);

        var order = inOrder(session, archiving);
        order.verify(session).set(Tenant.COMUNE_DI_MILANO);
        order.verify(archiving).archiveEntireEService(service);
        order.verifyNoMoreInteractions();
    }

    @Test
    void bindsCurrentAndAliasedServiceWithoutAmbiguity() throws NoSuchMethodException {
        var store = mock(EntityStore.class);
        var service = EService.builder().id(UUID.randomUUID()).build();
        when(store.getLastOrThrow(EService.class)).thenReturn(service);
        when(store.getByAliasOrThrow("ES-1", EService.class)).thenReturn(service);
        var current = new ScenarioParameterTypes(store);
        var aliases = new EServiceDescriptorParameterTypes(store);
        var registry = new ParameterTypeRegistry(Locale.ITALIAN);
        registry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<>(
                "currentEService", ScenarioParameterTypes.class.getMethod("currentEService", String.class)
                        .getAnnotation(ParameterType.class).value(),
                EService.class, current::currentEService));
        registry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<>(
                "eServiceByAlias", EServiceDescriptorParameterTypes.class.getMethod("eServiceByAlias", String.class)
                        .getAnnotation(ParameterType.class).value(),
                EService.class, aliases::eServiceByAlias));
        registry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<>(
                "tenant", CommonParameterTypes.class.getMethod("tenant", String.class)
                        .getAnnotation(ParameterType.class).value(),
                Tenant.class, Tenant::fromAlias));
        var factory = new ExpressionFactory(registry);
        var expressions = Arrays.stream(EServiceArchivingSteps.class
                        .getMethod("archiveEntireEService", EService.class, Tenant.class)
                        .getAnnotationsByType(Given.class))
                .map(Given::value).map(factory::createExpression).toList();

        for (String phrase : List.of(
                "l'e-service del Comune di Milano interamente in archiviazione",
                "l'e-service \"ES-1\" del Comune di Milano interamente in archiviazione")) {
            var matches = expressions.stream().map(expression -> expression.match(phrase))
                    .filter(Objects::nonNull).toList();
            assertThat(matches).as(phrase).hasSize(1);
            assertThat(matches.get(0)).extracting(argument -> (Object) argument.getValue())
                    .containsExactly(service, Tenant.COMUNE_DI_MILANO);
        }
    }
}
