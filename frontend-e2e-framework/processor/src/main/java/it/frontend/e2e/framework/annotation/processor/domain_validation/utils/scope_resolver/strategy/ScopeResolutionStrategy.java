package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy;

import it.frontend.e2e.framework.core.meta.Descriptor;
import it.frontend.e2e.framework.core.meta.Scope;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

public interface ScopeResolutionStrategy {
    Set<TypeElement> resolve(
            Scope scope,
            Descriptor domain,
            RoundEnvironment roundEnv,
            Types types,
            Elements elements,
            Messager messager
    );
}

