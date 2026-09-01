package it.pagopa.infrastructure.cucumber.hook.channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.logging.Logger;

public final class ChannelFeatureGenerator {

    private static final Logger LOG =
            Logger.getLogger(ChannelFeatureGenerator.class.getName());

    private final Path sourceRoot;
    private final Path targetRoot;
    private final ChannelScenarioExpansion scenarioExpansion;

    public ChannelFeatureGenerator(
            Path sourceRoot,
            Path targetRoot,
            ChannelScenarioExpansion scenarioExpansion
    ) {
        this.sourceRoot = Objects.requireNonNull(sourceRoot);
        this.targetRoot = Objects.requireNonNull(targetRoot);
        this.scenarioExpansion = Objects.requireNonNull(scenarioExpansion);
    }

    public void generate() throws IOException {
        if (!Files.exists(sourceRoot)) {
            LOG.warning(
                    "Source feature directory does not exist: "
                            + sourceRoot
                            + " – skipping."
            );
            return;
        }

        Files.createDirectories(targetRoot);

        Files.walkFileTree(sourceRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(
                    Path dir,
                    BasicFileAttributes attrs
            ) throws IOException {
                Path relative = sourceRoot.relativize(dir);
                Files.createDirectories(targetRoot.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) throws IOException {
                if (file.getFileName().toString().endsWith(".feature")) {
                    processFeatureFile(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        LOG.info("Channel feature expansion complete: " + targetRoot);
    }

    private void processFeatureFile(Path sourceFile) throws IOException {
        String sourceText =
                Files.readString(sourceFile, StandardCharsets.UTF_8);

        String expanded;

        try {
            expanded = scenarioExpansion.expand(
                    sourceFile,
                    sourceText
            );
        } catch (Exception e) {
            LOG.warning(
                    "Skipping "
                            + sourceFile
                            + " due to expansion error: "
                            + e.getMessage()
            );

            expanded = sourceText;
        }

        Path relative = sourceRoot.relativize(sourceFile);
        Path targetFile = targetRoot.resolve(relative);

        Files.writeString(
                targetFile,
                expanded,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        LOG.info("Generated: " + targetFile);
    }
}