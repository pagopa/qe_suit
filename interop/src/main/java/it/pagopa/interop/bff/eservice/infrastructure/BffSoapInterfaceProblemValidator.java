package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.generated.openapi.clients.bff.model.Problem;
import org.assertj.core.api.Assertions;

public final class BffSoapInterfaceProblemValidator {

    private BffSoapInterfaceProblemValidator() {
    }

    public static void assertSoapInterfaceProblem(RawResponse rawResponse, SoapInterfaceUploadFailureReason reason) {
        Problem problem = rawResponse.as(Problem.class);
        Assertions.assertThat(problem).isNotNull();
        Assertions.assertThat(problem.getStatus()).isEqualTo(400);
        Assertions.assertThat(problem.getTitle()).isNotEqualTo("Unexpected error");

        switch (reason) {
            case MISSING_SOAP_ADDRESS -> assertProblemFields(
                    problem,
                    "Error extracting field from SOAP file",
                    "Error extracting field soap:address from SOAP file",
                    "10017"
            );
            case NOT_PARSABLE_SOAP_FILE -> assertProblemFields(
                    problem,
                    "Error while parsing SOAP file",
                    "Error while parsing SOAP file",
                    "10016"
            );
            case MISSING_SOAP_OPERATION -> assertProblemFields(
                    problem,
                    "Error extracting field from SOAP file",
                    "Error extracting field soap:operation from SOAP file",
                    "10017"
            );
        }
    }

    private static void assertProblemFields(
            Problem problem,
            String expectedTitle,
            String expectedDetail,
            String expectedCodeSuffix
    ) {
        Assertions.assertThat(problem.getTitle()).isEqualTo(expectedTitle);
        if (problem.getDetail() != null) {
            Assertions.assertThat(problem.getDetail()).isEqualTo(expectedDetail);
        }
        Assertions.assertThat(problem.getErrors())
                .isNotNull()
                .isNotEmpty()
                .anySatisfy(error -> {
                    Assertions.assertThat(error.getCode()).endsWith(expectedCodeSuffix);
                    Assertions.assertThat(error.getDetail()).isEqualTo(expectedDetail);
                });
    }
}

