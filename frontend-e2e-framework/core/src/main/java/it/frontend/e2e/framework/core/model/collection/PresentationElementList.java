package it.frontend.e2e.framework.core.model.collection;

import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.List;

public class PresentationElementList<T extends Capability, S extends Selector, L extends Location, E extends AbstractPresentationElement<S,L>> extends AbstractList<T> {

    private final Class<T> itemType;
    private final CapabilityScope collectionScope;
    private final ICapabilityDispatcher dispatcher;

    public PresentationElementList(
            Class<?> itemType,
            CapabilityScope collectionScope,
            ICapabilityDispatcher dispatcher
    ) {
        this.itemType = (Class<T>) itemType;
        this.collectionScope = collectionScope;
        this.dispatcher = dispatcher;
    }

    @Override
    public T get(int index) {
        String indexedSelector = "(" + collectionScope.selector() + ")[" + (index + 1) + "]";

        CapabilityScope itemScope = new CapabilityScope(
                indexedSelector,
                collectionScope.location(),
                false
        );

        return itemType.cast(Proxy.newProxyInstance(
                itemType.getClassLoader(),
                new Class<?>[]{itemType},
                new DefaultBinderInvocationHandler(
                        dispatcher,
                        new BindContext(itemScope)
                )
        ));
    }

    @Override
    public int size() {
        return bindGettable(collectionScope)
                .getAll()
                .map(List::size)
                .orElse(0);
    }

    @SuppressWarnings("unchecked")
    private Gettable<S,L,E> bindGettable(CapabilityScope scope) {
        return (Gettable<S,L,E>) Proxy.newProxyInstance(
                Gettable.class.getClassLoader(),
                new Class<?>[]{Gettable.class},
                new DefaultBinderInvocationHandler(
                        dispatcher,
                        new BindContext(scope)
                )
        );
    }
}
