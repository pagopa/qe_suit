package it.pagopa.infrastructure.reporting.contract.resolver;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.infrastructure.reporting.contract.openapi.OpenApiContractTargetResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContractTargetRegressionTest {

    private static final Path SOURCE_ROOT = Path.of("src/test/java");
    private static final String CONTRACT_FIXTURES_PACKAGE = "it.pagopa.infrastructure.reporting.contract.resolver.fixtures";

    @Test
    void allApiContractFactoriesResolveToExactlyOneOpenApiOperation() throws Exception {
        String spec = Path.of("src/test/resources/reporting/openapi/all-api-contracts.yaml").toAbsolutePath().toString();
        ContractChannelConfig bff = new ContractChannelConfig("bff", "BFF", "Bff", ContractTargetType.OPENAPI, spec);
        OpenApiContractTargetResolver resolver = new OpenApiContractTargetResolver();

        List<Class<?>> contractClasses = contractClasses().stream()
                .filter(clazz -> clazz.getSimpleName().startsWith("Bff"))
                .toList();
        assertFalse(contractClasses.isEmpty());
        for (Class<?> contractClass : contractClasses) {
            for (Method method : testFactories(contractClass)) {
                String target = resolver.resolveTarget(new ContractFactoryContext(bff, contractClass.getName(), method.getName()));
                assertNotNull(target);
            }
        }
    }

    @Test
    void allPageContractFactoriesResolveToExactlyOnePage() throws Exception {
        ContractChannelConfig web = new ContractChannelConfig("web", "WEB", "Web", ContractTargetType.PAGE, null);
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        List<Class<?>> contractClasses = contractClasses().stream()
                .filter(clazz -> clazz.getSimpleName().startsWith("Web"))
                .toList();
        assertFalse(contractClasses.isEmpty());
        for (Class<?> contractClass : contractClasses) {
            for (Method method : testFactories(contractClass)) {
                String target = resolver.resolveTarget(new ContractFactoryContext(web, contractClass.getName(), method.getName()));
                assertNotNull(target);
            }
        }
    }

    private List<Class<?>> contractClasses() throws Exception {
        try (Stream<Path> stream = Files.walk(SOURCE_ROOT.resolve(CONTRACT_FIXTURES_PACKAGE.replace('.', '/')))) {
            return stream.filter(path -> path.toString().endsWith("ContractTest.java"))
                    .map(this::toClassName)
                    .map(this::loadClass)
                    .collect(Collectors.toList());
        }
    }

    private String toClassName(Path path) {
        String relative = SOURCE_ROOT.relativize(path).toString().replace('\\', '/');
        return relative.replace(".java", "").replace('/', '.');
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot load class " + className, ex);
        }
    }

    private List<Method> testFactories(Class<?> contractClass) {
        return Stream.of(contractClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestFactory.class))
                .toList();
    }
}
