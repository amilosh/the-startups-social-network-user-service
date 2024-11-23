package school.faang.user_service.convertor;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.domain.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CsvToPojoConverter {
    private final CsvSchemaFactory csvSchemaFactory;
    private final CsvRowToPersonMapper csvRowToPersonMapper;

    public List<Person> convertCsvToPojo(InputStream csvInputStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvSchemaFactory.createPersonSchema();

        MappingIterator<Map<String, String>> rows = csvMapper
                .readerFor(Map.class)
                .with(csvSchema)
                .readValues(csvInputStream);

        List<Person> persons = new ArrayList<>();
        while (rows.hasNext()) {
            Map<String, String> row = rows.next();
            Person person = csvRowToPersonMapper.mapRowToPerson(row);
            persons.add(person);
        }
        return persons;
    }
}
