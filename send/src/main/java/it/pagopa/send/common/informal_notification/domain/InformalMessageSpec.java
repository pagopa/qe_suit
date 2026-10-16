package it.pagopa.send.common.informal_notification.domain;

public record InformalMessageSpec(
        String subject,
        String longBody,
        String shortBody,
        String language
) {}
