package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class PersonToUserMapperTest {
    private PersonToUserMapper personToUserMapper;

    @BeforeEach
    void setUp() {
        personToUserMapper = Mappers.getMapper(PersonToUserMapper.class);
    }

    @Test
    void testPersonToUserMapping() {

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("johndoe@example.com");
        contactInfo.setPhone("123456789");

        Address address = new Address();
        address.setCity("New York");
        address.setCountry("USA");
        address.setState("NY");
        contactInfo.setAddress(address);

        person.setContactInfo(contactInfo);

        Education education = new Education();
        education.setFaculty("Computer Science");
        education.setYearOfStudy(4);
        education.setMajor("Software Engineering");
        person.setEducation(education);
        person.setEmployer("TechCorp");

        User user = personToUserMapper.personToUser(person);

        assertEquals("JohnDoe", user.getUsername());
        assertEquals("johndoe@example.com", user.getEmail());
        assertEquals("123456789", user.getPhone());
        assertEquals("New York", user.getCity());
        assertEquals("USA", user.getCountry().getTitle());

        String expectedAboutMe = "NY Computer Science 4 Software Engineering at TechCorp";
        assertEquals(expectedAboutMe, user.getAboutMe());
    }
}
