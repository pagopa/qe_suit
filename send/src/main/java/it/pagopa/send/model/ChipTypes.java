package it.pagopa.send.model;

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
