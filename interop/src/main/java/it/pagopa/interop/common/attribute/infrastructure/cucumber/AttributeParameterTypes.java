package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttributeParameterTypes {
    private final EntityStore entityStore;

    @ParameterType("certificato|certificato discreto|dichiarato|verificato")
    public AttributeKind attributeKind(String kind) {
        return switch (kind) {
            case "certificato" -> AttributeKind.CERTIFIED;
            case "certificato discreto" -> AttributeKind.CERTIFIED_DISCRETE;
            case "dichiarato" -> AttributeKind.DECLARED;
            case "verificato" -> AttributeKind.VERIFIED;
            default -> throw new IllegalArgumentException("Unsupported attribute kind: " + kind);
        };
    }

    @ParameterType("attributo \"([^\"]+)\"")
    public Attribute attributeByAlias(String alias) {
        return entityStore.getByAliasOrThrow(alias, Attribute.class);
    }
}
