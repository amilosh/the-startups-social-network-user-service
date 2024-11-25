package school.faang.user_service.validator;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.person.Person;
import school.faang.user_service.model.person.contact.Address;
import school.faang.user_service.model.person.contact.ContactInfo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals("firstName: не должно быть пустым", exception.getMessage());
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
        assertEquals("lastName: не должно быть пустым", exception.getMessage());
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
        assertEquals("email: не должно быть пустым", exception.getMessage());
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
        assertEquals("phone: не должно быть пустым", exception.getMessage());
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
        assertEquals("city: не должно быть пустым", exception.getMessage());
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
        assertEquals("country: не должно быть пустым", exception.getMessage());
    }
}