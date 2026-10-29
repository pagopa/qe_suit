package it.pagopa.infrastructure.contract.http;

import java.lang.reflect.Field;

/**
 * Identificazione di un'operazione ricavata dai metadati del client OpenAPI generato
 * (classe {@code <Api>.<Operation>Oper} con le costanti {@code REQ_METHOD} e {@code REQ_URI}).
 */
record HttpOperationTarget(String api, String operation, String method, String route) {
    private static final String OPERATION_SUFFIX = "Oper";

    static HttpOperationTarget from(Object operation) {
        Class<?> type = operation.getClass();
        while (type.isAnonymousClass() || type.isSynthetic()) {
            type = type.getSuperclass();
        }
        Class<?> enclosing = type.getEnclosingClass();
        String simpleName = type.getSimpleName();
        String operationName = simpleName.endsWith(OPERATION_SUFFIX) && simpleName.length() > OPERATION_SUFFIX.length()
                ? simpleName.substring(0, simpleName.length() - OPERATION_SUFFIX.length())
                : simpleName;
        return new HttpOperationTarget(
                enclosing == null ? null : enclosing.getSimpleName(),
                Character.toLowerCase(operationName.charAt(0)) + operationName.substring(1),
                staticConstant(type, "REQ_METHOD"),
                staticConstant(type, "REQ_URI")
        );
    }

    /**
     * Target mostrato nel report, nello stesso formato dell'indice OpenAPI ({@code GET /path}).
     */
    String reportTarget() {
        if (method != null && route != null) {
            return method + " " + route;
        }
        return label();
    }

    String label() {
        return api == null ? operation : api + "." + operation;
    }

    private static String staticConstant(Class<?> type, String name) {
        try {
            Field field = type.getField(name);
            Object value = field.get(null);
            return value == null ? null : value.toString();
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }
}
