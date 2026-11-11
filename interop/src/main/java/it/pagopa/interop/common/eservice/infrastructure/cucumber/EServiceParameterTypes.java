package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;

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

    @ParameterType("ricezione|erogazione")
    public EServiceMode eServiceMode(String mode) {
        return switch (mode.toLowerCase()) {
            case "ricezione" -> EServiceMode.RECEIVE;
            case "erogazione" -> EServiceMode.DELIVER;
            default -> throw new IllegalArgumentException("Modalità e-service non supportata: " + mode);
        };
    }
}
