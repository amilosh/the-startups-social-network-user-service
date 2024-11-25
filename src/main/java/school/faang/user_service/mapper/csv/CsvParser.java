package school.faang.user_service.mapper.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvParser {

    private final CsvMapper csvMapper;

    public <T> List<T> parseCsv(MultipartFile file, Class<T> clazz) {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try {
            return csvMapper.readerFor(clazz)
                    .with(schema)
                    .readValues(file.getInputStream())
                    .readAll()
                    .stream()
                    .map(clazz::cast)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error when read csv file", e);
        }
    }
}
