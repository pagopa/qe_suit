package it.pagopa.infrastructure.pst;

import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PstConfigTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsCompletePolicyAndOptionalOperationOverride() throws Exception {
        PstConfig config = load(configuration(true));

        assertEquals(200, config.successStatus());
        assertEquals(FuzzScenario.values().length, config.scenarioStatus().size());
        assertEquals(java.util.Set.of("updateResource"), config.operations());
        assertEquals(1, config.overrides().size());
        assertEquals(RequestScope.PAYLOAD, config.overrides().get(0).scope());
        assertEquals(NodePath.fromPointer("/sized"), config.overrides().get(0).target());
        assertEquals(202, config.overrides().get(0).status());
    }

    @Test
    void rejectsConfigurationMissingAnyScenarioStatus() throws Exception {
        assertThrows(PstConfigurationException.class, () -> load(configuration(false)));
    }

    private PstConfig load(String yaml) throws Exception {
        Path file = temporaryDirectory.resolve("pst.yaml");
        Files.writeString(file, yaml);
        return PstConfig.load(file);
    }

    private String configuration(boolean includeAllScenarios) {
        StringBuilder yaml = new StringBuilder("successStatus: 200\nscenarioStatus:\n");
        for (FuzzScenario scenario : FuzzScenario.values()) {
            if (!includeAllScenarios && scenario == FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM) continue;
            yaml.append("  ").append(scenario).append(": 400\n");
        }
        yaml.append("operations:\n  - updateResource\noverrides:\n")
                .append("  - operationId: updateResource\n")
                .append("    scope: PAYLOAD\n")
                .append("    target: /sized\n")
                .append("    scenario: REPLACED_WITH_EMPTY_STRING\n")
                .append("    status: 202\n");
        return yaml.toString();
    }
}
