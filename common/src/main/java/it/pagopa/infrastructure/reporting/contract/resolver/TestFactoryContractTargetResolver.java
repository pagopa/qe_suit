package it.pagopa.infrastructure.reporting.contract.resolver;

/**
 * Target delle factory che provano piu' endpoint, come i contract test di autorizzazione: non
 * esiste un endpoint singolo da indicare, perche' il nome del metodo non lo nomina e l'operazione
 * e' nota solo a runtime, dopo l'invocazione del supplier. Il target e' quindi il metodo
 * {@code @TestFactory} stesso.
 * <p>
 * Esiste perche' {@link ContractTargetResolverRegistry} richiede un resolver per ogni
 * {@code target-type}; metodo e URI di ogni chiamata restano nei log e nel messaggio di
 * fallimento, dove l'operazione e' gia' stata costruita.
 */
public class TestFactoryContractTargetResolver implements ContractTargetResolver {

    @Override
    public String resolveTarget(ContractFactoryContext context) {
        return context.methodName();
    }
}
