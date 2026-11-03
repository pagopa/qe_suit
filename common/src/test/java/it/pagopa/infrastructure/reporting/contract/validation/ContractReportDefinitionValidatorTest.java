package it.pagopa.infrastructure.reporting.contract.validation;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolver;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContractReportDefinitionValidatorTest {

    @TempDir
    Path root;

    @Test
    void delegates_each_factory_to_registered_resolver_without_executing_it() throws Exception {
        source("WebExampleContractTest", """
                @TestFactory void first() { throw new IllegalStateException(); }
                @org.junit.jupiter.api.TestFactory void second() {}
                void helper() {}
                """);
        List<ContractFactoryContext> contexts = new ArrayList<>();
        var validator = validator(context -> {
            contexts.add(context);
            return "resolved-by-custom-resolver";
        });
        assertTrue(validator.validate(root, configuration()).isEmpty());
        assertEquals(List.of("first", "second"),
                contexts.stream().map(ContractFactoryContext::methodName).toList());
        assertEquals("example.WebExampleContractTest", contexts.get(0).contractClassName());
    }

    @Test
    void aggregates_resolver_errors_for_all_factories() throws Exception {
        source("WebExampleContractTest", "@TestFactory void first() {} @TestFactory void second() {}");
        var violations = validator(context -> {
            throw new IllegalStateException("Resolver policy changed");
        }).validate(root, configuration());
        assertEquals(2, violations.size());
        assertTrue(violations.get(0).contains("#first"));
        assertTrue(violations.get(1).contains("Resolver policy changed"));
    }

    @Test
    void rejects_missing_channel_instead_of_silently_skipping_it() throws Exception {
        source("UnknownContractTest", "@TestFactory void factory() {}");
        var violations = validator(context -> "target").validate(root, configuration());
        assertEquals(1, violations.size());
        assertTrue(violations.get(0).contains("No reporting channel"));
    }

    @Test
    void uses_actual_page_resolver_from_shared_default_registry() throws Exception {
        source("WebExampleContractTest", "@TestFactory void factory() { validator.on(ExamplePage.class); }");
        var validator = new ContractReportDefinitionValidator(
                ContractTargetResolverRegistry.defaults(root));
        assertTrue(validator.validate(root, configuration()).isEmpty());
        source("WebExampleContractTest", "@TestFactory void factory() {}");
        var violations = validator.validate(root, configuration());
        assertEquals(1, violations.size());
        assertTrue(violations.get(0).contains("No .on(...)"));
    }

    @Test
    void rejects_empty_targets_and_missing_resolvers() throws Exception {
        source("WebExampleContractTest", "@TestFactory void factory() {}");
        assertTrue(validator(context -> " ").validate(root, configuration())
                .get(0).contains("empty target"));
        var validator = new ContractReportDefinitionValidator(
                new ContractTargetResolverRegistry(Map.of()));
        assertTrue(validator.validate(root, configuration())
                .get(0).contains("No ContractTargetResolver registered"));
    }

    @Test
    void reports_invalid_sources_and_empty_scan() throws Exception {
        assertFalse(validator(context -> "target").validate(root, configuration()).isEmpty());
        source("WebBrokenContractTest", "@TestFactory void broken(");
        assertTrue(validator(context -> "target").validate(root, configuration())
                .get(0).contains("Cannot parse source"));
    }

    @Test
    void rejects_missing_source_root() {
        assertThrows(IllegalArgumentException.class,
                () -> validator(context -> "target")
                        .validate(root.resolve("missing"), configuration()));
    }

    private ContractReportDefinitionValidator validator(ContractTargetResolver resolver) {
        return new ContractReportDefinitionValidator(new ContractTargetResolverRegistry(
                Map.of(ContractTargetType.PAGE, resolver)));
    }

    private ContractReportConfig configuration() {
        return new ContractReportConfig(Map.of("web", new ContractChannelConfig(
                "web", "Web", "Web", ContractTargetType.PAGE, null)));
    }

    private void source(String name, String methods) throws Exception {
        Path file = root.resolve("example/" + name + ".java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "package example; public class " + name + " { " + methods + " }");
    }
}
