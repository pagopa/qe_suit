package it.frontend.e2e.framework.core.utils;

import it.frontend.e2e.framework.annotation.selector.XPath;
import java.lang.reflect.Method;

public class XPathResolver {
    public static String resolve(Method method, Class<?> returnType) {
        XPath onMethod = method.getAnnotation(XPath.class);
        if (onMethod != null) return onMethod.value();
        XPath onType = returnType.getAnnotation(XPath.class);
        if (onType != null) return onType.value();
        return "";
    }

    public static String compose(String parent, String child) {
        if (parent == null || parent.isBlank()) return child;
        if (child == null || child.isBlank()) return parent;
        parent = parent.trim();
        child = child.trim();
        if (child.startsWith("./")) child = child.substring(2);
        if (parent.endsWith("/")) return parent + child;
        return parent + "/" + child;
    }
}

