package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.pojo.person.Person;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public User validateUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id {} not found", userId);
            return new NoSuchElementException(String.format("There isn't user with id = %d", userId));
        });
    }

    public void validateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DataValidationException("List Ids shouldn't be empty");
        }
    }

    public void validateUserForCreate(Person person) {
        String username = person.getFirstName();
        String lastName = person.getLastName();
        String faculty = person.getEducation().getFaculty();
        Integer yearOfStudy = person.getEducation().getYearOfStudy();
        String major = person.getEducation().getMajor();
        log.info("Trying to convert Person to User: username {}, lastName {}," +
                " faculty {}, yearOfStudy {}, major {} ", username, lastName, faculty, yearOfStudy, major);
        if (username.isEmpty()) {
            throw new DataValidationException("FirstName shouldn't be empty");
        }
        if (lastName.isEmpty()) {
            throw new DataValidationException("LastName shouldn't be empty");
        }
        if (faculty.isEmpty()) {
            throw new DataValidationException("Faculty shouldn't be empty");
        }
        if (yearOfStudy == null) {
            throw new DataValidationException("YearOfStudy shouldn't be empty");
        }
        if (major.isEmpty()) {
            throw new DataValidationException("Major shouldn't be empty");
        }
    }

}