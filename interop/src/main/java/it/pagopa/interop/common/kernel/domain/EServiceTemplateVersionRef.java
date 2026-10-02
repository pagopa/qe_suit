package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record EServiceTemplateVersionRef(UUID id) {
	public static EServiceTemplateVersionRef of(UUID id) {
		return new EServiceTemplateVersionRef(id);
	}
}