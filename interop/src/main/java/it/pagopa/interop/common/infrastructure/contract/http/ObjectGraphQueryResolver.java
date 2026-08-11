package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphQuery;

interface ObjectGraphQueryResolver {

    <T> ObjectGraphQuery resolve(Class<T> rootType, TargetExpression<T> expression);
}
