package it.pagopa.infrastructure.reporting.contract.validation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractReportConfigurationValidator;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractChannelResolver;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ContractReportDefinitionValidator {

    private final ContractTargetResolverRegistry registry;

    public ContractReportDefinitionValidator(ContractTargetResolverRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    public List<String> validate(Path sourceRoot, ContractReportConfig configuration)
            throws IOException {
        new ContractReportConfigurationValidator().validate(configuration);
        if (!Files.isDirectory(sourceRoot)) {
            throw new IllegalArgumentException("Test source directory not found: " + sourceRoot);
        }
        var parser = new JavaParser(new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17));
        var channelResolver = new ContractChannelResolver(configuration);
        List<String> violations = new ArrayList<>();
        List<Path> files;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            files = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("ContractTest.java"))
                    .sorted().toList();
        }
        if (files.isEmpty()) {
            violations.add("No *ContractTest.java source files found under " + sourceRoot);
        }
        for (Path file : files) {
            var result = parser.parse(file);
            if (!result.isSuccessful() || result.getResult().isEmpty()) {
                violations.add(file + " | Cannot parse source: " + result.getProblems());
                continue;
            }
            for (var type : result.getResult().orElseThrow()
                    .findAll(ClassOrInterfaceDeclaration.class)) {
                if (type.isInterface() || !type.getNameAsString().endsWith("ContractTest")) {
                    continue;
                }
                String className = type.getFullyQualifiedName().orElse(type.getNameAsString());
                for (var factory : type.getMethods()) {
                    if (isTestFactory(factory)) {
                        validateFactory(className, type.getNameAsString(),
                                factory.getNameAsString(), channelResolver, violations);
                    }
                }
            }
        }
        return List.copyOf(violations);
    }

    private void validateFactory(String className, String simpleClassName, String methodName,
                                 ContractChannelResolver channelResolver, List<String> violations) {
        try {
            var channel = channelResolver.resolveByClassName(simpleClassName)
                    .orElseThrow(() -> new IllegalStateException(
                            "No reporting channel configured for class " + simpleClassName));
            var context = new ContractFactoryContext(channel.config(), className, methodName);
            String target = registry.get(channel.config().targetType()).resolveTarget(context);
            if (target == null || target.isBlank()) {
                throw new IllegalStateException("Reporting resolver returned an empty target");
            }
        } catch (RuntimeException exception) {
            violations.add(className + "#" + methodName + " | "
                    + exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
    }

    private boolean isTestFactory(MethodDeclaration method) {
        return method.getAnnotations().stream().anyMatch(annotation -> {
            String name = annotation.getNameAsString();
            return "TestFactory".equals(name)
                    || "org.junit.jupiter.api.TestFactory".equals(name);
        });
    }
}
