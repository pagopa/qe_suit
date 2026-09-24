package it.pagopa.interop.common.kernel.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder(toBuilder = true)
@Jacksonized
public record EServiceTemplateRef(UUID id) {
	public static EServiceTemplateRef of(UUID id) {
		return new EServiceTemplateRef(id);
	}
}