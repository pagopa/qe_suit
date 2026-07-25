package it.pagopa.interop.common.infrastructure.http.contract;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface GetterProvider<T, R> extends Function<T, R>, Serializable {

}
