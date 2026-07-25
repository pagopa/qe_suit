package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.common.infrastructure.http.contract.GetterProvider;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

final class LambdaFieldNameExtractor {

    private LambdaFieldNameExtractor() {
    }

    static <T> String fieldNameOf(GetterProvider<T, ?> getter) {
        try {
            Method writeReplace = getter.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);

            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(getter);
            return propertyNameFrom(lambda.getImplMethodName());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Impossibile ricavare il nome del campo dal method reference",
                    e
            );
        }
    }

    private static String propertyNameFrom(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return decapitalize(methodName.substring(3));
        }
        if (methodName.startsWith("is") && methodName.length() > 2) {
            return decapitalize(methodName.substring(2));
        }
        return methodName;
    }

    private static String decapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}