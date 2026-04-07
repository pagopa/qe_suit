package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.subtypesOf;

import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.AbstractScopeResolutionStrategy;
import it.frontend.e2e.framework.core.meta.Descriptor;
import it.frontend.e2e.framework.core.meta.Scope;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

public final class SubtypesOfResolutionStrategy extends AbstractScopeResolutionStrategy {

    @Override
    public Set<TypeElement> resolve(
            Scope scope,
            Descriptor domain,
            RoundEnvironment roundEnv,
            Types types,
            Elements elements,
            Messager messager) {

        TypeElement target =
                elements.getTypeElement(scope.subtypesOf);

        if (target == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Scope target type not found: " + scope.subtypesOf
            );
            return Set.of();
        }

        Set<ElementKind> acceptedKinds = acceptedKinds(scope);
        boolean includeTarget = includeTarget(scope);

        Set<TypeElement> result = new HashSet<>();

        for (Element root : roundEnv.getRootElements()) {

            if (!(root instanceof TypeElement candidate)) {
                continue;
            }

            if (!acceptedKinds.contains(candidate.getKind())) {
                continue;
            }

            if (!includeTarget &&
                    types.isSameType(candidate.asType(), target.asType())) {
                continue;
            }

            if (types.isAssignable(candidate.asType(), target.asType())) {
                result.add(candidate);
            }
        }

        return result;
    }
}


