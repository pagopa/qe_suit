package it.pagopa.interop.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "it.pagopa.interop",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArchitectureRulesTest {

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
}