package it.pagopa.interop.common.infrastructure.reporting.contract.resolver;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PageContractTargetResolver implements ContractTargetResolver {

    private final Path sourceRoot;

    public PageContractTargetResolver() {
        this(Path.of("src/test/java"));
    }

    public PageContractTargetResolver(Path sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    @Override
    public String resolveTarget(ContractFactoryContext context) {
        Path sourcePath = sourceRoot.resolve(context.contractClassName().replace('.', '/') + ".java");
        if (!Files.exists(sourcePath)) {
            throw new IllegalStateException("Source file not found for PAGE resolver: " + sourcePath);
        }
        try {
            CompilationUnit unit = StaticJavaParser.parse(sourcePath);
            MethodDeclaration method = findFactoryMethod(unit, context.methodName());
            List<MethodCallExpr> onCalls = method.findAll(MethodCallExpr.class, call -> "on".equals(call.getNameAsString()));
            if (onCalls.isEmpty()) {
                throw new IllegalStateException("No .on(...) call found in " + context.contractClassName() + "#" + context.methodName());
            }
            if (onCalls.size() > 1) {
                throw new IllegalStateException("Multiple .on(...) calls found in " + context.contractClassName() + "#" + context.methodName());
            }
            Expression argument = onCalls.get(0).getArgument(0);
            if (!(argument instanceof ClassExpr classExpr)) {
                throw new IllegalStateException(".on(...) argument is not a class literal in " + context.contractClassName() + "#" + context.methodName());
            }
            return classExpr.getType().asString();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot parse Java source file: " + sourcePath, ex);
        }
    }

    private MethodDeclaration findFactoryMethod(CompilationUnit unit, String methodName) {
        List<MethodDeclaration> candidates = new ArrayList<>();
        unit.findAll(MethodDeclaration.class).forEach(method -> {
            boolean sameName = method.getNameAsString().equals(methodName);
            boolean isFactory = method.getAnnotations().stream().anyMatch(a -> "TestFactory".equals(a.getNameAsString()));
            if (sameName && isFactory) {
                candidates.add(method);
            }
        });
        if (candidates.isEmpty()) {
            throw new IllegalStateException("TestFactory method not found: " + methodName);
        }
        if (candidates.size() > 1) {
            throw new IllegalStateException("Multiple TestFactory methods found with name: " + methodName);
        }
        return candidates.get(0);
    }
}
