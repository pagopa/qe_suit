package it.pagopa.interop.common.infrastructure.reporting.contract.resolver;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.interop.common.infrastructure.reporting.contract.openapi.OpenApiContractTargetResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContractTargetRegressionTest {

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
        PageContractTargetResolver resolver = new PageContractTargetResolver(Path.of("src/test/java"));
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
        Path root = Path.of("src/test/java/it/pagopa/interop/suite/contract");
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(path -> path.toString().endsWith("ContractTest.java"))
                    .map(this::toClassName)
                    .map(this::loadClass)
                    .toList();
        }
    }

    private String toClassName(Path path) {
        String normalized = path.toString().replace('\\', '/');
        String classPath = normalized.substring(normalized.indexOf("it/pagopa/interop")).replace(".java", "");
        return classPath.replace('/', '.');
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
