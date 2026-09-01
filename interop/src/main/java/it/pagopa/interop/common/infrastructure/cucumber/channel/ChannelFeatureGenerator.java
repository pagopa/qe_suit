package it.pagopa.interop.common.infrastructure.cucumber.channel;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

/**
 * Walks the source feature directory, expands each {@code .feature} file via
 * {@link ChannelScenarioExpander}, and writes the result to the output directory
 * preserving the relative path structure.
 * <p>
 * Intended to run as a standalone main class during the Maven
 * {@code generate-test-resources} lifecycle phase.
 */
public final class ChannelFeatureGenerator {

    private static final Logger LOG = Logger.getLogger(ChannelFeatureGenerator.class.getName());

    private final Path sourceRoot;
    private final Path targetRoot;

    public ChannelFeatureGenerator(Path sourceRoot, Path targetRoot) {
        this.sourceRoot = sourceRoot;
        this.targetRoot = targetRoot;
    }

    /** Entry point for Maven exec plugin invocation. */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: ChannelFeatureGenerator <sourceRoot> <targetRoot>");
        }
        Path source = Path.of(args[0]);
        Path target = Path.of(args[1]);
        new ChannelFeatureGenerator(source, target).generate();
    }

    /**
     * Performs the full generation: reads from {@code sourceRoot}, writes to {@code targetRoot}.
     */
    public void generate() throws IOException {
        if (!Files.exists(sourceRoot)) {
            LOG.warning("Source feature directory does not exist: " + sourceRoot + " – skipping.");
            return;
        }

        Files.createDirectories(targetRoot);

        Files.walkFileTree(sourceRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Path relative = sourceRoot.relativize(dir);
                Files.createDirectories(targetRoot.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!file.getFileName().toString().endsWith(".feature")) {
                    return FileVisitResult.CONTINUE;
                }
                processFeatureFile(file);
                return FileVisitResult.CONTINUE;
            }
        });

        LOG.info("Channel feature expansion complete: " + targetRoot);
    }

    private void processFeatureFile(Path sourceFile) throws IOException {
        String sourceText = Files.readString(sourceFile, StandardCharsets.UTF_8);
        String expanded;
        try {
            expanded = ChannelScenarioExpander.expand(sourceFile, sourceText);
        } catch (Exception e) {
            LOG.warning("Skipping " + sourceFile + " due to expansion error: " + e.getMessage());
            expanded = sourceText; // fallback: copy as-is
        }

        Path relative = sourceRoot.relativize(sourceFile);
        Path targetFile = targetRoot.resolve(relative);
        Files.writeString(targetFile, expanded, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        LOG.info("Generated: " + targetFile);
    }
}
