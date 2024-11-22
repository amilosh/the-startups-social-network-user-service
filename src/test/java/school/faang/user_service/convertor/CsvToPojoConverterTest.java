package school.faang.user_service.convertor;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import school.faang.user_service.dto.pojo.Person;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvToPojoConverterTest {
    @Mock
    private CsvSchemaFactory csvSchemaFactory;

    @Mock
    private CsvRowToPersonMapper csvRowToPersonMapper;

    @InjectMocks
    private CsvToPojoConverter csvToPojoConverter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConvertCsvToPojo() throws Exception {
        String csvData =  "firstName,lastName,yearOfBirth,group" + System.lineSeparator()
                + "John,Doe,1990,GroupA" + System.lineSeparator()
                + "Jane,Smith,1992,GroupB";
        InputStream csvInputStream = new ByteArrayInputStream(csvData.getBytes());
        CsvSchema mockCsvSchema = mock(CsvSchema.class);
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<String, String>> mockIterator = mock(MappingIterator.class);

        when(csvSchemaFactory.createPersonSchema()).thenReturn(mockCsvSchema);

        when(mockIterator.hasNext()).thenReturn(true, true, false);
        when(mockIterator.next()).thenReturn(
                Map.of("firstName", "John", "lastName", "Doe", "yearOfBirth", "1990", "group", "GroupA"),
                Map.of("firstName", "Jane", "lastName", "Smith", "yearOfBirth", "1992", "group", "GroupB"));

        when(csvMapper.readerFor(Map.class).with(mockCsvSchema).readValues(csvInputStream))
                .thenReturn((MappingIterator)mockIterator);

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setYearOfBirth(1990);
        person1.setGroup("GroupA");

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Smith");
        person2.setYearOfBirth(1992);
        person2.setGroup("GroupB");

        when(csvRowToPersonMapper.mapRowToPerson(any(Map.class)))
                .thenReturn(person1)
                .thenReturn(person2);


        List<Person> persons = csvToPojoConverter.convertCsvToPojo(csvInputStream);


        assertNotNull(persons);
        assertEquals(2, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
        assertEquals("Doe", persons.get(0).getLastName());
        assertEquals(1990, persons.get(0).getYearOfBirth());
        assertEquals("GroupA", persons.get(0).getGroup());

        assertEquals("Jane", persons.get(1).getFirstName());
        assertEquals("Smith", persons.get(1).getLastName());
        assertEquals(1992, persons.get(1).getYearOfBirth());
        assertEquals("GroupB", persons.get(1).getGroup());
    }

    @Test
    public void testConvertCsvToPojo_emptyFile() throws Exception {

        String csvData = "";
        InputStream csvInputStream = new ByteArrayInputStream(csvData.getBytes());

        List<Person> persons = csvToPojoConverter.convertCsvToPojo(csvInputStream);

        assertNotNull(persons);
        assertTrue(persons.isEmpty(), "List of persons should be empty");
    }
}

