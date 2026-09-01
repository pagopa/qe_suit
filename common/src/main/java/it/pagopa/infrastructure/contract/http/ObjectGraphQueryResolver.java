package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.objectgraph.ObjectGraphQuery;

interface ObjectGraphQueryResolver {

    <T> ObjectGraphQuery resolve(Class<T> rootType, TargetExpression<T> expression);
}
