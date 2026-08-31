package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.interop.common.infrastructure.objectgraph.Node;

interface MutationValidityResolver {

    ContractValidity resolve(Node node, FuzzMutation mutation);
}
