package it.pagopa.pst.maven;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pure resolution of the file-system parameters of the {@code pst:generate} goal.
 */
public final class PstMojoParameters {

    static final String REPORT_FILE_NAME = "pst-report.html";

    private PstMojoParameters() {
    }

    public static Path resolveConfig(File basedir, File config) throws MojoFailureException {
        if (config == null || config.getPath().isBlank()) {
            throw new MojoFailureException(
                    "Parameter pst.config is required: set it to the PST YAML configuration file.");
        }
        Path path = resolve(basedir, config);
        if (!Files.isRegularFile(path)) {
            throw new MojoFailureException("PST configuration file (pst.config) does not exist: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new MojoFailureException("PST configuration file (pst.config) is not readable: " + path);
        }
        return path;
    }

    /**
     * Default: {@code <build directory>/pst/<openapi execution id>/pst-report.html}, so different codegen executions
     * never overwrite each other.
     */
    public static Path resolveOutput(File basedir, File buildDirectory, String executionId, File output) {
        if (output != null && !output.getPath().isBlank()) {
            return resolve(basedir, output);
        }
        return buildDirectory.toPath().resolve("pst").resolve(executionId).resolve(REPORT_FILE_NAME).normalize();
    }

    public static String requireFuzzingProfile(String fuzzingProfile) throws MojoFailureException {
        if (fuzzingProfile == null || fuzzingProfile.isBlank()) {
            throw new MojoFailureException(
                    "Parameter pst.fuzzingProfile is required: set it to the fully qualified name of the "
                            + "it.pagopa.infrastructure.fuzzing.FuzzingProfile used by the runtime contract tests.");
        }
        return fuzzingProfile.trim();
    }

    private static Path resolve(File basedir, File file) {
        Path path = file.toPath();
        if (!path.isAbsolute() && basedir != null) {
            path = basedir.toPath().resolve(path);
        }
        return path.toAbsolutePath().normalize();
    }
}
