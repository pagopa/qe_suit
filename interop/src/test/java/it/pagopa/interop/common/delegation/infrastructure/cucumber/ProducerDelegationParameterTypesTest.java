package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.ParameterType;
import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;
import it.pagopa.interop.common.infrastructure.cucumber.parameter_type.CommonParameterTypes;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProducerDelegationParameterTypesTest {

    @Test
    void mapsEverySupportedItalianState() {
        var parameterTypes = new ProducerDelegationParameterTypes();

        assertThat(parameterTypes.producerDelegationState("attiva"))
                .isEqualTo(ProducerDelegationState.ACTIVE);
        assertThat(parameterTypes.producerDelegationState("in attesa di accettazione"))
                .isEqualTo(ProducerDelegationState.WAITING_FOR_APPROVAL);
        assertThat(parameterTypes.producerDelegationState("rifiutata"))
                .isEqualTo(ProducerDelegationState.REJECTED);
        assertThat(parameterTypes.producerDelegationState("revocata"))
                .isEqualTo(ProducerDelegationState.REVOKED);
    }

    @Test
    void bindsEachCatalogFeaturePrepositionForm() throws NoSuchMethodException {
        var expressions = Arrays.stream(ProducerDelegationSteps.class.getMethod(
                        "setUpProducerDelegation",
                        String.class,
                        it.pagopa.interop.common.kernel.domain.Tenant.class,
                        it.pagopa.interop.common.kernel.domain.Tenant.class,
                        ProducerDelegationState.class
                ).getAnnotationsByType(Given.class))
                .map(Given::value)
                .toList();

        assertThat(expressions).containsExactlyInAnyOrder(
                "l'e-service {string} delegato in erogazione dal {tenant} al {tenant} con delega {producerDelegationState}",
                "l'e-service {string} delegato in erogazione da {tenant} al {tenant} con delega {producerDelegationState}",
                "l'e-service {string} delegato in erogazione dal {tenant} a {tenant} con delega {producerDelegationState}"
        );
    }

    @Test
    void cucumberExpressionsMatchCatalogPhrasesAndTransformEveryState() throws NoSuchMethodException {
        var registry = new ParameterTypeRegistry(Locale.ITALIAN);
        var tenantMethod = CommonParameterTypes.class.getMethod("tenant", String.class);
        registry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<>(
                "tenant", tenantMethod.getAnnotation(ParameterType.class).value(),
                Tenant.class, Tenant::fromAlias));
        var stateMethod = ProducerDelegationParameterTypes.class.getMethod("producerDelegationState", String.class);
        registry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<>(
                "producerDelegationState", stateMethod.getAnnotation(ParameterType.class).value(),
                ProducerDelegationState.class, new ProducerDelegationParameterTypes()::producerDelegationState));
        var factory = new ExpressionFactory(registry);
        var expressions = Arrays.stream(ProducerDelegationSteps.class.getMethod(
                        "setUpProducerDelegation", String.class, Tenant.class, Tenant.class, ProducerDelegationState.class
                ).getAnnotationsByType(Given.class))
                .map(Given::value).map(factory::createExpression).toList();
        var forms = List.of(
                "dal Comune di Milano al Comune di Pozzallo",
                "da Sogecap al Comune di Pozzallo",
                "dal Comune di Pozzallo a Sogecap"
        );
        var delegators = List.of(Tenant.COMUNE_DI_MILANO, Tenant.SOGECAP, Tenant.COMUNE_DI_POZZALLO);
        var delegates = List.of(Tenant.COMUNE_DI_POZZALLO, Tenant.COMUNE_DI_POZZALLO, Tenant.SOGECAP);
        var states = Map.of(
                "attiva", ProducerDelegationState.ACTIVE,
                "in attesa di accettazione", ProducerDelegationState.WAITING_FOR_APPROVAL,
                "rifiutata", ProducerDelegationState.REJECTED,
                "revocata", ProducerDelegationState.REVOKED
        );

        for (int i = 0; i < forms.size(); i++) {
            for (var state : states.entrySet()) {
                String phrase = "l'e-service \"ES-1\" delegato in erogazione " + forms.get(i)
                        + " con delega " + state.getKey();
                var matches = expressions.stream().map(expression -> expression.match(phrase))
                        .filter(java.util.Objects::nonNull).toList();
                assertThat(matches).as(phrase).hasSize(1);
                assertThat(matches.get(0)).extracting(argument -> (Object) argument.getValue())
                        .containsExactly("ES-1", delegators.get(i), delegates.get(i), state.getValue());
            }
        }
    }
}
