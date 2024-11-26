package school.faang.user_service.validator.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.pojo.person.Education;
import school.faang.user_service.pojo.person.PersonFromFile;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks
    private UserValidator validator;

    @Mock
    private UserRepository userRepository;

    private PersonFromFile personFromFile;

    @BeforeEach
    public void setUp() {
        Education education = Education.builder()
                .major("major")
                .yearOfStudy(2024)
                .faculty("faculty")
                .build();
        personFromFile = PersonFromFile.builder()
                .firstName("John")
                .lastName("Doe")
                .education(education)
                .build();
    }

    @Test
    public void testValidateUserForCreateWithEmptyUserName() {
        personFromFile.setFirstName("");
        assertThrows(DataValidationException.class, () -> validator.validateUserForCreate(personFromFile));
    }

    @Test
    public void testValidateUserForCreateWithEmptyUserLastName() {
        personFromFile.setLastName("");
        assertThrows(DataValidationException.class, () -> validator.validateUserForCreate(personFromFile));
    }

    @Test
    public void testValidateUserForCreateWithEmptyFaculty() {
        personFromFile.getEducation().setFaculty("");
        assertThrows(DataValidationException.class, () -> validator.validateUserForCreate(personFromFile));
    }

    @Test
    public void testValidateUserForCreateWithEmptyYearOfStudy() {
        personFromFile.getEducation().setYearOfStudy(null);
        assertThrows(DataValidationException.class, () -> validator.validateUserForCreate(personFromFile));
    }

    @Test
    public void testValidateUserForCreateWithEmptyMajor() {
        personFromFile.getEducation().setMajor("");
        assertThrows(DataValidationException.class, () -> validator.validateUserForCreate(personFromFile));
    }

}
