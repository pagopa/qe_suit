package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.Node;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class JacksonMutationValidityResolver implements MutationValidityResolver {
    private final ObjectMapper objectMapper;
    private final Class<?> rootType;

    JacksonMutationValidityResolver(ObjectMapper objectMapper, Class<?> rootType) {
        this.objectMapper = objectMapper;
        this.rootType = rootType;
    }

    @Override
    public ContractValidity resolve(Node node, FuzzMutation mutation) {
        Optional<PropertyMeta> maybeMeta = resolvePropertyMeta(node.path().toString());
        if (maybeMeta.isEmpty()) return ContractValidity.UNKNOWN;
        PropertyMeta meta = maybeMeta.get();
        meta.validate();

        if (mutation.scenario() == FuzzScenario.REPLACED_WITH_NULL) {
            if (meta.nullForbidden()) return ContractValidity.INVALID;
            if (meta.nullAllowedExplicitly()) return ContractValidity.VALID;
            return ContractValidity.UNKNOWN;
        }
        if (mutation.scenario() == FuzzScenario.REMOVED) {
            if (meta.presenceForbiddenToRemove()) return ContractValidity.INVALID;
            if (meta.optionalByJsonProperty()) return ContractValidity.VALID;
            return ContractValidity.UNKNOWN;
        }

        if (!(mutation.value() instanceof String value)) {
            return ContractValidity.UNKNOWN;
        }
        if (meta.notBlank && (value.isBlank() || value.isEmpty())) return ContractValidity.INVALID;
        if (meta.notEmpty && value.isEmpty()) return ContractValidity.INVALID;
        if (meta.minSize != null && value.length() < meta.minSize) return ContractValidity.INVALID;
        if (meta.maxSize != null && value.length() > meta.maxSize) return ContractValidity.INVALID;

        if (meta.notBlank || meta.notEmpty || meta.minSize != null || meta.maxSize != null) return ContractValidity.VALID;

        return ContractValidity.UNKNOWN;
    }

    private Optional<PropertyMeta> resolvePropertyMeta(String pointer) {
        if (pointer.isEmpty()) return Optional.empty();
        List<String> tokens = parsePointer(pointer);
        JavaType currentType = objectMapper.constructType(rootType);
        PropertyMeta lastMeta = null;
        for (String token : tokens) {
            if (isIndex(token)) {
                currentType = collectionElementType(currentType);
                continue;
            }
            Optional<BeanPropertyDefinition> property = introspect(currentType).findProperties().stream()
                    .filter(p -> p.getName().equals(token))
                    .findFirst();
            if (property.isEmpty()) return Optional.empty();
            lastMeta = PropertyMeta.from(property.get());
            currentType = propertyType(property.get());
        }
        return Optional.ofNullable(lastMeta);
    }

    private BeanDescription introspect(JavaType type) {
        return objectMapper.getSerializationConfig().introspect(type);
    }

    private JavaType propertyType(BeanPropertyDefinition property) {
        AnnotatedMember accessor = property.getAccessor();
        if (accessor != null) return accessor.getType();
        return objectMapper.constructType(Object.class);
    }

    private JavaType collectionElementType(JavaType type) {
        if (type.getContentType() != null) return type.getContentType();
        if (type.hasRawClass(List.class)) return objectMapper.constructType(Object.class);
        if (type.isArrayType()) return type.getContentType();

        return objectMapper.constructType(Object.class);
    }

    private boolean isIndex(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return !token.isEmpty();
    }

    private List<String> parsePointer(String pointer) {
        String[] raw = pointer.substring(1).split("/", -1);
        List<String> tokens = new ArrayList<>(raw.length);
        for (String token : raw) {
            tokens.add(token.replace("~1", "/").replace("~0", "~"));
        }
        return tokens;
    }

    private static final class PropertyMeta {
        private final boolean requiredByJsonProperty;
        private final boolean optionalByJsonProperty;
        private final boolean notNull;
        private final boolean nullable;
        private final boolean notBlank;
        private final boolean notEmpty;
        private final Integer minSize;
        private final Integer maxSize;

        private PropertyMeta(boolean requiredByJsonProperty, boolean optionalByJsonProperty, boolean notNull, boolean nullable, boolean notBlank, boolean notEmpty, Integer minSize, Integer maxSize) {
            this.requiredByJsonProperty = requiredByJsonProperty;
            this.optionalByJsonProperty = optionalByJsonProperty;
            this.notNull = notNull;
            this.nullable = nullable;
            this.notBlank = notBlank;
            this.notEmpty = notEmpty;
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        static PropertyMeta from(BeanPropertyDefinition property) {
            List<AnnotatedMember> members = List.of(property.getAccessor(), property.getField(), property.getGetter()).stream().filter(java.util.Objects::nonNull).toList();
            JsonProperty jsonProperty = firstAnnotation(members, JsonProperty.class);
            jakarta.validation.constraints.Size size = firstAnnotation(members, jakarta.validation.constraints.Size.class);
            boolean notNull = hasAny(members, Nonnull.class, NotNull.class);
            boolean nullable = hasAny(members, Nullable.class);
            boolean notBlank = hasAny(members, NotBlank.class);
            boolean notEmpty = hasAny(members, NotEmpty.class);
            return new PropertyMeta(
                    jsonProperty != null && jsonProperty.required(),
                    jsonProperty != null && !jsonProperty.required(),
                    notNull,
                    nullable,
                    notBlank,
                    notEmpty,
                    size == null ? null : size.min(),
                    size == null ? null : size.max()
            );
        }

        void validate() {
            if (notNull && nullable) {
                throw new ContractHttpException("Contradictory nullability metadata detected");
            }
        }

        boolean nullForbidden() {
            return notNull || notBlank || notEmpty;
        }

        boolean nullAllowedExplicitly() {
            return nullable && !nullForbidden();
        }

        boolean presenceForbiddenToRemove() {
            return requiredByJsonProperty || notBlank || notEmpty;
        }

        boolean optionalByJsonProperty() {
            return optionalByJsonProperty && !presenceForbiddenToRemove();
        }

        @SafeVarargs
        private static boolean hasAny(List<AnnotatedMember> members, Class<? extends java.lang.annotation.Annotation>... types) {
            for (Class<? extends java.lang.annotation.Annotation> type : types) {
                if (firstAnnotation(members, type) != null) return true;
            }
            return false;
        }

        private static <T extends java.lang.annotation.Annotation> T firstAnnotation(List<AnnotatedMember> members, Class<T> type) {
            for (AnnotatedMember member : members) {
                T ann = member.getAnnotation(type);
                if (ann != null) return ann;
            }
            return null;
        }
    }
}
