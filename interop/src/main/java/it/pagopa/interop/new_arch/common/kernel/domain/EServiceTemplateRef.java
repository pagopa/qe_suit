package it.pagopa.interop.new_arch.common.kernel.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EServiceTemplateRef {
    UUID templateId;
    UUID templateVersionId;
    String templateName;
    DocumentRef templateInterface;
    Boolean isNewTemplateVersionAvailable;
    Integer templateDailyCallsPerConsumer;
    Integer templateDailyCallsTotal;
}


