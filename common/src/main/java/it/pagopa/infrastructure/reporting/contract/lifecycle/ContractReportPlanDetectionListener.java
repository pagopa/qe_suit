package it.pagopa.infrastructure.reporting.contract.lifecycle;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContractReportPlanDetectionListener implements TestExecutionListener {

    private static final Pattern UNIQUE_ID_CLASS_OR_SUITE = Pattern.compile("\\[(class|suite):([^\\]]+)]");
    private final ContractReportRunState runState;

    public ContractReportPlanDetectionListener(ContractReportRunState runState) {
        this.runState = runState;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        boolean enabled = testPlan.getRoots().stream()
                .flatMap(root -> testPlan.getDescendants(root).stream())
                .anyMatch(this::isAnnotatedRunner);
        if (enabled) {
            runState.enableReport();
        }
    }

    private boolean isAnnotatedRunner(TestIdentifier identifier) {
        Optional<String> fromSource = extractClassNameFromSource(identifier.getSource());
        Optional<String> fromUniqueId = extractClassNameFromUniqueId(identifier.getUniqueIdObject());
        Optional<Class<?>> classFromSource = fromSource.flatMap(this::loadClass);
        if (classFromSource.isPresent()) {
            return classFromSource.get().isAnnotationPresent(GenerateContractReport.class);
        }
        return fromUniqueId.flatMap(this::loadClass)
                .map(clazz -> clazz.isAnnotationPresent(GenerateContractReport.class))
                .orElse(false);
    }

    private Optional<String> extractClassNameFromSource(Optional<TestSource> source) {
        if (source.isEmpty()) {
            return Optional.empty();
        }
        TestSource rawSource = source.get();
        if (rawSource instanceof ClassSource classSource) {
            return Optional.of(classSource.getClassName());
        }
        if (rawSource instanceof MethodSource methodSource) {
            return Optional.of(methodSource.getClassName());
        }
        return Optional.empty();
    }

    private Optional<String> extractClassNameFromUniqueId(UniqueId uniqueId) {
        if (uniqueId == null) {
            return Optional.empty();
        }
        Matcher matcher = UNIQUE_ID_CLASS_OR_SUITE.matcher(uniqueId.toString());
        if (matcher.find()) {
            return Optional.of(matcher.group(2));
        }
        return Optional.empty();
    }

    private Optional<Class<?>> loadClass(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException ignored) {
            return Optional.empty();
        }
    }
}
