package it.pagopa.pst.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates the PST HTML report for one {@code openapi-generator-maven-plugin} execution.
 * <p>
 * {@link Execute @Execute(phase = COMPILE)} forks the lifecycle up to {@code compile} in the same Maven process,
 * so codegen, source patches and compilation always run before the report is generated, also after {@code clean}.
 * Normal usage is the direct goal ({@code mvn pst:generate ...}); {@code mvn compile pst:generate} works too but
 * compiles twice because of the fork. The goal is not bound to any lifecycle phase.
 */
@Mojo(
        name = "generate",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        threadSafe = true
)
@Execute(phase = LifecyclePhase.COMPILE)
public class PstGenerateMojo extends AbstractMojo {

    /**
     * Id of the {@code org.openapitools:openapi-generator-maven-plugin} execution to design.
     */
    @Parameter(property = "pst.openapiExecution")
    private String openapiExecution;

    /**
     * PST YAML configuration file (relative paths are resolved against the project base directory).
     */
    @Parameter(property = "pst.config")
    private File config;

    /**
     * Fully qualified name of the {@code FuzzingProfile} shared with the runtime contract tests.
     */
    @Parameter(property = "pst.fuzzingProfile")
    private String fuzzingProfile;

    /**
     * Report path. Default: {@code ${project.build.directory}/pst/<openapiExecution>/pst-report.html}.
     */
    @Parameter(property = "pst.output")
    private File output;

    @Parameter(property = "pst.title", defaultValue = "Progettazione Scenari di Test")
    private String title;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ResolvedOpenApiExecution execution = new OpenApiGeneratorExecutionResolver().resolve(project, openapiExecution);
        Path configPath = PstMojoParameters.resolveConfig(project.getBasedir(), config);
        String profile = PstMojoParameters.requireFuzzingProfile(fuzzingProfile);
        Path outputPath = PstMojoParameters.resolveOutput(
                project.getBasedir(),
                new File(project.getBuild().getDirectory()),
                execution.executionId(),
                output
        );

        getLog().info("PST OpenAPI Generator execution: " + execution.executionId());
        getLog().info("PST OpenAPI spec: " + execution.openApiLocation());
        getLog().info("PST packages: api=" + execution.apiPackage() + ", model=" + execution.modelPackage());
        getLog().info("PST configuration: " + configPath);
        getLog().info("PST fuzzing profile: " + profile);

        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("codegenId", execution.executionId());
        arguments.put("openApiLocation", execution.openApiLocation());
        arguments.put("apiPackage", execution.apiPackage());
        arguments.put("modelPackage", execution.modelPackage());
        arguments.put("configPath", configPath.toString());
        arguments.put("outputPath", outputPath.toString());
        arguments.put("title", title == null || title.isBlank() ? "Progettazione Scenari di Test" : title);
        arguments.put("fuzzingProfile", profile);

        Map<?, ?> result;
        try (URLClassLoader classLoader = ProjectClassLoaderFactory.create(project)) {
            try {
                result = PstLauncherInvoker.invoke(classLoader, arguments);
            } finally {
                stopProjectLogging(classLoader);
            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Cannot close PST project class loader", exception);
        }

        getLog().info("PST operations designed: " + result.get("operationCount"));
        getLog().info("PST scenarios designed: " + result.get("scenarioCount"));
        getLog().info("PST report generated: " + result.get("reportPath"));
    }

    /**
     * Stops the Logback context loaded in the project class loader and deregisters its JVM
     * shutdown hook: the hook loads classes lazily and would fail once the loader is closed.
     */
    private static void stopProjectLogging(ClassLoader classLoader) {
        try {
            Object factory = Class.forName("org.slf4j.LoggerFactory", true, classLoader)
                    .getMethod("getILoggerFactory").invoke(null);
            Class<?> contextBase = Class.forName("ch.qos.logback.core.ContextBase", true, classLoader);
            if (!contextBase.isInstance(factory)) {
                return;
            }
            String hookKey = (String) Class.forName("ch.qos.logback.core.CoreConstants", true, classLoader)
                    .getField("SHUTDOWN_HOOK_THREAD").get(null);
            if (contextBase.getMethod("getObject", String.class).invoke(factory, hookKey) instanceof Thread hook) {
                Runtime.getRuntime().removeShutdownHook(hook);
            }
            contextBase.getMethod("stop").invoke(factory);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Best-effort: logging shutdown must never fail the goal.
        }
    }
}


