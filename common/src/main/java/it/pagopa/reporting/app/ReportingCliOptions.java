package it.pagopa.reporting.app;

import java.nio.file.Files;
import java.nio.file.Path;

record ReportingCliOptions(
        Path inputPath,
        Path outputPath,
        boolean includeDtoDump
) {
    static ReportingCliOptions parse(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException(helpMessage("Missing required arguments"));
        }

        Path input = null;
        Path output = null;
        boolean includeDtoDump = true;

        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            switch (current) {
                case "--input", "-i" -> {
                    i = ensureValue(args, i, current);
                    input = Path.of(args[i]);
                }
                case "--output", "-o" -> {
                    i = ensureValue(args, i, current);
                    output = Path.of(args[i]);
                }
                case "--no-dto-dump" -> includeDtoDump = false;
                case "--help", "-h" -> throw new IllegalArgumentException(helpMessage(null));
                default -> throw new IllegalArgumentException(helpMessage("Unknown option: " + current));
            }
        }

        if (input == null) {
            throw new IllegalArgumentException(helpMessage("--input is required"));
        }

        if (output == null) {
            output = defaultOutputPath(input);
        }

        return new ReportingCliOptions(input.toAbsolutePath().normalize(), output.toAbsolutePath().normalize(), includeDtoDump);
    }

    private static int ensureValue(String[] args, int index, String option) {
        if (index + 1 >= args.length) {
            throw new IllegalArgumentException(helpMessage("Missing value for option: " + option));
        }
        return index + 1;
    }

    private static Path defaultOutputPath(Path input) {
        Path normalized = input.toAbsolutePath().normalize();
        if (Files.isDirectory(normalized)) {
            return normalized.resolve("surefire-html-report.html");
        }

        String fileName = normalized.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String baseName = dot > 0 ? fileName.substring(0, dot) : fileName;
        Path parent = normalized.getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        return parent.resolve(baseName + ".html");
    }

    static String helpMessage(String error) {
        StringBuilder builder = new StringBuilder();
        if (error != null && !error.isBlank()) {
            builder.append(error).append("\n\n");
        }

        builder.append("Usage:\n")
                .append("  java it.pagopa.reporting.app.ReportingMain --input <surefire-xml-or-directory> [--output <html-file>] [--no-dto-dump]\n\n")
                .append("Options:\n")
                .append("  -i, --input        Required. Input Surefire XML file or directory containing TEST-*.xml files\n")
                .append("  -o, --output       Optional. Output HTML file path\n")
                .append("      --no-dto-dump  Optional. Disable annotation-driven DTO recursive panel\n")
                .append("  -h, --help         Show this help\n");

        return builder.toString();
    }
}

