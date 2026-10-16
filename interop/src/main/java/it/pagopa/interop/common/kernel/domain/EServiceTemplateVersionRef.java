package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record EServiceTemplateVersionRef(UUID versionId) {
	public static EServiceTemplateVersionRef of(UUID versionId) {
		return new EServiceTemplateVersionRef(versionId);
	}
}