package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver;

import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.ScopeResolutionStrategy;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.factory.ScopeResolutionStrategyFactory;
import it.frontend.e2e.framework.core.meta.Descriptor;
import it.frontend.e2e.framework.core.meta.Scope;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

public final class ScopeResolver {

    private final Types types;
    private final Elements elements;
    private final Messager messager;
    private final ScopeResolutionStrategyFactory factory = new ScopeResolutionStrategyFactory();

    public ScopeResolver(ProcessingEnvironment env) {
        this.types = env.getTypeUtils();
        this.elements = env.getElementUtils();
        this.messager = env.getMessager();
    }

    public Set<TypeElement> resolve(Descriptor domain, String scopeName, RoundEnvironment roundEnv) {

        Scope scope = domain.scopes.get(scopeName);

        if (scope == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Unknown scope '" + scopeName +
                            "' in domain '" + domain.domainId + "'"
            );
            return Set.of();
        }

        try {
            ScopeResolutionStrategy strategy = factory.forScope(scope);
            return strategy.resolve(scope, domain, roundEnv, types, elements, messager);
        } catch (IllegalArgumentException e) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    e.getMessage()
            );
            return Set.of();
        }
    }
}





