package it.pagopa.interop.common.kernel.dto;

import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class ConfigurableTextSizeValidator
        implements ConstraintValidator<ConfigurableTextSize, ConfigurableText> {
    private int max;

    @Override
    public void initialize(ConfigurableTextSize constraint) {
        max = constraint.max();
        if (max <= 0) {
            throw new ConstraintDeclarationException("ConfigurableTextSize.max must be positive");
        }
    }

    @Override
    public boolean isValid(ConfigurableText text, ConstraintValidatorContext context) {
        if (text == null || text.value() == null || text.mode() == null) {
            return true;
        }
        long requiredLength = text.value().length()
                + (text.mode() == TextGenerationMode.RANDOM ? 2L : 0L);
        return requiredLength <= max;
    }
}
