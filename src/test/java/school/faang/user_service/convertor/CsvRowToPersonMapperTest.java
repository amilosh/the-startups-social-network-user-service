package school.faang.user_service.convertor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.pojo.Person;
import school.faang.user_service.dto.pojo.PreviousEducation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
//@ExtendWith(MockitoExtension.class)
class CsvRowToPersonMapperTest {
    private CsvRowToPersonMapper csvRowToPersonMapper;

    @BeforeEach
    void setUp() {
        csvRowToPersonMapper = new CsvRowToPersonMapper();
    }
    @Test
    void testMapRowToPersonSuccess() {

        Map<String, String> row = new HashMap<>();
        row.put("firstName", "John");
        row.put("lastName", "Doe");
        row.put("yearOfBirth", "1990");
        row.put("group", "A1");
        row.put("studentID", "12345");
        row.put("status", "active");
        row.put("admissionDate", "2020-09-01");
        row.put("graduationDate", "2024-06-01");
        row.put("scholarship", "true");
        row.put("employer", "TechCorp");

        row.put("street", "Main St");
        row.put("city", "Springfield");
        row.put("state", "IL");
        row.put("country", "USA");
        row.put("postalCode", "62704");

        row.put("faculty", "Engineering");
        row.put("yearOfStudy", "4");
        row.put("major", "Computer Science");

        row.put("degree", "Bachelor");
        row.put("institution", "MIT");
        row.put("completionYear", "2016");


        Person person = csvRowToPersonMapper.mapRowToPerson(row);


        assertNotNull(person);
        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
        assertEquals(1990, person.getYearOfBirth());
        assertEquals("A1", person.getGroup());
        assertEquals("12345", person.getStudentID());
        assertEquals("active", person.getStatus());
        assertEquals(LocalDate.of(2020, 9, 1), person.getAdmissionDate());
        assertEquals(LocalDate.of(2024, 6, 1), person.getGraduationDate());
        assertTrue(person.isScholarship());
        assertEquals("TechCorp", person.getEmployer());

        assertNotNull(person.getContactInfo());
        assertEquals("Main St", person.getContactInfo().getAddress().getStreet());
        assertEquals("Springfield", person.getContactInfo().getAddress().getCity());
        assertEquals("IL", person.getContactInfo().getAddress().getState());
        assertEquals("USA", person.getContactInfo().getAddress().getCountry());
        assertEquals("62704", person.getContactInfo().getAddress().getPostalCode());

        assertNotNull(person.getEducation());
        assertEquals("Engineering", person.getEducation().getFaculty());
        assertEquals(4, person.getEducation().getYearOfStudy());
        assertEquals("Computer Science", person.getEducation().getMajor());

        assertNotNull(person.getPreviousEducation());
        assertEquals(1, person.getPreviousEducation().size());
        PreviousEducation prevEdu = person.getPreviousEducation().get(0);
        assertEquals("Bachelor", prevEdu.getDegree());
        assertEquals("MIT", prevEdu.getInstitution());
        assertEquals(2016, prevEdu.getCompletionYear());
    }
}
