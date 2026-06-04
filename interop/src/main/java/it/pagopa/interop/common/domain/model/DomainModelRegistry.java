package it.pagopa.interop.common.domain.model;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DomainModelRegistry {

    // Mappa: Chiave = Classe Model OpenAPI (Padre) -> Valore = Model di dominio (Figlio)
    private final Map<Class<?>, Class<? extends TestModel>> registry = new HashMap<>();

    @PostConstruct
    public void init() {
        // Provider di Spring per scansionare il classpath (senza filtri di default come @Component)
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        // Cerca qualsiasi cosa implementi l'interfaccia TestModel
        scanner.addIncludeFilter(new AssignableTypeFilter(TestModel.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("it.pagopa.interop")) {
            try {
                Class<?> domainClass = Class.forName(bd.getBeanClassName());

                // Esclude le interfacce stesse (come TestModel o TestChildModel)
                if (!domainClass.isInterface()) {
                    Class<?> openApiSuperclass = domainClass.getSuperclass();

                    // Se la classe estende effettivamente un modello dell'OpenAPI Client
                    if (openApiSuperclass != null && openApiSuperclass != Object.class) {
                        registry.put(openApiSuperclass, (Class<? extends TestModel>) domainClass);
                    }
                }
            } catch (ClassNotFoundException e) {
                // Classe non trovata al caricamento, ignoriamo in sicurezza
            }
        }
    }

    /**
     * Data la classe autogenerata dall'OpenAPI, restituisce la rispettiva classe di dominio.
     */
    @SuppressWarnings("unchecked")
    public <T extends TestModel> Class<T> getDomainClassFor(Class<?> openApiClass) {
        return (Class<T>) registry.get(openApiClass);
    }
}
