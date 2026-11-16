package it.frontend.e2e.framework.core.model.collection;

import it.frontend.e2e.framework.core.binder.ComponentList;
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
import java.util.Spliterator;
import java.util.Spliterators;

public class PresentationElementList<T extends Capability, S extends Selector, L extends Location, E extends AbstractPresentationElement<S, L>> extends AbstractList<T> implements ComponentList<T> {

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
        return get(index, Gettable.FindPolicy.PRESENT);
    }

    @Override
    public T get(int index, Gettable.FindPolicy findPolicy) {
        java.util.Objects.requireNonNull(findPolicy, "findPolicy");

        CapabilityScope scope = itemScope(index);

        boolean found = bindGettable(scope)
                .getAll(findPolicy)
                .map(elements -> !elements.isEmpty())
                .orElse(false);

        if (!found) {
            throw new java.util.NoSuchElementException(
                    "Elemento all'indice " + index
                            + " non risolto con policy " + findPolicy
            );
        }

        return createItemProxy(scope);
    }

    @Override
    public int size() {
        return size(Gettable.FindPolicy.PRESENT);
    }

    @Override
    public int size(Gettable.FindPolicy findPolicy) {
        return bindGettable(collectionScope)
                .getAll(findPolicy)
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


    private CapabilityScope itemScope(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Indice negativo: " + index);
        }

        String indexedSelector =
                "(" + collectionScope.selector() + ")[" + (index + 1) + "]";

        return new CapabilityScope(
                indexedSelector,
                collectionScope.location(),
                false
        );
    }

    private T createItemProxy(CapabilityScope scope) {
        return itemType.cast(Proxy.newProxyInstance(
                itemType.getClassLoader(),
                new Class<?>[]{itemType},
                new DefaultBinderInvocationHandler(
                        dispatcher,
                        new BindContext(scope)
                )
        ));
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(
                iterator(),
                Spliterator.ORDERED
        );
    }
}
