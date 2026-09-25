package it.pagopa.send.common.legal_notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChipTypes {
    WARNING("warning"),
        ERROR("error"),
            SUCCESS("success");

    private final String value;
}
