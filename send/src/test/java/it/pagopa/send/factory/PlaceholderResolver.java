package it.pagopa.send.factory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaceholderResolver {
    // thread-safe
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String resolve(String value) {
        if (value == null) return null;
        return value
                .replace("{timestamp}", LocalDateTime.now().format(FORMATTER))
                .replace("{counter}",   String.valueOf(COUNTER.incrementAndGet()))
                .replace("{uuid}",      java.util.UUID.randomUUID().toString().substring(0, 8));
    }
}