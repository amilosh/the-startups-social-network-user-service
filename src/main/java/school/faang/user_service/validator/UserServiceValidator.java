package school.faang.user_service.validator;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.BanUserEvent;
import school.faang.user_service.listener.BanUserListener;
import school.faang.user_service.model.person.Person;

@Component
public class UserServiceValidator {
    public void validateBanUserEvent(BanUserEvent banUserEvent){
        if(banUserEvent.getUserId()<0){
            throw new IllegalArgumentException("Отрицательный id");
        }
    }

    public void validatePerson(Person person) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();

            if (!validator.validate(person).isEmpty()) {
                throw new ConstraintViolationException(validator.validate(person));
            }

            if (!validator.validate(person.getContactInfo()).isEmpty()) {
                throw new ConstraintViolationException(validator.validate(person.getContactInfo()));
            }

            if (!validator.validate(person.getContactInfo().getAddress()).isEmpty()) {
                throw new ConstraintViolationException(validator.validate(person.getContactInfo().getAddress()));
            }
        }
    }
}
