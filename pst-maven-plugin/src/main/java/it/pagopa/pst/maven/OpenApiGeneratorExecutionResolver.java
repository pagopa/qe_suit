package it.pagopa.pst.maven;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Selects exactly one {@code org.openapitools:openapi-generator-maven-plugin} execution, by id,
 * from the effective (already interpolated) {@link MavenProject} model and extracts the values
 * needed by the PST pipeline. The POM file is never parsed directly.
 */
public final class OpenApiGeneratorExecutionResolver {

    static final String GROUP_ID = "org.openapitools";
    static final String ARTIFACT_ID = "openapi-generator-maven-plugin";
    private static final Pattern URL_SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://.*");

    public ResolvedOpenApiExecution resolve(MavenProject project, String executionId) throws MojoFailureException {
        if (executionId == null || executionId.isBlank()) {
            throw new MojoFailureException(
                    "Parameter pst.openapiExecution is required: set it to the id of an "
                            + GROUP_ID + ":" + ARTIFACT_ID + " execution.");
        }
        String id = executionId.trim();
        List<Plugin> generatorPlugins = project.getBuildPlugins().stream()
                .filter(plugin -> GROUP_ID.equals(plugin.getGroupId()) && ARTIFACT_ID.equals(plugin.getArtifactId()))
                .toList();
        if (generatorPlugins.isEmpty()) {
            throw new MojoFailureException(
                    "No " + GROUP_ID + ":" + ARTIFACT_ID + " plugin declared in project " + project.getId()
                            + " (requested pst.openapiExecution=" + id + ").");
        }

        List<Match> matches = new ArrayList<>();
        List<String> available = new ArrayList<>();
        for (Plugin plugin : generatorPlugins) {
            for (PluginExecution execution : plugin.getExecutions()) {
                available.add(execution.getId());
                if (id.equals(execution.getId())) {
                    matches.add(new Match(plugin, execution));
                }
            }
        }
        if (matches.isEmpty()) {
            throw new MojoFailureException(
                    "No " + ARTIFACT_ID + " execution with id '" + id + "' (pst.openapiExecution). Available executions: "
                            + available);
        }
        if (matches.size() > 1) {
            throw new MojoFailureException(
                    "Ambiguous pst.openapiExecution='" + id + "': " + matches.size() + " "
                            + ARTIFACT_ID + " executions share this id.");
        }
        return extract(project.getBasedir(), matches.get(0));
    }

    private ResolvedOpenApiExecution extract(File basedir, Match match) throws MojoFailureException {
        String id = match.execution().getId();
        Xpp3Dom executionConfig = asDom(match.execution().getConfiguration());
        Xpp3Dom pluginConfig = asDom(match.plugin().getConfiguration());

        String skip = value(executionConfig, pluginConfig, "skip");
        if (skip != null && Boolean.parseBoolean(skip.trim())) {
            throw new MojoFailureException("Execution '" + id + "' of " + ARTIFACT_ID + " is configured with skip=true.");
        }

        String inputSpec = value(executionConfig, pluginConfig, "inputSpec");
        if (inputSpec == null || inputSpec.isBlank()) {
            throw new MojoFailureException("Execution '" + id + "' of " + ARTIFACT_ID + " has no inputSpec.");
        }
        if (inputSpec.contains("${")) {
            throw new MojoFailureException(
                    "Execution '" + id + "' has an unresolved inputSpec '" + inputSpec.trim()
                            + "': the referenced property is not defined.");
        }
        String location = resolveLocation(basedir, id, inputSpec.trim());
        String apiPackage = packageValue(executionConfig, pluginConfig, "apiPackage", id);
        String modelPackage = packageValue(executionConfig, pluginConfig, "modelPackage", id);
        return new ResolvedOpenApiExecution(id, location, apiPackage, modelPackage);
    }

    private String resolveLocation(File basedir, String id, String inputSpec) throws MojoFailureException {
        if (URL_SCHEME.matcher(inputSpec).matches() && !inputSpec.toLowerCase(Locale.ROOT).startsWith("file:")) {
            return inputSpec;
        }
        Path path;
        if (inputSpec.toLowerCase(Locale.ROOT).startsWith("file:")) {
            try {
                path = Path.of(java.net.URI.create(inputSpec));
            } catch (IllegalArgumentException exception) {
                throw new MojoFailureException("Execution '" + id + "' has an invalid inputSpec URI: " + inputSpec);
            }
        } else {
            path = Path.of(inputSpec);
            if (!path.isAbsolute() && basedir != null) {
                path = basedir.toPath().resolve(path);
            }
        }
        path = path.normalize();
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new MojoFailureException(
                    "OpenAPI spec of execution '" + id + "' is not accessible: " + path + " (inputSpec=" + inputSpec + ")");
        }
        return path.toString();
    }

    private String packageValue(Xpp3Dom executionConfig, Xpp3Dom pluginConfig, String name, String id)
            throws MojoFailureException {
        String topLevel = trimmed(value(executionConfig, pluginConfig, name));
        String configOption = trimmed(value(
                child(executionConfig, "configOptions"),
                child(pluginConfig, "configOptions"),
                name
        ));
        if (topLevel != null && configOption != null && !topLevel.equals(configOption)) {
            throw new MojoFailureException(
                    "Execution '" + id + "' declares conflicting " + name + " values: <" + name + ">" + topLevel
                            + " and <configOptions><" + name + ">" + configOption + ".");
        }
        String resolved = topLevel != null ? topLevel : configOption;
        if (resolved == null) {
            throw new MojoFailureException(
                    "Execution '" + id + "' of " + ARTIFACT_ID + " does not declare " + name
                            + " (neither <" + name + "> nor <configOptions><" + name + ">).");
        }
        return resolved;
    }

    /**
     * Execution configuration first; plugin-level configuration as fallback, mirroring Maven's merge.
     */
    private static String value(Xpp3Dom primary, Xpp3Dom fallback, String name) {
        Xpp3Dom node = child(primary, name);
        if (node == null) node = child(fallback, name);
        return node == null ? null : node.getValue();
    }

    private static Xpp3Dom child(Xpp3Dom parent, String name) {
        return parent == null ? null : parent.getChild(name);
    }

    private static String trimmed(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static Xpp3Dom asDom(Object configuration) {
        return configuration instanceof Xpp3Dom dom ? dom : null;
    }

    private record Match(Plugin plugin, PluginExecution execution) {
    }
}
