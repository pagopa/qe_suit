package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttribute;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BffTenantAttributeMapper {
    private BffTenantAttributeMapper() {
    }

    public static Set<UUID> activeIds(TenantAttributes attributes, AttributeKind kind) {
        Objects.requireNonNull(attributes, "Tenant response has no attributes");
        return switch (kind) {
            case CERTIFIED, CERTIFIED_DISCRETE -> stream(attributes.getCertified())
                    .filter(attribute -> attribute.getRevocationTimestamp() == null)
                    .filter(attribute -> attribute.getKind() != null
                            && attribute.getKind().name().equals(kind.name()))
                    .map(it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttribute::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            case DECLARED -> stream(attributes.getDeclared())
                    .filter(attribute -> attribute.getRevocationTimestamp() == null)
                    .map(it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttribute::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            case VERIFIED -> stream(attributes.getVerified())
                    .filter(BffTenantAttributeMapper::hasActiveVerification)
                    .map(it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttribute::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        };
    }

    private static <T> Stream<T> stream(List<T> values) {
        return values == null ? Stream.empty() : values.stream();
    }

    private static boolean hasActiveVerification(VerifiedTenantAttribute attribute) {
        if (attribute.getVerifiedBy() == null || attribute.getVerifiedBy().isEmpty()) return false;
        Instant now = Instant.now();
        return attribute.getVerifiedBy().stream().anyMatch(verification -> {
            if (verification.getId() == null) return false;
            if (verification.getExpirationDate() != null
                    && !parseApiDate(verification.getExpirationDate()).isAfter(now)) return false;
            return stream(attribute.getRevokedBy())
                    .filter(revocation -> Objects.equals(revocation.getId(), verification.getId()))
                    .noneMatch(revocation -> revocation.getRevocationDate() == null
                            || verification.getVerificationDate() == null
                            || !parseApiDate(revocation.getRevocationDate())
                            .isBefore(parseApiDate(verification.getVerificationDate())));
        });
    }

    private static Instant parseApiDate(String value) {
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
            } catch (DateTimeParseException exception) {
                throw new IllegalStateException("Unsupported tenant attribute date: " + value, exception);
            }
        }
    }
}
