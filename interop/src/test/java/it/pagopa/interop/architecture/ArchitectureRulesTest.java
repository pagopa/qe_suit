package it.pagopa.interop.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.fail;

@AnalyzeClasses(
        packages = "it.pagopa.interop",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArchitectureRulesTest {

    private static final List<String> ROOT_PACKAGES = List.of("common", "bff", "web");
    private static final List<String> ALLOWED_VERTICAL_PACKAGES = List.of("application", "domain", "infrastructure");
    private static final List<String> ALLOWED_GLOBAL_PACKAGES = List.of("infrastructure", "kernel");

    private static final Set<String> COMMON_ALLOWED_PATTERNS = Set.of("Gateway", "UseCase", "Config", "Steps", "Validator", "Command", "Factory", "Exception", "Journey", "JourneyModule", "JourneyImpl", "InvocationHandler");
    private static final Set<String> COMMON_EXCLUDED_SEGMENTS = Set.of("domain", "kernel", "infrastructure");

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAnyPackage("..application..")
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAnyPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..");

    @Test
    void packages_under_common_bff_web_must_follow_global_or_vertical_structure() {
        var imported = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("it.pagopa.interop");

        Set<String> invalidPackages = new TreeSet<>();

        for (JavaClass javaClass : imported) {
            String packageName = javaClass.getPackageName();

            for (String root : ROOT_PACKAGES) {
                String prefix = "it.pagopa.interop." + root + ".";
                if (!packageName.startsWith(prefix)) {
                    continue;
                }

                String remainder = packageName.substring(prefix.length());
                String[] segments = remainder.split("\\.");

                if (segments.length == 0 || segments[0].isBlank()) {
                    continue;
                }

                // Caso 1: package globale consentito (es. common.infrastructure)
                if (ALLOWED_GLOBAL_PACKAGES.contains(segments[0])) {
                    continue;
                }

                // Caso 2: verticale classica -> <verticalSlice>.<layer>
                if (segments.length < 2) {
                    invalidPackages.add(packageName);
                    continue;
                }

                String layer = segments[1];
                if (!ALLOWED_VERTICAL_PACKAGES.contains(layer)) {
                    invalidPackages.add(packageName);
                }
            }
        }

        if (!invalidPackages.isEmpty()) {
            fail("Trovati package non consentiti sotto common/bff/web: " + invalidPackages);
        }
    }

    @Test
    void channel_implementations_of_common_classes_must_have_channel_prefix() {
        var imported = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("it.pagopa.interop");

        Set<String> violations = new TreeSet<>();

        for (JavaClass javaClass : imported) {
            checkNamingConvention(javaClass, violations);
        }

        if (!violations.isEmpty()) {
            fail("Violazioni naming classi architetturali nei channel: " + violations);
        }
    }

    @Test
    void gateway_classes_must_not_depend_on_cucumber_directly_or_indirectly() {
        var imported = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("it.pagopa.interop");

        Set<String> violations = new TreeSet<>();

        for (JavaClass javaClass : imported) {
            if (!javaClass.getSimpleName().endsWith("Gateway")) {
                continue;
            }

            Optional<List<JavaClass>> path = findPathToCucumber(javaClass, new HashSet<>());
            path.ifPresent(javaClasses -> violations.add(formatPath(javaClasses)));
        }

        if (!violations.isEmpty()) {
            fail("Gateway con dipendenze dirette/indirette da Cucumber: " + violations);
        }
    }

    @Test
    void java_source_files_must_have_less_than_200_lines() throws Exception {
        Path sourceRoot = Path.of("src/main/java");
        Set<String> violations = new TreeSet<>();

        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            paths.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try (Stream<String> linesStream = Files.lines(path)) {
                            long lines = linesStream.count();
                            if (lines >= 200) {
                                violations.add(path + " (" + lines + " lines)");
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }

        if (!violations.isEmpty()) {
            fail("Classi con 200 o più righe: " + violations);
        }
    }

    @Test
    void java_test_files_must_end_with_test_suffix() throws Exception {
        Path testRoot = Path.of("src/test/java");
        Set<String> violations = new TreeSet<>();

        try (Stream<Path> paths = Files.walk(testRoot)) {
            paths.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        if ("package-info.java".equals(fileName) || "module-info.java".equals(fileName)) {
                            return;
                        }
                        if (!fileName.endsWith("Test.java")) {
                            violations.add(path.toString());
                        }
                    });
        }

        if (!violations.isEmpty()) {
            fail("I file sotto src/test/java devono terminare con 'Test': " + violations);
        }
    }

    private static void checkNamingConvention(JavaClass javaClass, Set<String> violations) {
        String packageName = javaClass.getPackageName();
        String fullName = javaClass.getFullName();
        String simpleName = javaClass.getSimpleName();

        if (!packageName.startsWith("it.pagopa.interop.")) return;
        if (packageName.startsWith("it.pagopa.interop.generated.")) return;
        if (fullName.contains("$")) return;

        String[] segments = packageName.substring("it.pagopa.interop.".length()).split("\\.");
        if (segments.length == 0) return;

        String channel = segments[0];

        if ("common".equals(channel)) {
            checkCommonNaming(fullName, simpleName, segments, violations);
        } else {
            checkChannelNaming(fullName, simpleName, channel, violations);
        }
    }

    private static void checkCommonNaming(String fullName, String simpleName, String[] segments, Set<String> violations) {
        boolean isExcluded = COMMON_EXCLUDED_SEGMENTS.stream().anyMatch(s -> isInSegments(segments, s));
        if (isExcluded) return;

        boolean hasValidSuffix = COMMON_ALLOWED_PATTERNS.stream().anyMatch(simpleName::endsWith);
        if (!hasValidSuffix) {
            violations.add(fullName + " (expected suffix one of: " + COMMON_ALLOWED_PATTERNS + ")");
        }
    }

    private static void checkChannelNaming(String fullName, String simpleName, String channel, Set<String> violations) {
        boolean isCorePattern = COMMON_ALLOWED_PATTERNS.stream().anyMatch(simpleName::endsWith);
        if (!isCorePattern) return;

        String expectedPrefix = capitalize(channel);
        if (!simpleName.startsWith(expectedPrefix)) {
            violations.add(fullName + " (expected prefix: " + expectedPrefix + ")");
        }
    }

    private static boolean isInSegments(String[] segments, String target) {
        for (String s : segments) {
            if (target.equals(s)) return true;
        }
        return false;
    }

    private static String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static Optional<List<JavaClass>> findPathToCucumber(JavaClass sourceClass, Set<String> visited) {
        if (isCucumberClass(sourceClass)) {
            return Optional.of(List.of(sourceClass));
        }

        if (!visited.add(sourceClass.getFullName())) {
            return Optional.empty();
        }

        for (var dependency : sourceClass.getDirectDependenciesFromSelf()) {
            JavaClass targetClass = dependency.getTargetClass();

            if (isCucumberClass(targetClass)) {
                return Optional.of(List.of(sourceClass, targetClass));
            }

            if (!targetClass.getPackageName().startsWith("it.pagopa.interop.")) {
                continue;
            }

            Optional<List<JavaClass>> tailPath = findPathToCucumber(targetClass, visited);
            if (tailPath.isPresent()) {
                List<JavaClass> fullPath = new ArrayList<>();
                fullPath.add(sourceClass);
                fullPath.addAll(tailPath.get());
                return Optional.of(fullPath);
            }
        }

        return Optional.empty();
    }

    private static boolean isCucumberClass(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        return packageName.startsWith("io.cucumber.")
                || packageName.contains(".cucumber.");
    }

    private static String formatPath(List<JavaClass> classes) {
        return classes.stream()
                .map(JavaClass::getFullName)
                .reduce((left, right) -> left + " -> " + right)
                .orElse("<empty-path>");
    }
}