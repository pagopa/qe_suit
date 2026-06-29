package it.pagopa.interop.common.contract.model.eservice;

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
    DescriptorDocumentRef templateInterface;
    Boolean newTemplateVersionAvailable;
    Integer templateDailyCallsPerConsumer;
    Integer templateDailyCallsTotal;
}


