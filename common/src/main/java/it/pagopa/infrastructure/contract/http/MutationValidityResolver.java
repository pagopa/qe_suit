package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.objectgraph.Node;

interface MutationValidityResolver {

    ContractValidity resolve(Node node, FuzzMutation mutation);
}
