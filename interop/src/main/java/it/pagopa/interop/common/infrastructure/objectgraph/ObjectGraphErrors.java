package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.JavaType;

final class ObjectGraphErrors {

    private ObjectGraphErrors() {
    }

    static ObjectGraphException fail(String message, NodePath path, JavaType javaType, Throwable cause) {
        StringBuilder builder = new StringBuilder(message);
        if (path != null) {
            builder.append(" [path=").append(path).append("]");
        }
        if (javaType != null) {
            builder.append(" [javaType=").append(javaType).append("]");
        }
        return cause == null
                ? new ObjectGraphException(builder.toString())
                : new ObjectGraphException(builder.toString(), cause);
    }
}
