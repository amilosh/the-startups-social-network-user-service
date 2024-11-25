package school.faang.user_service.mapper.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvParser {

    private final CsvMapper csvMapper;

    public <T> List<T> parseCsv(InputStream inputStream, Class<T> clazz) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return csvMapper.readerFor(clazz)
                .with(schema)
                .readValues(inputStream)
                .readAll().stream()
                .map(clazz::cast)
                .toList();
    }
}
