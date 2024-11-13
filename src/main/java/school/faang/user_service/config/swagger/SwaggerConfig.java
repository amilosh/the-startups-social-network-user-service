package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("This service is responsible for managing users, their skills, goals, subscriptions, events, and mentorships.")
                        .contact(new Contact()
                                .name("BobrAll")
                                .email("BobrAll@example.com")
                        )
                )
                .servers(List.of(new Server()
                        .url("/api/v1")
                ));
    }
}