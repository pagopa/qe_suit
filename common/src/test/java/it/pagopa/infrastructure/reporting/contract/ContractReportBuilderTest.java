package it.pagopa.infrastructure.reporting.contract;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.infrastructure.reporting.contract.model.ContractScenarioStatus;
import it.pagopa.infrastructure.reporting.contract.parser.JUnitExecutionNode;
import it.pagopa.infrastructure.reporting.contract.parser.JUnitExecutionStatus;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolver;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractReportBuilderTest {

    @Test
    void dynamicTestUniqueIdContainingFactorySegmentIsNotFactory() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode dynamic = node("1", null, "[class:x]/[test-factory:createAgreement()]/[dynamic-test:#1]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.SUCCESSFUL);
        ContractReport report = builder.build(List.of(dynamic), config(), Instant.parse("2026-09-25T10:00:00Z"));
        assertEquals(0, report.groups().size());
        assertEquals(0, countingResolver.calls.get());
    }

    @Test
    void factoryUniqueIdWithoutDynamicSegmentIsFactoryAndTargetResolvedOnce() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode factory = node("1", null, "[class:x]/[test-factory:createAgreement()]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.FAILED);
        factory.setStartedAt(Instant.parse("2026-09-25T10:00:00Z"));
        factory.setFinishedAt(Instant.parse("2026-09-25T10:00:01Z"));
        ContractReport report = builder.build(List.of(factory), config(), Instant.parse("2026-09-25T10:10:00Z"));
        assertEquals(1, report.groups().size());
        assertEquals(1, countingResolver.calls.get());
        assertEquals("TARGET:createAgreement", report.groups().get(0).target());
    }

    @Test
    void dynamicTestsAreAttachedByParentId() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode factory = node("10", null, "[class:x]/[test-factory:createAgreement()]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.SUCCESSFUL);
        JUnitExecutionNode dynamic = node("11", "10", "[class:x]/[test-factory:createAgreement()]/[dynamic-test:#1]", null, null, JUnitExecutionStatus.SUCCESSFUL);
        factory.addChild(dynamic);
        ContractReport report = builder.build(List.of(factory, dynamic), config(), Instant.parse("2026-09-25T10:10:00Z"));
        assertEquals(1, report.groups().get(0).scenarioCount());
    }

    @Test
    void classContainerWithContractClassNameIsNotTargetResolved() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode classContainer = node("1", null, "[class:it.pagopa.interop.suite.contract.BffAgreementContractTest]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", null, JUnitExecutionStatus.SUCCESSFUL);
        JUnitExecutionNode factory = node("2", "1", "[class:x]/[test-factory:createAgreement()]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.FAILED);
        factory.setStartedAt(Instant.parse("2026-09-25T10:00:00Z"));
        factory.setFinishedAt(Instant.parse("2026-09-25T10:00:01Z"));
        classContainer.addChild(factory);
        ContractReport report = builder.build(List.of(classContainer, factory), config(), Instant.parse("2026-09-25T10:10:00Z"));
        assertEquals(1, report.groups().size());
        assertEquals(1, countingResolver.calls.get());
    }

    @Test
    void failedFactoryWithoutDynamicChildrenCreatesPreconditionFailedScenario() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode factory = node("1", null, "[class:x]/[test-factory:createAgreement()]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.FAILED);
        factory.setStartedAt(Instant.parse("2026-09-25T10:00:00Z"));
        factory.setFinishedAt(Instant.parse("2026-09-25T10:00:01Z"));
        ContractReport report = builder.build(List.of(factory), config(), Instant.parse("2026-09-25T10:10:00Z"));
        assertEquals(1, report.total());
        assertEquals(1, report.failed());
        assertEquals(1, report.preconditionFailedCount());
        assertEquals("Precondition failed", report.groups().get(0).scenarios().get(0).displayName());
    }

    @Test
    void summaryCountsAndEffectiveDurationAreComputedFromScenarios() {
        CountingResolver countingResolver = new CountingResolver();
        ContractReportBuilder builder = builder(countingResolver);
        JUnitExecutionNode factory = node("1", null, "[class:x]/[test-factory:createAgreement()]", "it.pagopa.interop.suite.contract.BffAgreementContractTest", "createAgreement", JUnitExecutionStatus.SUCCESSFUL);
        JUnitExecutionNode a = node("2", "1", "[class:x]/[test-factory:createAgreement()]/[dynamic-test:#1]", null, null, JUnitExecutionStatus.SUCCESSFUL);
        a.setStartedAt(Instant.parse("2026-09-25T10:00:00Z"));
        a.setFinishedAt(Instant.parse("2026-09-25T10:00:10Z"));
        JUnitExecutionNode b = node("3", "1", "[class:x]/[test-factory:createAgreement()]/[dynamic-test:#2]", null, null, JUnitExecutionStatus.FAILED);
        b.setStartedAt(Instant.parse("2026-09-25T10:00:02Z"));
        b.setFinishedAt(Instant.parse("2026-09-25T10:00:08Z"));
        factory.addChild(a);
        factory.addChild(b);
        ContractReport report = builder.build(List.of(factory, a, b), config(), Instant.parse("2026-09-25T10:10:00Z"));

        assertEquals(2, report.total());
        assertEquals(1, report.passed());
        assertEquals(1, report.failed());
        assertEquals(10_000, report.effectiveDuration().toMillis());
        assertTrue(report.groups().get(0).aggregateStatus() == ContractScenarioStatus.FAILED);
    }

    private ContractReportBuilder builder(ContractTargetResolver resolver) {
        ContractTargetResolverRegistry registry = new ContractTargetResolverRegistry(Map.of(
                ContractTargetType.OPENAPI, resolver,
                ContractTargetType.PAGE, resolver
        ));
        return new ContractReportBuilder(registry);
    }

    private ContractReportConfig config() {
        ContractChannelConfig bff = new ContractChannelConfig("bff", "BFF", "Bff", ContractTargetType.OPENAPI, "dummy");
        return new ContractReportConfig(Map.of("bff", bff));
    }

    private JUnitExecutionNode node(String id, String parentId, String uniqueId, String className, String methodName, JUnitExecutionStatus status) {
        JUnitExecutionNode node = new JUnitExecutionNode(id);
        node.setParentId(parentId);
        node.setUniqueId(uniqueId);
        node.setClassName(className);
        node.setMethodName(methodName);
        node.setDisplayName(uniqueId);
        node.setStatus(status);
        return node;
    }

    static class CountingResolver implements ContractTargetResolver {
        final AtomicInteger calls = new AtomicInteger();

        @Override
        public String resolveTarget(ContractFactoryContext context) {
            calls.incrementAndGet();
            return "TARGET:" + context.methodName();
        }
    }
}
