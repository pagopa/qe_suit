package it.pagopa.pst.maven;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiGeneratorExecutionResolverTest {

    private static final String URL_SPEC = "https://example.org/specs/api.yml";

    @TempDir
    Path basedir;

    private final OpenApiGeneratorExecutionResolver resolver = new OpenApiGeneratorExecutionResolver();

    @Test
    void resolvesSingleMatchingExecutionFromConfigOptions() throws Exception {
        MavenProject project = project(generator(
                execution("generate-a-client", URL_SPEC, null, null, Map.of("apiPackage", "a.api", "modelPackage", "a.model")),
                execution("generate-b-client", "https://example.org/b.yml", null, null,
                        Map.of("apiPackage", "b.api", "modelPackage", "b.model"))
        ));

        ResolvedOpenApiExecution resolved = resolver.resolve(project, "generate-b-client");

        assertEquals("generate-b-client", resolved.executionId());
        assertEquals("https://example.org/b.yml", resolved.openApiLocation());
        assertEquals("b.api", resolved.apiPackage());
        assertEquals("b.model", resolved.modelPackage());
    }

    @Test
    void resolvesTopLevelPackagesAndTrimsInterpolatedWhitespace() throws Exception {
        MavenProject project = project(generator(
                execution("gen", "\n   " + URL_SPEC + "\n   ", " x.api ", " x.model ", Map.of())
        ));

        ResolvedOpenApiExecution resolved = resolver.resolve(project, " gen ");

        assertEquals(URL_SPEC, resolved.openApiLocation());
        assertEquals("x.api", resolved.apiPackage());
        assertEquals("x.model", resolved.modelPackage());
    }

    @Test
    void resolvesRelativeLocalSpecAgainstBasedir() throws Exception {
        Files.createDirectories(basedir.resolve("specs"));
        Files.writeString(basedir.resolve("specs/api.yaml"), "openapi: 3.0.3");
        MavenProject project = project(generator(
                execution("gen", "specs/api.yaml", "p.api", "p.model", Map.of())
        ));

        ResolvedOpenApiExecution resolved = resolver.resolve(project, "gen");

        assertEquals(basedir.resolve("specs/api.yaml").normalize().toString(), resolved.openApiLocation());
    }

    @Test
    void fallsBackToPluginLevelConfiguration() throws Exception {
        Plugin plugin = generator(execution("gen", null, null, null, Map.of()));
        plugin.setConfiguration(config(URL_SPEC, "p.api", "p.model", Map.of()));

        ResolvedOpenApiExecution resolved = resolver.resolve(project(plugin), "gen");

        assertEquals(URL_SPEC, resolved.openApiLocation());
        assertEquals("p.api", resolved.apiPackage());
    }

    @Test
    void failsWhenExecutionParameterIsMissing() {
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> resolver.resolve(project(generator(execution("gen", URL_SPEC, "a", "b", Map.of()))), "  "));
        assertTrue(error.getMessage().contains("pst.openapiExecution is required"));
    }

    @Test
    void failsWhenNoExecutionMatches() {
        MavenProject project = project(generator(execution("generate-a-client", URL_SPEC, "a", "b", Map.of())));
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> resolver.resolve(project, "generate-m2m-client"));
        assertTrue(error.getMessage().contains("generate-m2m-client"));
        assertTrue(error.getMessage().contains("[generate-a-client]"));
    }

    @Test
    void failsWhenGeneratorPluginIsMissing() {
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> resolver.resolve(project(), "gen"));
        assertTrue(error.getMessage().contains("openapi-generator-maven-plugin"));
    }

    @Test
    void failsWhenSeveralExecutionsShareTheId() {
        MavenProject project = project(
                generator(execution("gen", URL_SPEC, "a", "b", Map.of())),
                generator(execution("gen", URL_SPEC, "c", "d", Map.of()))
        );
        MojoFailureException error = assertThrows(MojoFailureException.class, () -> resolver.resolve(project, "gen"));
        assertTrue(error.getMessage().contains("Ambiguous pst.openapiExecution='gen'"));
    }

    @Test
    void failsWhenPackagesAreMissing() {
        MavenProject project = project(generator(execution("gen", URL_SPEC, "a.api", null, Map.of())));
        MojoFailureException error = assertThrows(MojoFailureException.class, () -> resolver.resolve(project, "gen"));
        assertTrue(error.getMessage().contains("modelPackage"));
        assertTrue(error.getMessage().contains("'gen'"));
    }

    @Test
    void failsWhenPackagesConflict() {
        MavenProject project = project(generator(
                execution("gen", URL_SPEC, "a.api", "a.model", Map.of("apiPackage", "other.api"))
        ));
        MojoFailureException error = assertThrows(MojoFailureException.class, () -> resolver.resolve(project, "gen"));
        assertTrue(error.getMessage().contains("conflicting apiPackage"));
    }

    @Test
    void failsWhenInputSpecIsMissingOrUnresolved() {
        MojoFailureException missing = assertThrows(MojoFailureException.class, () -> resolver.resolve(
                project(generator(execution("gen", null, "a", "b", Map.of()))), "gen"));
        assertTrue(missing.getMessage().contains("no inputSpec"));

        MojoFailureException unresolved = assertThrows(MojoFailureException.class, () -> resolver.resolve(
                project(generator(execution("gen", "${m2m.openapi.url}", "a", "b", Map.of()))), "gen"));
        assertTrue(unresolved.getMessage().contains("${m2m.openapi.url}"));
    }

    @Test
    void failsWhenLocalSpecIsNotAccessible() {
        MavenProject project = project(generator(execution("gen", "specs/missing.yaml", "a", "b", Map.of())));
        MojoFailureException error = assertThrows(MojoFailureException.class, () -> resolver.resolve(project, "gen"));
        assertTrue(error.getMessage().contains("not accessible"));
        assertTrue(error.getMessage().contains("missing.yaml"));
    }

    @Test
    void failsWhenExecutionIsSkipped() {
        PluginExecution execution = execution("gen", URL_SPEC, "a", "b", Map.of());
        ((Xpp3Dom) execution.getConfiguration()).addChild(node("skip", "true"));
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> resolver.resolve(project(generator(execution)), "gen"));
        assertTrue(error.getMessage().contains("skip=true"));
    }

    private MavenProject project(Plugin... plugins) {
        Model model = new Model();
        model.setGroupId("it.example");
        model.setArtifactId("fixture");
        model.setVersion("1");
        Build build = new Build();
        for (Plugin plugin : plugins) build.addPlugin(plugin);
        model.setBuild(build);
        MavenProject project = new MavenProject(model);
        project.setFile(new File(basedir.toFile(), "pom.xml"));
        return project;
    }

    private static Plugin generator(PluginExecution... executions) {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.openapitools");
        plugin.setArtifactId("openapi-generator-maven-plugin");
        for (PluginExecution execution : executions) plugin.addExecution(execution);
        return plugin;
    }

    private static PluginExecution execution(
            String id, String inputSpec, String apiPackage, String modelPackage, Map<String, String> configOptions
    ) {
        PluginExecution execution = new PluginExecution();
        execution.setId(id);
        execution.addGoal("generate");
        execution.setConfiguration(config(inputSpec, apiPackage, modelPackage, configOptions));
        return execution;
    }

    private static Xpp3Dom config(String inputSpec, String apiPackage, String modelPackage, Map<String, String> configOptions) {
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        if (inputSpec != null) configuration.addChild(node("inputSpec", inputSpec));
        if (apiPackage != null) configuration.addChild(node("apiPackage", apiPackage));
        if (modelPackage != null) configuration.addChild(node("modelPackage", modelPackage));
        if (!configOptions.isEmpty()) {
            Xpp3Dom options = new Xpp3Dom("configOptions");
            configOptions.forEach((key, value) -> options.addChild(node(key, value)));
            configuration.addChild(options);
        }
        return configuration;
    }

    private static Xpp3Dom node(String name, String value) {
        Xpp3Dom node = new Xpp3Dom(name);
        node.setValue(value);
        return node;
    }
}
