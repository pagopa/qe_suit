package it.pagopa.infrastructure.logging;

import java.nio.file.Files;
import java.nio.file.Path;

public final class TestLogGrouperMain {

    private TestLogGrouperMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Usage: TestLogGrouperMain <source-log> <grouped-log>"
            );
            System.exit(1);
        }

        Path source = Path.of(args[0]);
        Path target = Path.of(args[1]);

        if (!Files.isRegularFile(source)) {
            System.out.printf(
                    "No log file to group: %s%n",
                    source
            );
            return;
        }

        int groups = TestLogGrouper.groupFile(source, target);

        System.out.printf(
                "Grouped log written to: %s%n",
                target
        );

        System.out.printf(
                "Test case groups found: %d%n",
                groups
        );
    }
}