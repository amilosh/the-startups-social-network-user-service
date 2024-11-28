package school.faang.user_service.config.app;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Value("${threadPool.size}")
    private int poolSize;

    @Bean
    public CsvMapper csvMapper() {
        return new CsvMapper();
    }

    @Bean
    public ExecutorService fixedThreadPool(){
        return Executors.newFixedThreadPool(poolSize);
    }
}
