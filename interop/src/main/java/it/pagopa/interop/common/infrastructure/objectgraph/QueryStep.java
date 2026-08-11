package it.pagopa.interop.common.infrastructure.objectgraph;

sealed interface QueryStep permits PropertyStep, IndexStep {
}
