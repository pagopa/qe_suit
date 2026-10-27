package it.pagopa.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDownloadMultipart {
    @JsonProperty("file")
    private String file;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("prettyName")
    private String prettyName;
}
