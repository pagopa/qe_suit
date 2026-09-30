package it.pagopa.infrastructure.objectgraph;

sealed interface QueryStep permits PropertyStep, IndexStep {
}
