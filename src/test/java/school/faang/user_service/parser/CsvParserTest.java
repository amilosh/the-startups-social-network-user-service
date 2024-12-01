package school.faang.user_service.parser;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.domain.Person;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    private CsvParser csvParser;

    @Test
    void parseCsvValidCsvCorrectly() throws Exception {
        String testCsv = IOUtils.toString(ClassLoader.getSystemClassLoader()
                .getSystemResourceAsStream("students1.csv"));

        InputStream inputStream = new ByteArrayInputStream(testCsv.getBytes());
        csvParser = new CsvParser();

        List<Person> people = csvParser.parseCsv(inputStream);

        assertEquals(2, people.size());

        Person john = people.get(0);
        assertEquals("John", john.getFirstName());
        assertEquals("Doe", john.getLastName());
        assertEquals(1990, john.getYearOfBirth());
        assertEquals("Math", john.getGroup());
        assertEquals("123", john.getStudentID());
        assertEquals("Main St", john.getContactInfo().getAddress().getStreet());
        assertEquals("USA", john.getContactInfo().getAddress().getCountry());
        assertEquals("john.doe@example.com", john.getContactInfo().getEmail());
        assertEquals("Science", john.getEducation().getFaculty());
        assertTrue(john.isScholarship());

        Person jane = people.get(1);
        assertEquals("Jane", jane.getFirstName());
        assertEquals("Smith", jane.getLastName());
        assertEquals(1992, jane.getYearOfBirth());
        assertEquals("CS", jane.getGroup());
        assertFalse(jane.isScholarship());
    }

    @Test
    void parseCsv_shouldThrowExceptionForInvalidData() {
        String invalidCsvData = """
                firstName,lastName,yearOfBirth
                John,Doe,not_a_year
                """;
        InputStream inputStream = new ByteArrayInputStream(invalidCsvData.getBytes());
        csvParser = new CsvParser();

        assertThrows(NumberFormatException.class, () -> csvParser.parseCsv(inputStream));
    }
}