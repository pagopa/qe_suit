package it.pagopa.interop.common.infrastructure.reporting.contract.renderer;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ContractReportFormatters {

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            .withLocale(Locale.ITALY)
            .withZone(ROME);

    public String formatTimestamp(Instant value) {
        return value == null ? "-" : TIMESTAMP_FORMATTER.format(value);
    }

    public String formatDuration(Duration duration) {
        if (duration == null) {
            return "-";
        }
        long millis = Math.max(0, duration.toMillis());
        if (millis < 1_000) {
            return millis + " ms";
        }
        long minutes = millis / 60_000;
        double seconds = (millis % 60_000) / 1000.0;
        String secondText = String.format(Locale.ITALY, "%.2f", seconds).replace('.', ',');
        if (minutes == 0) {
            return secondText + "s";
        }
        return minutes + "m " + secondText + "s";
    }

    public String statusClass(String status) {
        return status == null ? "" : status.toLowerCase(Locale.ROOT);
    }

    public String channelClass(String channelKey) {
        if (channelKey == null) {
            return "channel-default";
        }
        return "channel-" + channelKey.toLowerCase(Locale.ROOT);
    }
}
