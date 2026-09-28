package it.pagopa.infrastructure.pst.launcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;
import it.pagopa.infrastructure.pst.PstConfigurationException;
import it.pagopa.infrastructure.pst.fixture.PstFixtureApi;
import it.pagopa.infrastructure.pst.fixture.PstFixturePayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PstGenerationLauncherTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesReportThroughJdkTypedBoundary() throws Exception {
        Path output = temporaryDirectory.resolve("nested/pst/pst-report.html");
        Map<String, Object> result = PstGenerationLauncher.generate(arguments(writeConfig(), output, TestProfile.class));

        assertEquals(output.toString(), result.get(PstGenerationLauncher.RESULT_REPORT_PATH));
        assertEquals(1, result.get(PstGenerationLauncher.RESULT_OPERATION_COUNT));
        assertTrue((Integer) result.get(PstGenerationLauncher.RESULT_SCENARIO_COUNT) > 0);
        String html = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(html.contains("data-operation-id=\"updateResource\""));
        assertTrue(html.contains("PST fixture title"));
    }

    @Test
    void profileProvidesPlannersAndMapper() throws Exception {
        CountingProfile.calls = 0;
        Path output = temporaryDirectory.resolve("counting.html");
        PstGenerationLauncher.generate(arguments(writeConfig(), output, CountingProfile.class));
        assertEquals(3, CountingProfile.calls);
    }

    @Test
    void failsWhenProfileClassIsMissing() throws Exception {
        Map<String, String> arguments = arguments(writeConfig(), temporaryDirectory.resolve("x.html"), TestProfile.class);
        arguments.put(PstGenerationLauncher.FUZZING_PROFILE, "com.example.MissingProfile");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> PstGenerationLauncher.generate(arguments));
        assertTrue(error.getMessage().contains("com.example.MissingProfile"));
    }

    @Test
    void failsWhenProfileDoesNotImplementSpi() throws Exception {
        Map<String, String> arguments = arguments(writeConfig(), temporaryDirectory.resolve("x.html"), TestProfile.class);
        arguments.put(PstGenerationLauncher.FUZZING_PROFILE, String.class.getName());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> PstGenerationLauncher.generate(arguments));
        assertTrue(error.getMessage().contains("does not implement " + FuzzingProfile.class.getName()));
    }

    @Test
    void failsWhenProfileHasNoPublicNoArgConstructor() throws Exception {
        Map<String, String> arguments = arguments(writeConfig(), temporaryDirectory.resolve("x.html"), NoDefaultConstructorProfile.class);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> PstGenerationLauncher.generate(arguments));
        assertTrue(error.getMessage().contains("public no-arg constructor"));
    }

    @Test
    void failsWhenConfigFileDoesNotExist() {
        Path missing = temporaryDirectory.resolve("missing.yaml");
        Map<String, String> arguments = arguments(missing, temporaryDirectory.resolve("x.html"), TestProfile.class);
        PstConfigurationException error = assertThrows(PstConfigurationException.class,
                () -> PstGenerationLauncher.generate(arguments));
        assertTrue(error.getMessage().contains(missing.toString()));
    }

    @Test
    void failsWhenArgumentIsMissing() throws Exception {
        Map<String, String> arguments = arguments(writeConfig(), temporaryDirectory.resolve("x.html"), TestProfile.class);
        arguments.remove(PstGenerationLauncher.API_PACKAGE);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> PstGenerationLauncher.generate(arguments));
        assertTrue(error.getMessage().contains(PstGenerationLauncher.API_PACKAGE));
    }

    private Map<String, String> arguments(Path config, Path output, Class<?> profile) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(PstGenerationLauncher.CODEGEN_ID, "fixture-codegen");
        arguments.put(PstGenerationLauncher.OPENAPI_LOCATION, specLocation());
        arguments.put(PstGenerationLauncher.API_PACKAGE, PstFixtureApi.class.getPackageName());
        arguments.put(PstGenerationLauncher.MODEL_PACKAGE, PstFixturePayload.class.getPackageName());
        arguments.put(PstGenerationLauncher.CONFIG_PATH, config.toString());
        arguments.put(PstGenerationLauncher.OUTPUT_PATH, output.toString());
        arguments.put(PstGenerationLauncher.TITLE, "PST fixture title");
        arguments.put(PstGenerationLauncher.FUZZING_PROFILE, profile.getName());
        return arguments;
    }

    private String specLocation() {
        try {
            return Path.of(getClass().getResource("/openapi/pst-equivalence.yaml").toURI()).toString();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private Path writeConfig() throws Exception {
        StringBuilder yaml = new StringBuilder("successStatus: 200\nscenarioStatus:\n");
        for (FuzzScenario scenario : FuzzScenario.values()) {
            yaml.append("  ").append(scenario).append(": 400\n");
        }
        Path file = temporaryDirectory.resolve("pst.yaml");
        Files.writeString(file, yaml);
        return file;
    }

    public static class TestProfile implements FuzzingProfile {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final FuzzCasePlanner payload = new FuzzCasePlanner(List.of(new NullAndMissingRule(), new ScalarRule()));
        private final FuzzCasePlanner path = new FuzzCasePlanner(List.of(new ScalarRule()));

        @Override
        public ObjectMapper objectMapper() {
            return objectMapper;
        }

        @Override
        public FuzzCasePlanner payloadPlanner() {
            return payload;
        }

        @Override
        public FuzzCasePlanner pathParamsPlanner() {
            return path;
        }
    }

    public static class CountingProfile extends TestProfile {
        static int calls;

        @Override
        public ObjectMapper objectMapper() {
            calls++;
            return super.objectMapper();
        }

        @Override
        public FuzzCasePlanner payloadPlanner() {
            calls++;
            return super.payloadPlanner();
        }

        @Override
        public FuzzCasePlanner pathParamsPlanner() {
            calls++;
            return super.pathParamsPlanner();
        }
    }

    public static class NoDefaultConstructorProfile extends TestProfile {
        public NoDefaultConstructorProfile(String ignored) {
        }
    }
}
