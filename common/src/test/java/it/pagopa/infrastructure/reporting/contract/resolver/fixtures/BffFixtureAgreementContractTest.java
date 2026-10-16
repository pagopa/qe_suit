package it.pagopa.infrastructure.reporting.contract.resolver.fixtures;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Fixture contract class scanned by ContractTargetRegressionTest.
 * Method names must match the operationIds declared in reporting/openapi/all-api-contracts.yaml.
 */
public class BffFixtureAgreementContractTest {

    @TestFactory
    Stream<DynamicTest> createAgreement() {
        return Stream.empty();
    }

    @TestFactory
    Stream<DynamicTest> getAgreementById() {
        return Stream.empty();
    }
}
