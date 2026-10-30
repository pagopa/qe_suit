package it.pagopa.interop.architecture;

import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfigurationLoader;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import it.pagopa.infrastructure.reporting.contract.validation.ContractReportDefinitionValidator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractReportDefinitionsTest {

    @Test
    void contract_factories_must_be_resolvable_by_report() throws Exception {
        Path sourceRoot = Path.of("src/test/java");
        var configuration = new ContractReportConfigurationLoader().load();
        var validator = new ContractReportDefinitionValidator(
                ContractTargetResolverRegistry.defaults(sourceRoot)
        );
        var violations = validator.validate(sourceRoot, configuration);
        assertTrue(violations.isEmpty(),
                () -> "Contract reporting definitions are invalid:\n"
                        + String.join("\n", violations));
    }
}
