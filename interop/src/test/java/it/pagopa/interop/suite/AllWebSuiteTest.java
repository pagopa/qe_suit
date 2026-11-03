package it.pagopa.interop.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import it.pagopa.interop.suite.contract.*;
import org.junit.platform.suite.api.*;

@Suite
@GenerateContractReport
@IncludeEngines("junit-jupiter")
@SelectClasses({
        WebAgreementRequestPaginationContractTest.class,
        WebConsumerAgreementDetailsPageContractTest.class,
        WebConsumerAgreementsListPageContractTest.class,
        WebConsumerPurposeDetailsPageContractTest.class,
        WebConsumerPurposesListPageContractTest.class,
        WebConsumerPurposeTemplateCatalogPageContractTest.class,
        WebDebugClientAssertionContractTest.class,
        WebEServiceCatalogContractTest.class,
        WebEServiceContractTest.class,
        WebEServiceCreatePageContractTest.class,
        WebEServiceDetailsCatalogPageContractTest.class,
        WebEServiceDuplicationContractTest.class,
        WebEServiceProviderListContractTest.class,
        WebEServiceThresholdContractTest.class,
        WebProducerAgreementContractTest.class,
        WebProducerEServiceAttributesContractTest.class,
        WebProducerPurposeContractTest.class,
        WebProviderEServiceSummaryContractTest.class,
        WebPurposeTemplateContractTest.class,
        WebTOSContractTest.class,
})
@ExcludeTags("ignore")
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
public class AllWebSuiteTest {
}
