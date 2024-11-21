package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.pojo.user.Person;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserExistence(boolean isExist) {
        if (!isExist) {
            throw new EntityNotFoundException("User not exists");
        }
    }

    public void validateAllPersons(List<Person> persons) {
        log.info("Starting validation of {} persons", persons.size());
        List<String> errors = new ArrayList<>();

        for (Person person : persons) {
            log.debug("Validating person with email: {} and phone: {}", person.getEmail(), person.getPhone());

            boolean isDataUnique = isPersonDataUnique(person.getEmail(), person.getPhone());
            if (!isDataUnique) {
                String errorMessage = "Invalid data for user: " + person.getEmail() + " " + person.getPhone();
                errors.add(errorMessage);
                log.warn("Validation failed for person: {}", errorMessage);
            }
        }

        if (!errors.isEmpty()) {
            log.error("Validation failed: {}", errors);
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
        log.info("Validation successful for all persons.");
    }

    public boolean isPersonDataUnique(String email, String phone) {
        if (email == null || email.isEmpty()) {
            log.debug("Email is null or empty, considering it not unique.");
            return false;
        }

        log.debug("Checking if email '{}' is unique.", email);
        boolean isEmailUnique = userRepository.findByEmail(email).isEmpty();
        log.debug("Email '{}' is unique: {}", email, isEmailUnique);

        if (phone == null || phone.isEmpty()) {
            log.debug("Phone is null or empty, considering it not unique.");
            return false;
        }

        log.debug("Checking if phone '{}' is unique.", phone);
        boolean isPhoneUnique = userRepository.findByPhone(phone).isEmpty();
        log.debug("Phone '{}' is unique: {}", phone, isPhoneUnique);

        return isEmailUnique && isPhoneUnique;
    }

}
