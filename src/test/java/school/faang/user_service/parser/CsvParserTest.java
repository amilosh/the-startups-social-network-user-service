package school.faang.user_service.parser;

import org.junit.jupiter.api.Test;
import school.faang.user_service.domain.Person;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    private CsvParser csvParser;

    @Test
    void parseCsvValidCsvCorrectly() throws Exception {
        String csvData = """
                firstName,lastName,yearOfBirth,group,studentID,street,city,state,country,postalCode,email,phone,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,scholarship,employer
                John,Doe,1990,Math,123,Main St,Anytown,CA,USA,90210,john.doe@example.com,555-1234,Science,2,Physics,3.8,active,2020-01-01,2024-01-01,true,Acme Corp
                Jane,Smith,1992,CS,456,Second St,Othercity,NY,USA,10001,jane.smith@example.com,555-5678,Engineering,3,CS,3.9,active,2019-01-01,2023-01-01,false,Tech Inc
                """;

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
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