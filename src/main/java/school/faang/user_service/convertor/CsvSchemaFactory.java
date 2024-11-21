package school.faang.user_service.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.pojo.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class CsvSchemaFactory {
    public CsvSchema createPersonSchema() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        CsvMapper csvMapper = new CsvMapper();
        return CsvSchema.builder()
                .addColumn("firstName")
                .addColumn("lastName")
                .addColumn("yearOfBirth")
                .addColumn("group")
                .addColumn("studentID")
                .addColumn("email")
                .addColumn("phone")
                .addColumn("street")
                .addColumn("city")
                .addColumn("state")
                .addColumn("country")
                .addColumn("postalCode")
                .addColumn("faculty")
                .addColumn("yearOfStudy")
                .addColumn("major")
                .addColumn("GPA")
                .addColumn("status")
                .addColumn("admissionDate")
                .addColumn("graduationDate")
                .addColumn("degree")
                .addColumn("institution")
                .addColumn("completionYear")
                .addColumn("scholarship")
                .addColumn("employer")
                .setColumnSeparator(',')
                .build().withHeader();
    }
}
