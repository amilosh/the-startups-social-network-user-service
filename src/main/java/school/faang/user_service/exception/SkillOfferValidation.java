package school.faang.user_service.service.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.Set;

public class SkillOfferValidation {
    public void validate(SkillOfferDto skillOffer) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<SkillOfferDto>> violations = validator.validate(skillOffer);
            if (!violations.isEmpty()) {
                throw new DataValidationException("Validation failed: " + violations);
            }
        }
    }
}
