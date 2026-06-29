package it.pagopa.interop.common.contract.model.eservice_template;

import it.pagopa.interop.common.contract.model.shared.DocumentRef;
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
    Boolean newTemplateVersionAvailable;
    Integer templateDailyCallsPerConsumer;
    Integer templateDailyCallsTotal;
}


