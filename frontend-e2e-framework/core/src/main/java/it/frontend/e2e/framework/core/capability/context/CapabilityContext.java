package it.frontend.e2e.framework.core.capability.context;

import java.util.ArrayDeque;
import java.util.Deque;

public class CapabilityContext {
    private CapabilityContext() {}

    private static final ThreadLocal<Deque<CapabilityScope>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(CapabilityScope scope) {
        STACK.get().push(scope);
    }

    public static String selector() {
        CapabilityScope current = STACK.get().peek();
        return current != null ? current.selector() : null;
    }

    public static String location() {
        CapabilityScope current = STACK.get().peek();
        return current != null ? current.location() : null;
    }

    public static void pop() {
        Deque<CapabilityScope> d = STACK.get();
        if (!d.isEmpty()) {
            d.pop();
        }
        if (d.isEmpty()) {
            STACK.remove();
        }
    }

    public static void popSelector() {
        pop();
    }
}
