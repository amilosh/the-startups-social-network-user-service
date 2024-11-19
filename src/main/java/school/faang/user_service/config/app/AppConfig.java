package school.faang.user_service.config.app;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CsvMapper csvMapper() {
        return new CsvMapper();
    }
}
