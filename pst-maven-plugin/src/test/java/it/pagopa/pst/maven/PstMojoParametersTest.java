package it.pagopa.pst.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PstMojoParametersTest {

    @TempDir
    Path basedir;

    @Test
    void resolvesRelativeConfigAgainstBasedir() throws Exception {
        Files.createDirectories(basedir.resolve("src/test/resources/pst"));
        Path config = Files.writeString(basedir.resolve("src/test/resources/pst/api.yaml"), "successStatus: 200");

        assertEquals(config.toAbsolutePath().normalize(),
                PstMojoParameters.resolveConfig(basedir.toFile(), new File("src/test/resources/pst/api.yaml")));
    }

    @Test
    void failsWhenConfigIsMissing() {
        MojoFailureException notSet = assertThrows(MojoFailureException.class,
                () -> PstMojoParameters.resolveConfig(basedir.toFile(), null));
        assertTrue(notSet.getMessage().contains("pst.config is required"));

        MojoFailureException notFound = assertThrows(MojoFailureException.class,
                () -> PstMojoParameters.resolveConfig(basedir.toFile(), new File("missing/pst.yaml")));
        assertTrue(notFound.getMessage().contains("pst.config"));
        assertTrue(notFound.getMessage().contains("missing" + File.separator + "pst.yaml"));
    }

    @Test
    void failsWhenConfigIsADirectory() {
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> PstMojoParameters.resolveConfig(basedir.toFile(), basedir.toFile()));
        assertTrue(error.getMessage().contains("does not exist"));
    }

    @Test
    void defaultOutputIsScopedByExecutionId() {
        File target = basedir.resolve("target").toFile();

        assertEquals(basedir.resolve("target/pst/generate-a-client/pst-report.html"),
                PstMojoParameters.resolveOutput(basedir.toFile(), target, "generate-a-client", null));
        assertEquals(basedir.resolve("target/pst/generate-b-client/pst-report.html"),
                PstMojoParameters.resolveOutput(basedir.toFile(), target, "generate-b-client", new File(" ")));
    }

    @Test
    void customOutputOverridesDefault() {
        File target = basedir.resolve("target").toFile();

        assertEquals(basedir.resolve("reports/custom.html"),
                PstMojoParameters.resolveOutput(basedir.toFile(), target, "gen", new File("reports/custom.html")));
        Path absolute = basedir.resolve("elsewhere/x.html");
        assertEquals(absolute,
                PstMojoParameters.resolveOutput(basedir.toFile(), target, "gen", absolute.toFile()));
    }

    @Test
    void fuzzingProfileIsRequired() throws Exception {
        MojoFailureException error = assertThrows(MojoFailureException.class,
                () -> PstMojoParameters.requireFuzzingProfile(" "));
        assertTrue(error.getMessage().contains("pst.fuzzingProfile is required"));
        assertEquals("a.B", PstMojoParameters.requireFuzzingProfile(" a.B "));
    }
}
