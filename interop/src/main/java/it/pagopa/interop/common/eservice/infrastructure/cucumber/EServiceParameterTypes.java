package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;

import java.util.Arrays;

public class EServiceParameterTypes {

    @ParameterType("30|60|90|120")
    public GracePeriodDays gracePeriodDays(String value) {
        int days = Integer.parseInt(value);
        return Arrays.stream(GracePeriodDays.values())
                .filter(period -> period.getDays() == days)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported grace period in days: " + value));
    }

    @ParameterType("assenza di soap:address|file SOAP non parsabile|assenza di soap:operation")
    public SoapInterfaceUploadFailureReason soapInterfaceUploadFailureReason(String reason) {
        return switch (reason) {
            case "assenza di soap:address" -> SoapInterfaceUploadFailureReason.MISSING_SOAP_ADDRESS;
            case "file SOAP non parsabile" -> SoapInterfaceUploadFailureReason.NOT_PARSABLE_SOAP_FILE;
            case "assenza di soap:operation" -> SoapInterfaceUploadFailureReason.MISSING_SOAP_OPERATION;
            default -> throw new IllegalArgumentException("Motivo di rifiuto upload interfaccia SOAP non supportato: " + reason);
        };
    }
}
