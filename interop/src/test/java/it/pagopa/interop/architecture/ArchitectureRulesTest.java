package it.pagopa.interop.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
}