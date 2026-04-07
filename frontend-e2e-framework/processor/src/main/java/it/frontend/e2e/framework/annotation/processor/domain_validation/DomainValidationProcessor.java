package it.frontend.e2e.framework.annotation.processor.domain_validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.frontend.e2e.framework.annotation.processor.domain_validation.factory.DomainValidatorFactory;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.RuleEngine;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.DescriptorLoader;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.descriptor_parser.json.JsonDomainDescriptorParser;
import it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.ScopeResolver;
import it.frontend.e2e.framework.core.meta.Constraint;
import it.frontend.e2e.framework.core.meta.Descriptor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DomainValidationProcessor extends AbstractProcessor {
    private List<Descriptor> domains = Collections.emptyList();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        DescriptorLoader descriptorLoader =
                new DescriptorLoader(
                        new JsonDomainDescriptorParser(new ObjectMapper()),
                        new DomainValidatorFactory()
                );

        this.domains = descriptorLoader.loadAll(
                getClass().getClassLoader(),
                env.getMessager()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver() || domains.isEmpty())
            return false;

        ScopeResolver scopeResolver = new ScopeResolver(processingEnv);
        RuleEngine ruleEngine = new RuleEngine(processingEnv);

        for (Descriptor domain : domains) {

            if (domain.constraints == null || domain.constraints.isEmpty())
                continue;

            for (Constraint constraint : domain.constraints) {
                Set<TypeElement> targets = scopeResolver.resolve(domain, constraint.scope, roundEnv);

                if (constraint.mustHaveAnnotation != null)
                    ruleEngine.applyMustHaveAnnotation(constraint, targets);

                if (constraint.mustNotHaveAnnotation != null)
                    ruleEngine.applyMustNotHaveAnnotation(constraint, targets);
            }
        }

        return false;
    }
}
