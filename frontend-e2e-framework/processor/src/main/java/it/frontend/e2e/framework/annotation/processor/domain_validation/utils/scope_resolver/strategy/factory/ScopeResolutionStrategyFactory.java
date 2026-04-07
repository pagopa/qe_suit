package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.factory;

import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.ScopeResolutionStrategy;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.subtypesOf.SubtypesOfResolutionStrategy;
import it.frontend.e2e.framework.core.meta.Scope;

public final class ScopeResolutionStrategyFactory {

    public ScopeResolutionStrategy forScope(Scope scope) {

        if (scope.subtypesOf != null) {
            return new SubtypesOfResolutionStrategy();
        }

        throw new IllegalArgumentException(
                "Unsupported scope definition"
        );
    }
}

