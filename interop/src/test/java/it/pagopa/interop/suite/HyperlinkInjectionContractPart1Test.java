package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.*;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        BffAgreementContractTest.class,
        BffCertifiedAttributeContractTest.class,
        BffCertifiedDiscreteAttributeContractTest.class,
        BffClientContractTest.class,
        BffDeclaredAttributeContractTest.class,
        BffEServiceTemplateContractTest.class,
        BffVerifiedAttributeContractTest.class,
        BffClientContractTest.class,
        BffEServiceContractTest.class,
        BffProducerKeychainContractTest.class,
})
@ConfigurationParameters({
        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.enabled",
                value = "true"
        ),
        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.mode.default",
                value = "concurrent"
        ),
        @ConfigurationParameter(
                key = "junit.jupiter.execution.parallel.config.strategy",
                value = "dynamic"
        )
})
/* TODO 29/09/2026 classe di comodo che raccoglie i soli test di contratto implementati per la
 *   parte 1 di Hyperlink injection. Al tendere, essendo che tutti i test di contratto testano
 *   anche hyperlink injection, si potrà rimuovere OPPURE configurare che vengano eseguiti
 *   non tutti i fuzzy test, ma solo quelli della rule REPLACED_WITH_URL_INJECTION. Da decidere. */
public class HyperlinkInjectionContractPart1Test {
}
