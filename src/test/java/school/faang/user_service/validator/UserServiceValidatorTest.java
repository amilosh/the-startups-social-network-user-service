package school.faang.user_service.validator;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.person.Person;
import school.faang.user_service.model.person.contact.Address;
import school.faang.user_service.model.person.contact.ContactInfo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceValidatorTest {

    private UserServiceValidator validator = new UserServiceValidator();

    @Test
    void validatePersonSuccess() {
        assertDoesNotThrow(() -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
    }

    @Test
    void validatePerson_blankOrNullFirstName() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName(null)
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("firstName"));
    }

    @Test
    void validatePerson_blankOrNullLastName() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName(null)
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("lastName"));
    }

    @Test
    void validatePerson_blankOrNullEmail() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email(null)
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    void validatePerson_blankOrNullPhone() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone(null)
                        .address(Address.builder()
                                .city("city")
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("phone"));
    }

    @Test
    void validatePerson_blankOrNullCity() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city(null)
                                .country("country")
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("city"));
    }

    @Test
    void validatePerson_blankOrNullCountry() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> validator.validatePerson(Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .country(null)
                                .build())
                        .build())
                .build()
        ));
        assertTrue(exception.getMessage().contains("country"));
    }
}