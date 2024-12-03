package school.faang.user_service.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CsvParser {
    public List<Person> parseCsv(InputStream inputStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> it = csvMapper.readerFor(Map.class)
                .with(schema).readValues(inputStream);

        List<Person> people = new ArrayList<>();

        while (it.hasNext()) {
            Map<String, String> row = it.next();
            Address address = new Address(
                    row.get("street"),
                    row.get("city"),
                    row.get("state"),
                    row.get("country"),
                    row.get("postalCode")
            );

            ContactInfo contactInfo = new ContactInfo(
                    row.get("email"),
                    row.get("phone"),
                    address
            );

            Person person = Person.builder()
                    .firstName(row.get("firstName"))
                    .lastName(row.get("lastName"))
                    .yearOfBirth(Integer.parseInt(row.get("yearOfBirth")))
                    .group(row.get("group"))
                    .studentID(row.get("studentID"))
                    .contactInfo(contactInfo)
                    .education(Education.builder()
                            .faculty(row.get("faculty"))
                            .yearOfStudy(Integer.parseInt(row.get("yearOfStudy")))
                            .major(row.get("major"))
                            .GPA(Double.parseDouble(row.get("GPA")))
                            .build())
                    .status(row.get("status"))
                    .admissionDate(LocalDate.parse(row.get("admissionDate")))
                    .graduationDate(LocalDate.parse(row.get("graduationDate")))
                    .scholarship(Boolean.parseBoolean(row.get("scholarship")))
                    .employer(row.get("employer"))
                    .build();
            people.add(person);
        }
        return people;
    }
}
