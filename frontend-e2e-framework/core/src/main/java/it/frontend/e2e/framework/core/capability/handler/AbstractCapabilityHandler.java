package it.frontend.e2e.framework.core.capability.handler;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.context.CapabilityContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import lombok.Getter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public abstract class AbstractCapabilityHandler<C extends Capability> implements ICapabilityHandler {

    private static final ConcurrentMap<Class<?>, CapabilityIntrospection> CACHE = new ConcurrentHashMap<>();

    private final Class<C> capabilityClass;
    protected final C capabilityImpl;
    private final CapabilityIntrospection introspection;

    @SuppressWarnings("unchecked")
    protected AbstractCapabilityHandler(C capabilityImpl) {
        this.capabilityImpl = capabilityImpl;
        this.capabilityClass = (Class<C>) capabilityImpl.getClass();
        this.introspection = CACHE.computeIfAbsent(this.capabilityClass, CapabilityIntrospection::build);
    }

    @Override
    public boolean canHandle(Method method) {
        if (method == null) return false;

        Class<?> declaringClass = method.getDeclaringClass();

        // Se non c'entra nulla con la capability, skip
        if (!capabilityClass.isAssignableFrom(declaringClass) && !declaringClass.isAssignableFrom(capabilityClass)) {
            return false;
        }

        // CASO 1: method è della capability stessa o di una sua sotto-interfaccia
        // gestisce solo se NON esiste overload/override in una sotto-interfaccia
        if (capabilityClass.isAssignableFrom(declaringClass)) {
            if (declaringClass != capabilityClass) return false;
            return !introspection.redefinedInSubInterfacesBySig.contains(sig(method));
        }

        // CASO 2: method è dichiarato in una super-interfaccia della capability
        // gestisci SOLO se la capability fa overload/override del metodo
        if (declaringClass.isAssignableFrom(capabilityClass)) {
            return introspection.declaredInThisCapabilityBySig.contains(sig(method));
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T handle(Method method, Object[] args, CapabilityScope scope) {
        if (method == null) throw new IllegalArgumentException("method is null");

        Object[] safeArgs = (args != null) ? args : new Object[0];
        CapabilityContext.push(scope);

        try {
            MethodHandle mh = introspection.invokers.computeIfAbsent(method, m -> buildInvoker(capabilityClass, m));
            Object result = mh.bindTo(capabilityImpl).invokeWithArguments(safeArgs);
            return (T) result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw rethrowUnchecked(t);
        } finally {
            CapabilityContext.popSelector();
        }
    }

    private static String sig(Method m) {
        StringBuilder sb = new StringBuilder(m.getName()).append('(');
        for (Class<?> p : m.getParameterTypes()) {
            sb.append(p.getName()).append(',');
        }
        return sb.append(')').toString();
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> type) {
        Set<Class<?>> result = new LinkedHashSet<>();
        Deque<Class<?>> stack = new ArrayDeque<>();
        if (type != null) stack.push(type);

        // Recupera tutte le interfacce implementate dalla classe e dalle sue super-classi
        while (!stack.isEmpty()) {
            Class<?> current = stack.pop();
            for (Class<?> itf : current.getInterfaces()) {
                if (result.add(itf)) {
                    stack.push(itf);
                }
            }
            Class<?> parent = current.getSuperclass();
            if (parent != null && parent != Object.class) {
                stack.push(parent);
            }
        }

        // Espandi anche le super-interfacce delle interfacce raccolte
        Deque<Class<?>> itfStack = new ArrayDeque<>(result);
        while (!itfStack.isEmpty()) {
            Class<?> itf = itfStack.pop();
            for (Class<?> parent : itf.getInterfaces()) {
                if (result.add(parent)) {
                    itfStack.push(parent);
                }
            }
        }

        return result;
    }

    private record CapabilityIntrospection(
            Set<String> declaredInThisCapabilityBySig,
            Set<String> redefinedInSubInterfacesBySig,
            ConcurrentMap<Method, MethodHandle> invokers
    ) {
        static CapabilityIntrospection build(Class<?> capabilityClass) {
            Set<String> declared = new HashSet<>();
            for (Method m : capabilityClass.getDeclaredMethods()) declared.add(sig(m));

            Set<String> subRedefined = new HashSet<>();
            for (Class<?> itf : getAllInterfaces(capabilityClass)) {
                if (itf == capabilityClass) continue;
                if (!capabilityClass.isAssignableFrom(itf)) continue;
                for (Method m : itf.getDeclaredMethods()) subRedefined.add(sig(m));
            }

            return new CapabilityIntrospection(
                    Collections.unmodifiableSet(declared),
                    Collections.unmodifiableSet(subRedefined),
                    new ConcurrentHashMap<>()
            );
        }
    }

    private static MethodHandle buildInvoker(Class<?> capabilityClass, Method interfaceOrSuperMethod) {
        try {
            // Prova a trovare il metodo "equivalente" sulla capability concreta
            Method target = getTargetMethod(capabilityClass, interfaceOrSuperMethod);

            // Assicurati che il metodo sia accessibile
            target.setAccessible(true);

            // MethodHandle per invocazione più veloce rispetto a Method.invoke
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return lookup.unreflect(target);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "No matching method on capabilityClass=" + capabilityClass.getName() +
                            " for " + interfaceOrSuperMethod,
                    e
            );
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot access method: " + interfaceOrSuperMethod, e);
        }
    }

    private static Method getTargetMethod(Class<?> capabilityClass, Method interfaceOrSuperMethod) throws NoSuchMethodException {
        Method target;
        try {
            target = capabilityClass.getMethod(
                        interfaceOrSuperMethod.getName(),
                        interfaceOrSuperMethod.getParameterTypes()
                    );
        } catch (NoSuchMethodException e) {
            // fallback: può essere non-public o dichiarato diversamente
            target = capabilityClass.getDeclaredMethod(
                        interfaceOrSuperMethod.getName(),
                        interfaceOrSuperMethod.getParameterTypes()
                    );
        }
        return target;
    }

    private static RuntimeException rethrowUnchecked(Throwable t) {
        if (t instanceof RuntimeException re) return re;
        if (t instanceof Error er) throw er;
        return new RuntimeException(t);
    }
}
